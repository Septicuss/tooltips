package fi.septicuss.tooltips.api;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.ConditionManager;
import fi.septicuss.tooltips.managers.preset.Preset;
import fi.septicuss.tooltips.managers.preset.functions.Function;
import fi.septicuss.tooltips.managers.preset.functions.Functions;
import fi.septicuss.tooltips.managers.theme.Theme;
import fi.septicuss.tooltips.managers.title.TitleBuilder;
import fi.septicuss.tooltips.managers.tooltip.Tooltip;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class TooltipsAPI {

    private static final Set<Condition> conditionQueue = new HashSet<>();

    public static void addCondition(@Nonnull Condition condition) {
        if (Tooltips.get() == null || Tooltips.get().getConditionManager() == null) {
            conditionQueue.add(condition);
            return;
        }

        Tooltips.get().getConditionManager().register(condition);
    }

    // Internal
    public static Set<Condition> getConditionQueue() {
        return conditionQueue;
    }

    public static void removeCondition(@Nonnull String name) {
        Tooltips.get().getConditionManager().unregister(name);
    }

    public static Set<String> getConditions() {
        return Tooltips.get().getConditionManager().getConditions();
    }

    public static void addFunction(String name, Function function) {
        Functions.add(name, function);
    }

    public static void removeFunction(String name, Function function) {
        Functions.remove(name);
    }

    public static void runAction(@Nonnull String action, @Nonnull Player player) {
        Tooltips.get().getTooltipManager().runActions(action, player);
    }

    public static void sendTooltip(Player player, Preset preset) {
        if (player == null) throw new NullPointerException("Player cannot be null");
        if (preset == null) throw new NullPointerException("Preset cannot be null");
        sendTooltipTitle(player, preset, null);
    }

    public static void sendTooltip(Player player, Preset preset, List<String> override) {
        if (player == null) throw new NullPointerException("Player cannot be null");
        if (preset == null) throw new NullPointerException("Preset cannot be null");
        sendTooltipTitle(player, preset, override);
    }

    public static void sendTooltip(Player player, Theme theme, List<String> override) {
        if (player == null) throw new NullPointerException("Player cannot be null");
        if (theme == null) throw new NullPointerException("Theme cannot be null");
        sendTooltipTitle(player, theme, override);
    }

    public static Tooltip getTooltip(Player player, Preset preset, @Nullable List<String> override) {
        if (player == null) throw new NullPointerException("Player cannot be null");
        if (preset == null) throw new NullPointerException("Preset cannot be null");
        return Tooltips.get().getTooltipManager().getTooltip(player, preset, override);
    }

    public static Tooltip getTooltip(Player player, Theme theme, @Nullable List<String> override) {
        if (player == null) throw new NullPointerException("Player cannot be null");
        if (theme == null) throw new NullPointerException("Theme cannot be null");
        return Tooltips.get().getTooltipManager().getTooltip(player, theme, override);
    }

    public static boolean doesThemeExist(String id) {
        return Tooltips.get().getThemeManager().doesThemeExist(id);
    }

    public static @Nullable Theme getTheme(String id) {
        return Tooltips.get().getThemeManager().getTheme(id);
    }

    public static Set<String> getThemeIds() {
        return Collections.unmodifiableSet(Tooltips.get().getThemeManager().getThemes().keySet());
    }

    public static boolean doesPresetExist(String id) {
        return Tooltips.get().getPresetManager().doesPresetExist(id);
    }

    public static @Nullable Preset getPreset(String id) {
        return Tooltips.get().getPresetManager().getPreset(id);
    }

    public static Set<String> getPresetIds() {
        return Collections.unmodifiableSet(Tooltips.get().getPresetManager().getPresets().keySet());
    }

    private static void sendTooltipTitle(Player player, Preset preset, List<String> extra) {
        Tooltip tooltip = Tooltips.get().getTooltipManager().getTooltip(player, preset, extra);

        var title = new TitleBuilder(Tooltips.get().getTitleManager())
                .setSubtitle(tooltip.getComponent())
                .setFadeIn(preset.getFadeIn())
                .setStay(preset.getStay())
                .setFadeOut(preset.getFadeOut())
                .build();

        title.ifPresent(value -> value.send(player));
    }

    private static void sendTooltipTitle(Player player, Theme theme, List<String> extra) {
        Tooltip tooltip = Tooltips.get().getTooltipManager().getTooltip(player, theme, extra);

        var title = new TitleBuilder(Tooltips.get().getTitleManager())
                .setSubtitle(tooltip.getComponent())
                .setFadeIn(0)
                .setStay(5 * 20)
                .setFadeOut(0)
                .build();

        title.ifPresent(value -> value.send(player));
    }

}
