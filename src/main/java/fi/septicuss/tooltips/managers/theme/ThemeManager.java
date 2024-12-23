package fi.septicuss.tooltips.managers.theme;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.utils.FileUtils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ThemeManager {
	
	public static final char OFFSET = '\uE000';
	public static final char LEFT = '\uF101';
	public static final char CENTER = '\uF102';
	public static final char RIGHT = '\uF103';

	private Map<String, Theme> themes;

	public ThemeManager() {
		this.themes = new HashMap<>();
	}

	public void loadFrom(final File themeDirectory) {
		Tooltips.log("Loading themes...");

		int total = 0;
		int valid = 0;
		themes.clear();

		final List<File> themeFiles = FileUtils.getAllYamlFilesFromDirectory(themeDirectory);

		if (!themeFiles.isEmpty()) {
			for (File file : themeFiles) {
				final String relativeName = FileUtils.getRelativeFileName(themeDirectory, file);

				var config = YamlConfiguration.loadConfiguration(file);
				var root = config.getRoot();

				for (String name : root.getKeys(false)) {
					final String themePath = relativeName + "/" + name;

					final ConfigurationSection section = root.getConfigurationSection(name);
					final Theme theme = new Theme(themePath, section);

					total++;

					if (!theme.isValid()) continue;

					registerTheme(themePath, theme);
					valid++;

				}

			}
		}

		final int invalid = total - valid;
		final String loadedAmount = valid + ((invalid > 0) ? (" (out of " + total + ")") : (""));
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
