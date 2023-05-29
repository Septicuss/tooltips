package fi.septicuss.tooltips.object.preset;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;

import fi.septicuss.tooltips.Tooltips;
import net.md_5.bungee.api.ChatColor;

public class PresetManager {

	private Map<String, Preset> presets;
	private Map<String, Preset> conditionalPresets;

	public PresetManager() {
		this.presets = new HashMap<>();
		this.conditionalPresets = new HashMap<>();
	}

	public void loadFrom(Tooltips plugin, List<FileConfiguration> presetConfigs) {

		Tooltips.logger().info(String.format("Loading presets..."));

		int amount = 0;
		presets.clear();

		if (!presetConfigs.isEmpty()) {
			for (FileConfiguration config : presetConfigs) {
				for (String name : config.getRoot().getKeys(false)) {
					final ConfigurationSection section = config.getRoot().getConfigurationSection(name);
					final Preset preset = new Preset(plugin, section);

					if (!preset.isValid()) {
						continue;
					}

					presets.put(name, preset);

					if (preset.hasStatementHolder())
						conditionalPresets.put(name, preset);

					amount++;
				}
			}
		}

		Tooltips.log(ChatColor.GREEN + String.format("Loaded %d presets.", amount));

	}

	public Map<String, Preset> getConditionalPresets() {
		return Collections.unmodifiableMap(conditionalPresets);
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
