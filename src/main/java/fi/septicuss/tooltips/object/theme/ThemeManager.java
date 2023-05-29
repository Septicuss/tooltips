package fi.septicuss.tooltips.object.theme;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import fi.septicuss.tooltips.Tooltips;
import net.md_5.bungee.api.ChatColor;

public class ThemeManager {
	
	public static final char OFFSET = '\uF100';
	public static final char LEFT = '\uF101';
	public static final char CENTER = '\uF102';
	public static final char RIGHT = '\uF103';

	private Map<String, Theme> themes;

	public ThemeManager() {
		this.themes = new HashMap<>();
	}

	public void loadFrom(List<FileConfiguration> themeConfigs) {

		Tooltips.log("Loading themes...");

		int total = 0;
		int amount = 0;

		themes.clear();

		if (!themeConfigs.isEmpty()) {
			for (FileConfiguration config : themeConfigs) {
				for (String name : config.getRoot().getKeys(false)) {
					final ConfigurationSection section = config.getRoot().getConfigurationSection(name);
					final Theme theme = new Theme(section);

					total++;

					if (!theme.isValid()) {
						continue;
					}

					registerTheme(name, theme);
					amount++;
				}
			}
		}

		final int invalid = total - amount;
		final String loadedAmount = amount + ((invalid > 0) ? (" (out of " + total + ")") : (""));
		final String message = String.format("Loaded " + loadedAmount + " themes.");

		Tooltips.log(ChatColor.GREEN + message);

	}

	public Map<String, Theme> getThemes() {
		return Collections.unmodifiableMap(themes);
	}

	public Theme getTheme(String name) {
		return themes.get(name);
	}

	public boolean doesThemeExist(String name) {
		return themes.containsKey(name);
	}

	public void registerTheme(String name, Theme theme) {
		this.themes.put(name, theme);
	}
}
