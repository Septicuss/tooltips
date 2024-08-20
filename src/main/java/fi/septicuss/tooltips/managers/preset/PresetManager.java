package fi.septicuss.tooltips.managers.preset;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import fi.septicuss.tooltips.utils.FileUtils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.utils.Utils;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.configuration.file.YamlConfiguration;

public class PresetManager {

	private final Map<String, Preset> presets;
	private final Set<String> conditionalPresets;

	public PresetManager() {
		this.presets = new HashMap<>();
		this.conditionalPresets = new HashSet<>();
	}

	public void loadFrom(Tooltips plugin, File presetDirectory) {
		Tooltips.logger().info(String.format("Loading presets..."));

		int valid = 0;
		presets.clear();

		final List<File> presetFiles = FileUtils.getAllYamlFilesFromDirectory(presetDirectory);

		for (File file : presetFiles) {
			final String relativeName = FileUtils.getRelativeFileName(presetDirectory, file);
			final String fileName = FileUtils.getExtensionlessFileName(file);

			var config = YamlConfiguration.loadConfiguration(file);
			var root = config.getRoot();

			for (String key : root.getKeys(false)) {
				final String presetPath = relativeName + "/" + key;
				var section = root.getConfigurationSection(key);

				Preset preset = null;

				if (section.isSet("parent")) {
					String parentId = section.getString("parent");

					if (!presets.containsKey(parentId)) {
						Tooltips.warn("Unable to define preset " + Utils.quote(presetPath) + ", due to unknown parent " + Utils.quote(parentId));
						continue;
					}

					preset = new Preset(plugin, presetPath, presets.get(parentId), section);
				} else {
					preset = new Preset(plugin, presetPath, section);
				}

				if (!preset.isValid()) {
					continue;
				}

				presets.put(presetPath, preset);

				if (preset.hasStatementHolder()) {
					conditionalPresets.add(presetPath);
				}

				valid++;
			}

		}

		Tooltips.log(ChatColor.GREEN + String.format("Loaded %d presets.", valid));
	}

	public Set<Preset> getConditionalPresets() {
		return conditionalPresets.stream().map(presets::get).filter(Objects::nonNull).collect(Collectors.toSet());
	}

	public Map<String, Preset> getPresets() {
		return Collections.unmodifiableMap(this.presets);
	}

	public Preset getPreset(String preset) {
		return presets.get(preset);
	}

	public boolean doesPresetExist(String preset) {
		return presets.containsKey(preset);
	}

}
