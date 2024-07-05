package fi.septicuss.tooltips.tooltip.runnable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.comphenix.protocol.ProtocolManager;

import fi.septicuss.tooltips.managers.preset.Preset;
import fi.septicuss.tooltips.managers.preset.actions.ActionProperties;
import fi.septicuss.tooltips.managers.preset.actions.ActionProperties.TooltipAction;
import fi.septicuss.tooltips.managers.preset.actions.command.ActionCommands;
import fi.septicuss.tooltips.managers.preset.condition.StatementHolder;
import fi.septicuss.tooltips.managers.preset.show.ShowProperties;
import fi.septicuss.tooltips.managers.title.TitleBuilder;
import fi.septicuss.tooltips.tooltip.Tooltip;
import fi.septicuss.tooltips.tooltip.TooltipManager;
import fi.septicuss.tooltips.tooltip.runnable.TooltipData.CooldownType;
import fi.septicuss.tooltips.utils.cache.tooltip.TooltipCache;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;

public class TooltipRunnable extends BukkitRunnable {

	// For now, it just works
	private static final int MAGIC_NUMBER = 3;

	// As to not send an active tooltip every checkFrequency of ticks
	private static final int ACTIVE_PRESET_DURATION = 40;

	private TooltipManager tooltipManager;
	private ProtocolManager protocolManager;

	private Map<UUID, TooltipData> dataMap = new HashMap<>();

	private Map<String, Preset> presets;
	private Map<String, StatementHolder> holders;

	public TooltipRunnable(TooltipManager tooltipManager, ProtocolManager protocolManager, Map<String, Preset> presets,
			Map<String, StatementHolder> holders, int checkFrequency) {
		this.tooltipManager = tooltipManager;
		this.protocolManager = protocolManager;

		this.presets = presets;
		this.holders = holders;
	}

	@Override
	public void run() {

		var onlinePlayers = Bukkit.getOnlinePlayers();
		if (onlinePlayers.isEmpty())
			return;

		outer: for (Player player : onlinePlayers) {

			for (var holderEntry : holders.entrySet()) {

				var id = holderEntry.getKey();
				var holder = holderEntry.getValue();

				boolean result = holder.evaluate(player);
				Preset preset = presets.get(id);

				if (result) {
					TooltipData data = getPlayerData(player);

					// Check if the text of the current preset has changed
					if (data.hasCurrentText() && data.getCurrentPresetId().equals(id) && preset.getShowProperties().isActive()) {

						if (!data.getCurrentText().equals(Placeholders.replacePlaceholders(player, preset.getText()))) {
							// Text has changed
							data.setTextJustUpdated(true);
							
							handleFalse(player, id, preset, preset.getShowProperties());

							data.removeCooldown(CooldownType.FADE_IN);
							data.removeCooldown(CooldownType.FADE_OUT);

							if (preset.getFadeOut() == 0)
								data.removeCooldown(CooldownType.STAY);

							return;
						} else {
							// Text has not changed
							handleTrue(player, id, preset, preset.getShowProperties());
							data.setTextJustUpdated(false);
						}
					} else {
						// We're handling a new preset
						handleTrue(player, id, preset, preset.getShowProperties());
						data.setTextJustUpdated(false);
					}

					continue outer;
				} else {
					getPlayerData(player).setTextJustUpdated(false);
					handleFalse(player, id, preset, preset.getShowProperties());
				}

			}

		}

	}

	// -------------
	// EXTERNAL
	// -------------

	public void runActions(TooltipAction action, Player player) {
		TooltipData data = getPlayerData(player);

		if (!data.hasCurrentPreset())
			return;

		String presetId = data.getCurrentPresetId();
		Preset preset = presets.get(presetId);

		if (!preset.getActionProperties().hasAnyActions())
			return;

		ActionProperties actions = preset.getActionProperties();

		if (!actions.hasAction(action))
			return;

		List<String> commands = Placeholders.replacePlaceholders(player, actions.getCommandsForAction(action));

		commands.forEach(command -> {
			final String consoleCommand = command.replace("%player%", player.getName());
			
			if (ActionCommands.isValidCommand(consoleCommand)) {
				ActionCommands.runCommand(player, presetId, consoleCommand);
				return;
			}
			
			Bukkit.dispatchCommand(Bukkit.getConsoleSender(), consoleCommand);
		});
	}

	public void clearData() {
		dataMap.clear();
	}

	// -------------
	// HANDLERS
	// -------------

