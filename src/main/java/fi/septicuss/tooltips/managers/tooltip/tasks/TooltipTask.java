package fi.septicuss.tooltips.managers.tooltip.tasks;

import fi.septicuss.tooltips.managers.preset.Preset;
import fi.septicuss.tooltips.managers.preset.actions.ActionProperties;
import fi.septicuss.tooltips.managers.preset.actions.command.ActionCommands;
import fi.septicuss.tooltips.managers.preset.show.ShowProperties;
import fi.septicuss.tooltips.managers.title.TitleBuilder;
import fi.septicuss.tooltips.managers.tooltip.TooltipManager;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.CooldownType;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.PlayerTooltipData;
import fi.septicuss.tooltips.utils.cache.tooltip.TooltipCache;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

/**
 * Manages:
 * - changes between currently shown tooltip (transitions)
 * - animations
 */
public class TooltipTask extends BukkitRunnable {

    private static final int ACTIVE_PRESET_DURATION_TICKS = 40;
    private static final int EXTRA_DELAY_TICKS = 3;

    private final TooltipManager manager;

    public TooltipTask(TooltipManager tooltipManager) {
        this.manager = tooltipManager;
    }

    @Override
    public void run() {

        var onlinePlayers = Bukkit.getOnlinePlayers();
        if (onlinePlayers.isEmpty())
            return;

        outer: for (Player player : onlinePlayers) {
            final PlayerTooltipData data = manager.getPlayerTooltipData(player);

            final boolean hasCurrentPreset = data.hasCurrentPreset();
            final boolean hasDisplayedPreset = data.hasDisplayedPreset();
            final boolean hasBoth = (hasCurrentPreset && hasDisplayedPreset);
            final boolean currentSameAsDisplayed = hasBoth && data.getCurrentPreset().equals(data.getDisplayedPreset());

            /* --- Displaying same preset --- */

            if (currentSameAsDisplayed) {
                final Preset preset = manager.getPresets().get(data.getDisplayedPreset());
                final ArrayList<String> text = process(player, preset.getText());

                final boolean textChanged = data.hasDisplayedText() && !(data.getDisplayedText().equals(text));

                // Text, but not the preset changed, redisplay
                if (textChanged) {
                    final var copy = new ArrayList<>(data.getDisplayedText());

                    data.setSavedText(copy);
                    data.setTextChanged(true);

                    hide(player, data.getDisplayedPreset(), preset);

                    data.setDisplayedText(text);
                    data.setDisplayedPreset(null);
                    continue;
                }

                data.setTextChanged(false);
                data.setSavedText(null);
                display(player, data.getDisplayedPreset(), preset);
                continue;
            }

            /* --- Displaying new preset --- */

            // Hide previous preset, if present
            if (hasDisplayedPreset) {
                final Preset preset = manager.getPresets().get(data.getDisplayedPreset());
                hide(player, data.getDisplayedPreset(), preset);
                data.setDisplayedPreset(null);
            }

            // Waiting until hidden
            for (var cooldownType : CooldownType.values()) {
                if (data.hasCooldown(cooldownType)) {
                    continue outer;
                }
            }

            // Start displaying the current preset
            data.setDisplayedPreset(data.getCurrentPreset());
            data.setFirstTime(true);
            data.setTextChanged(false);

        }

    }

