package fi.septicuss.tooltips.managers.tooltip;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.condition.StatementHolder;
import fi.septicuss.tooltips.managers.preset.Preset;
import fi.septicuss.tooltips.managers.preset.PresetManager;
import fi.septicuss.tooltips.managers.theme.Theme;
import fi.septicuss.tooltips.managers.title.TitleManager;
import fi.septicuss.tooltips.managers.tooltip.build.TooltipBuilder;
import fi.septicuss.tooltips.managers.tooltip.tasks.CacheTask;
import fi.septicuss.tooltips.managers.tooltip.tasks.ConditionTask;
import fi.septicuss.tooltips.managers.tooltip.tasks.TooltipTask;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.PlayerTooltipData;
import fi.septicuss.tooltips.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
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
	private CacheTask cacheTask;

	// Variables
	private final Map<UUID, PlayerTooltipData> playerTooltipData = new ConcurrentHashMap<>();
	private final Map<String, Preset> presets = new LinkedHashMap<>();
	private final Map<String, StatementHolder> holders = new LinkedHashMap<>();

	// Lock
	private final ConcurrentHashMap<String, Set<UUID>> actionLock = new ConcurrentHashMap<>();

	public TooltipManager(Tooltips plugin) {
		this.tooltipBuilder = new TooltipBuilder(plugin.getIconManager());
		this.titleManager = plugin.getTitleManager();
		this.plugin = plugin;
	}

	public Tooltip getTooltip(Player target, Preset preset, List<String> unprocessedText) {
		if (unprocessedText == null || unprocessedText.isEmpty())
			unprocessedText = preset.getText();

		return this.tooltipBuilder.build(preset, Text.processText(target, unprocessedText));
	}

	public Tooltip getTooltip(Player target, Theme theme, List<String> unprocessedText) {
		return this.tooltipBuilder.build(theme, Text.processText(target, unprocessedText));
	}

	public void runTasks() {
		this.loadPresets(plugin.getPresetManager());

		this.conditionTask = new ConditionTask(this);
		this.conditionTask.runTaskTimer(plugin, 0L, plugin.getCheckFrequency());

		this.tooltipTask = new TooltipTask(this);
		this.tooltipTask.runTaskTimerAsynchronously(plugin, 0L, 1L);

		this.cacheTask = new CacheTask();
		this.cacheTask.runTaskTimerAsynchronously(plugin, 0L, 20 * 20 /* Every 20 seconds */);
	}

	public void stopTasks() {
		if (this.conditionTask != null)
			this.conditionTask.cancel();
		this.conditionTask = null;
		if (this.tooltipTask != null)
			this.tooltipTask.cancel();
		this.tooltipTask = null;
		if (this.cacheTask != null)
			this.cacheTask.cancel();
		this.cacheTask = null;
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

	public void removePlayerTooltipData(Player player) {
		playerTooltipData.remove(player.getUniqueId());
	}

	public void runActions(String action, Player player) {
		if (this.tooltipTask == null)
			return;

        if (this.isLocked(action, player.getUniqueId()))
            return;

        this.lock(action, player.getUniqueId());

		this.tooltipTask.runActions(action, player);
	}

	private boolean isLocked(String action, UUID uuid) {
		return this.actionLock.getOrDefault(action, Collections.emptySet()).contains(uuid);
	}

	private void lock(String action, UUID uuid) {
		// Add to lock
        this.actionLock.computeIfAbsent(action, s -> ConcurrentHashMap.newKeySet()).add(uuid);

        // Remove from lock on next tick
		Bukkit.getScheduler().runTask(plugin, () -> {
            Set<UUID> set = this.actionLock.get(action);
            if (set != null && set.remove(uuid) && set.isEmpty()) {
                this.actionLock.remove(action);
            }
		});
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