	// TRUE
	private void handleTrue(Player player, String presetId, Preset preset, ShowProperties properties) {

		TooltipData data = getPlayerData(player);

		if (data.hasCurrentPreset() && !data.getCurrentPresetId().equals(presetId)) {
			String previousId = data.getCurrentPresetId();
			Preset previousPreset = presets.get(previousId);
			
			data.setTextJustUpdated(false);
			handleFalse(player, previousId, previousPreset, previousPreset.getShowProperties());
			return;
		}

		// Has already been shown once
		if (data.hasCurrentPreset() && !properties.isActive()) {
			if (data.hasCooldown(CooldownType.FADE_OUT))
				return;
			return;
		}

		// If has any type of cooldown
		for (CooldownType cooldownType : CooldownType.values())
			if (data.hasCooldown(cooldownType)) {
				return;
			}

		List<String> rawText = Placeholders.replacePlaceholders(player, preset.getText());
		data.setCurrentText(rawText);

		TitleBuilder builder;

		if (TooltipCache.contains(player, rawText)) {
			builder = TooltipCache.get(player, rawText).clone();
		} else {
			Tooltip tooltip = tooltipManager.getTooltip(player, preset, null);
			builder = new TitleBuilder(protocolManager);
			builder.setSubtitle(tooltip.getComponents());
			TooltipCache.cache(player, rawText, builder.clone());
		}

		if (properties.isActive()) {

			boolean firstTime = false;

			// Did not start showing preset yet, set fade in first
			if (!data.hasCurrentPreset()) {
				int fadeIn = preset.getFadeIn();

				builder.setFadeIn(fadeIn);
				data.addCooldown(CooldownType.FADE_IN, fadeIn);

				firstTime = true;
			}

			// Send every
			int activeStay = ACTIVE_PRESET_DURATION;

			builder.setStay(activeStay + MAGIC_NUMBER);
			builder.build().send(player);

			data.addCooldown(CooldownType.STAY, activeStay - MAGIC_NUMBER);
			data.setCurrentPreset(presetId);

			if (firstTime && !data.hasTextJustUpdated())
				runActions(TooltipAction.ON_SHOW, player);

		} else {

			// Send once
			int fadeIn = preset.getFadeIn();
			int stay = preset.getStay();
			int fadeOut = preset.getFadeOut();
			int total = fadeIn + stay + fadeOut;

			builder.setFadeIn(fadeIn);
			builder.setStay(stay);
			builder.setFadeOut(fadeOut);
			builder.build().send(player);

			data.addCooldown(CooldownType.FADE_OUT, total);
			data.setCurrentPreset(presetId);

			if (!data.hasTextJustUpdated())
				runActions(TooltipAction.ON_SHOW, player);

		}

	}

	// FALSE
	private void handleFalse(Player player, String presetId, Preset preset, ShowProperties properties) {

		TooltipData data = getPlayerData(player);

		// Not relevant
		if (!data.hasCurrentPreset() || !data.getCurrentPresetId().equals(presetId)) {
			return;
		}

		if (properties.isActive()) {

			// Active

			if (data.hasCooldown(CooldownType.FADE_OUT))
				return;

			List<String> rawText;
			TitleBuilder builder;

			if (data.hasCurrentText())
				rawText = data.getCurrentText();
			else
				rawText = Placeholders.replacePlaceholders(player, preset.getText());

			if (TooltipCache.contains(player, rawText)) {
				builder = TooltipCache.get(player, rawText).clone();
			} else {
				Tooltip tooltip = tooltipManager.getTooltip(player, preset, null);
				builder = new TitleBuilder(protocolManager);
				builder.setSubtitle(tooltip.getComponents());
			}

			// TODO: Maybe?
			// TooltipCache.remove(player);

			int remainingTicks = MAGIC_NUMBER;
			int fadeOut = preset.getFadeOut();
			boolean hasFadeOut = fadeOut > 0;

			// Has not finished fading in yet
			builder.setStay(hasFadeOut ? remainingTicks : remainingTicks + MAGIC_NUMBER);
			builder.setFadeOut(fadeOut);
			builder.build().send(player);

			if (hasFadeOut)
				data.addCooldown(CooldownType.FADE_OUT, fadeOut + remainingTicks + MAGIC_NUMBER);

			data.addCooldown(CooldownType.STAY, remainingTicks);

			if (properties.hasCooldown())
				data.addCooldown(CooldownType.COOLDOWN, properties.getCooldown());

			if (!data.hasTextJustUpdated())
				runActions(TooltipAction.ON_STOP_SHOWING, player);
			
			data.setCurrentPreset(null);
			data.setCurrentText(null);
			return;

		} else {

			// Inactive

			if (data.hasCooldown(CooldownType.FADE_OUT))
				return;

			if (properties.hasCooldown())
				data.addCooldown(CooldownType.COOLDOWN, properties.getCooldown());

			if (!data.hasTextJustUpdated())
				runActions(TooltipAction.ON_STOP_SHOWING, player);

			data.removeCooldown(CooldownType.STAY);
			data.setCurrentPreset(null);
			data.setCurrentText(null);
			return;

		}

	}

	private TooltipData getPlayerData(Player player) {
		final UUID uuid = player.getUniqueId();

		if (dataMap.containsKey(uuid))
			return dataMap.get(uuid);

		TooltipData data = new TooltipData(player);
		dataMap.put(uuid, data);
		return data;
	}

}