    // TODO: Actions
    public void runActions(ActionProperties.TooltipAction action, Player player) {
        final PlayerTooltipData data = manager.getPlayerTooltipData(player);

        if (!data.hasDisplayedPreset()) {
            return;
        }

        final String presetId = data.getDisplayedPreset();
        final Preset preset = manager.getPresets().get(presetId);

        if (!preset.getActionProperties().hasAnyActions()) {
            return;
        }

        final ActionProperties actions = preset.getActionProperties();

        if (!actions.hasAction(action)) {
            return;
        }

        final List<String> commands = Placeholders.replacePlaceholders(player, actions.getCommandsForAction(action));
        commands.forEach(command -> {
            final String consoleCommand = command.replace("%player%", player.getName());

            if (ActionCommands.isValidCommand(consoleCommand)) {
                ActionCommands.runCommand(player, presetId, consoleCommand);
                return;
            }

            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand);
        });

    }

    private void display(Player player, String presetId, Preset preset) {
        final PlayerTooltipData data = manager.getPlayerTooltipData(player);
        final ShowProperties properties = preset.getShowProperties();
        final boolean active = properties.isActive();

        // Already being displayed
        for (var cooldownType : CooldownType.values()) {
            if (data.hasCooldown(cooldownType)) {
                return;
            }
        }

        final var text = process(player, preset.getText());
        data.setDisplayedText(text);

        final TitleBuilder titleBuilder = getTooltipTitle(player, data, preset);

        /* --- Active preset --- */
        if (active) {
            final boolean firstTime = data.isFirstTime();

            // Add fade-in only for first time
            if (firstTime) {
                final int fadeIn = preset.getFadeIn();

                titleBuilder.setFadeIn(fadeIn);
                data.addCooldown(CooldownType.FADE_IN, fadeIn);
                data.setFirstTime(false);
            } else {
                titleBuilder.setFadeIn(0);
            }

            // Re-send every duration
            int stay = ACTIVE_PRESET_DURATION_TICKS;

            titleBuilder.setStay(stay + EXTRA_DELAY_TICKS);
            titleBuilder.build().send(player);

            data.addCooldown(CooldownType.STAY, stay - EXTRA_DELAY_TICKS);

            if (firstTime && !data.hasTextChanged()) {
                runActions(ActionProperties.TooltipAction.ON_SHOW, player);
            }

            return;
        }

        /* --- Inactive preset --- */

        if (!data.isFirstTime()) {
            return;
        }

        int fadeIn = preset.getFadeIn();
        int stay = preset.getStay();
        int fadeOut = preset.getFadeOut();
        int total = fadeIn + stay + fadeOut;

        titleBuilder.setFadeIn(fadeIn);
        titleBuilder.setStay(stay);
        titleBuilder.setFadeOut(fadeOut);
        titleBuilder.build().send(player);

        data.addCooldown(CooldownType.FADE_OUT, total);
        data.setFirstTime(false);

        if (!data.hasTextChanged()) {
            runActions(ActionProperties.TooltipAction.ON_SHOW, player);
        }

    }

    private void hide(Player player, String presetId, Preset preset) {
        final PlayerTooltipData data = manager.getPlayerTooltipData(player);
        final ShowProperties properties = preset.getShowProperties();
        final boolean active = properties.isActive();

        /* --- Active preset --- */
        if (active) {
            final TitleBuilder titleBuilder = this.getTooltipTitle(player, data, preset);

            final int fadeOut = preset.getFadeOut();
            final boolean hasFadeOut = fadeOut > 0;
            final int stay = (data.hasTextChanged() ? 10 : 0);

            titleBuilder.setFadeIn(0);
            titleBuilder.setStay(stay);
            titleBuilder.setFadeOut(fadeOut);
            titleBuilder.build().send(player);

            if (hasFadeOut) {
                data.addCooldown(CooldownType.FADE_OUT, fadeOut);
            }

            // Presets own cooldown
            if (properties.hasCooldown()) {
                data.addCooldown(CooldownType.COOLDOWN, properties.getCooldown());
            }

            if (!data.hasTextChanged()) {
                runActions(ActionProperties.TooltipAction.ON_STOP_SHOWING, player);
            }

            data.removeCooldown(CooldownType.STAY);
            data.setDisplayedText(null);
            return;
        }

        /* --- Inactive preset --- */

        if (properties.hasCooldown()) {
            data.addCooldown(CooldownType.COOLDOWN, properties.getCooldown());
        }

        if (!data.hasTextChanged()) {
            runActions(ActionProperties.TooltipAction.ON_STOP_SHOWING, player);
        }

        data.removeCooldown(CooldownType.STAY);
        data.setDisplayedText(null);
    }

    private TitleBuilder getTooltipTitle(Player player, PlayerTooltipData data, Preset preset) {
        final boolean savedText = data.hasSavedText();
        final boolean displayedText = data.hasDisplayedText();

        final ArrayList<String> text = (
                savedText ? data.getSavedText() :
                        displayedText ? data.getDisplayedText() : process(player, preset.getText())
        );

        TitleBuilder builder;

        if (TooltipCache.contains(player, text)) {
            builder = TooltipCache.get(player, text);
        } else {
            builder = new TitleBuilder(manager.getTitleManager());
            builder.setSubtitle(manager.getTooltip(player, preset, text).getComponents());
            TooltipCache.cache(player, text, builder.clone());
        }

        return builder;
    }

    private ArrayList<String> process(Player player, List<String> text) {
        return (ArrayList<String>) Placeholders.replacePlaceholders(player, text);
    }

}
