package fi.septicuss.tooltips.managers.tooltip;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.condition.StatementHolder;
import fi.septicuss.tooltips.managers.preset.Preset;
import fi.septicuss.tooltips.managers.preset.PresetManager;
import fi.septicuss.tooltips.managers.preset.actions.ActionProperties.TooltipAction;
import fi.septicuss.tooltips.managers.theme.Theme;
import fi.septicuss.tooltips.managers.title.TitleManager;
import fi.septicuss.tooltips.managers.tooltip.building.TooltipBuilder;
import fi.septicuss.tooltips.managers.tooltip.tasks.ConditionTask;
import fi.septicuss.tooltips.managers.tooltip.tasks.TooltipTask;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.PlayerTooltipData;
import org.bukkit.entity.Player;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TooltipManager {

	// Managers
	private final Tooltips plugin;
	private final TooltipBuilder tooltipBuilder;
	private final TitleManager titleManager;

	// Tasks
	private ConditionTask conditionTask;
	private TooltipTask tooltipTask;

	// Variables
	private final Map<UUID, PlayerTooltipData> playerTooltipData = new ConcurrentHashMap<>();
	private final Map<String, Preset> presets = new LinkedHashMap<>();
	private final Map<String, StatementHolder> holders = new LinkedHashMap<>();

	public TooltipManager(Tooltips plugin) {
		this.tooltipBuilder = new TooltipBuilder(plugin.getIconManager());
		this.titleManager = plugin.getTitleManager();
		this.plugin = plugin;
	}

	public Tooltip getTooltip(Player target, Preset preset, List<String> unprocessedText) {
		return this.tooltipBuilder.getTooltip(target, preset, unprocessedText);
	}

	public Tooltip getTooltip(Player target, Theme theme, List<String> unprocessedText) {
		return this.tooltipBuilder.getTooltip(target, theme, unprocessedText);
	}

	public void runTasks() {
		this.loadPresets(plugin.getPresetManager());
		this.conditionTask = new ConditionTask(this);
		this.conditionTask.runTaskTimer(plugin, 0L, plugin.getCheckFrequency());

		this.tooltipTask = new TooltipTask(this);
		this.tooltipTask.runTaskTimerAsynchronously(plugin, 0L, 1L);
	}

	public void stopTasks() {
		if (this.conditionTask != null)
			this.conditionTask.cancel();
		this.conditionTask = null;
		if (this.tooltipTask != null)
			this.tooltipTask.cancel();
		this.tooltipTask = null;
	}

	private void loadPresets(PresetManager presetManager) {
		presets.clear();
		holders.clear();

		for (var preset : presetManager.getConditionalPresets()) {
			String id = preset.getId();

			presets.put(id, preset);
			holders.put(id, preset.getStatementHolder());
		}
	}

	public PlayerTooltipData getPlayerTooltipData(Player player) {
		return playerTooltipData.computeIfAbsent(player.getUniqueId(), PlayerTooltipData::new);
	}

	public PlayerTooltipData getPlayerTooltipData(UUID uuid) {
		return playerTooltipData.computeIfAbsent(uuid, PlayerTooltipData::new);
	}

	public void runActions(TooltipAction action, Player player) {
		if (this.tooltipTask != null)
			this.tooltipTask.runActions(action, player);
	}

	public Map<String, Preset> getPresets() {
		return presets;
	}

	public Map<String, StatementHolder> getHolders() {
		return holders;
	}

	public TitleManager getTitleManager() {
		return titleManager;
	}



}
