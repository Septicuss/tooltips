package fi.septicuss.tooltips.managers.schema;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.imageio.ImageIO;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.font.Widths;

/**
 * Parses schemas defined in .data/schemas/....yml into JSON providers
 */
public class SchemaManager {

	private Map<String, SchemaPart> schemas;
	private File schemaDirectory;

	public SchemaManager() {
		this.schemas = new HashMap<>();
	}

	public void loadFrom(File schemaDirectory) {

		Tooltips.log("Loading schemas...");

		this.schemaDirectory = schemaDirectory;

		for (var file : schemaDirectory.listFiles()) {
			if (file == null || !file.isFile()) {
				continue;
			}

			if (!file.getName().endsWith(".yml")) {
				continue;
			}

			parseSchemaFile(schemaDirectory, file);
		}

	}

	public Set<SchemaPart> getSchemaParts() {
		return Collections.unmodifiableSet(new HashSet<>(schemas.values()));
	}

	public File getSchemaDirectory() {
		return schemaDirectory;
	}

	private void parseSchemaFile(File schemaDirectory, File schemaFile) {
		FileConfiguration config = YamlConfiguration.loadConfiguration(schemaFile);

		if (!config.isSet("providers")) {
			return;
		}

		File textureDirectory = new File(schemaDirectory, "textures");

		for (String providerKey : config.getConfigurationSection("providers").getKeys(false)) {
			ConfigurationSection providerSection = config.getConfigurationSection("providers." + providerKey);
			File textureFile = null;

			if (providerSection.isSet("texture")) {
				String texture = providerSection.getString("texture");
				textureFile = new File(textureDirectory, texture);

				if (textureFile == null || !textureFile.exists()) {
					warn(providerKey, "Texture " + Utils.quote(texture) + " was not found");
					continue;
				}
			}

			JsonObject provider = parseProvider(providerSection);

			if (provider == null) {
				continue;
			}

			try {
				List<String> chars = providerSection.getStringList("chars");
				int height = providerSection.getInt("height");

				BufferedImage image = ImageIO.read(textureFile);
				Widths.loadFrom(chars, height, image);
			} catch (IOException e) {
				e.printStackTrace();
			}

			schemas.put(providerKey, new SchemaPart(providerSection, provider));
		}

	}

	private JsonObject parseProvider(ConfigurationSection providerSection) {

		if (providerSection == null || !providerSection.isSet("type")) {
			return null;
		}

		final String key = providerSection.getName();

		if (!providerSection.isSet("height")) {
			warn(key, "Missing 'height'");
			return null;
		}

		if (!providerSection.isSet("texture")) {
			warn(key, "Missing 'texture'");
			return null;
		}

		if (!providerSection.isSet("chars")) {
			warn(key, "Missing 'chars'");
			return null;
		}

		int ascent = providerSection.getInt("ascent");
		int height = providerSection.getInt("height");
		String texture = providerSection.getString("texture");
		String texturePath = String.format("tooltips:font/%s", texture);
		List<String> chars = providerSection.getStringList("chars");

		JsonObject provider = new JsonObject();

		provider.addProperty("type", "bitmap");
		provider.addProperty("file", texturePath);
		provider.addProperty("height", height);

		if (ascent != 0) {
			provider.addProperty("ascent", providerSection.getInt("ascent"));
		}

		JsonArray array = new JsonArray();
		chars.forEach(array::add);
		provider.add("chars", array);

		return provider;
	}

	private void warn(String key, String reason) {
		Tooltips.warn("Failed to load provider " + Utils.quote(key) + " in schemas.");
		Tooltips.warn(" - " + reason);
	}

}
