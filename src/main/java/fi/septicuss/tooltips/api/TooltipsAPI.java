package fi.septicuss.tooltips.api;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nullable;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.object.preset.Preset;
import fi.septicuss.tooltips.object.preset.condition.Condition;
import fi.septicuss.tooltips.object.theme.Theme;
import fi.septicuss.tooltips.object.title.Title;
import fi.septicuss.tooltips.object.title.TitleBuilder;
import fi.septicuss.tooltips.tooltip.Tooltip;

public class TooltipsAPI {

	private static final Map<String, Condition> REGISTERED_CONDITIONS = new ConcurrentHashMap<>();

	public static void registerCondition(String name, Condition condition) {
		REGISTERED_CONDITIONS.put(name, condition);
	}

	public static void unregisterCondition(String name) {
		REGISTERED_CONDITIONS.remove(name);
	}

	public static Map<String, Condition> getRegisteredConditions(){
		return Collections.unmodifiableMap(REGISTERED_CONDITIONS);
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

		Title title = new TitleBuilder(Tooltips.get().getProtocolManager())
				.setSubtitle(tooltip.getComponents())
				.setFadeIn(preset.getFadeIn())
				.setStay(preset.getStay())
				.setFadeOut(preset.getFadeOut())
				.build();

		title.send(player);
	}

	private static void sendTooltipTitle(Player player, Theme theme, List<String> extra) {
		Tooltip tooltip = Tooltips.get().getTooltipManager().getTooltip(player, theme, extra);
		
		Title title = new TitleBuilder(Tooltips.get().getProtocolManager())
				.setSubtitle(tooltip.getComponents())
				.setFadeIn(0)
				.setStay(5*20)
				.setFadeOut(0)
				.build();
		
		title.send(player);
	}

}
