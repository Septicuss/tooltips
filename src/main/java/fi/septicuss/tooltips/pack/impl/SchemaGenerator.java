package fi.septicuss.tooltips.pack.impl;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.imageio.ImageIO;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.pack.Generator;
import fi.septicuss.tooltips.pack.PackGenerator;
import fi.septicuss.tooltips.utils.FileUtils;

public class SchemaGenerator implements Generator {

	private Tooltips plugin;
	private PackGenerator packGenerator;

	public SchemaGenerator(Tooltips plugin, PackGenerator packGenerator) {
		this.plugin = plugin;
		this.packGenerator = packGenerator;
	}

	@Override
	public void generate() {
		generateSchemas();
	}

	@Override
	public String getName() {
		return "Schema Generator";
	}

	private void generateSchemas() {

		final File schemaFonts = new File(plugin.getDataFolder(), "data/schemas/font");

		if (!schemaFonts.exists()) {
			return;
		}

		List<File> fontFiles = new ArrayList<>();

		for (File file : schemaFonts.listFiles()) {
			if (!file.isFile())
				continue;

			if (file.getName().endsWith(".yml")) {
				fontFiles.add(file);
				continue;
			}
		}

		processFontFiles(fontFiles);

		final File schemasTextures = new File(plugin.getDataFolder(), "data/schemas/textures");
		final File generatedTextures = new File(plugin.getDataFolder(), "generated/tooltips/textures");

		FileUtils.copyFiles(schemasTextures, generatedTextures);

	}

	private void processFontFiles(List<File> fontFiles) {

		final List<File> textures = new ArrayList<>();

		for (File fontFile : fontFiles) {

			YamlConfiguration fontConfig = YamlConfiguration.loadConfiguration(fontFile);

			for (String provider : fontConfig.getConfigurationSection("providers").getKeys(false)) {
				ConfigurationSection providerSection = fontConfig.getConfigurationSection("providers." + provider);
				String type = providerSection.getString("type");

				// BitMap provider
				if (type.equals("bitmap")) {
					String texture = providerSection.getString("texture");
					String texturePath = "data/schemas/textures/" + texture;

					File textureFile = new File(plugin.getDataFolder(), texturePath);
					textures.add(textureFile);

					String textureName = textureFile.getName().replace(".png", "");

					JsonObject bitmapProvider = new JsonObject();
					bitmapProvider.addProperty("type", "bitmap");
					bitmapProvider.addProperty("height", providerSection.getInt("height", 4));
					
					int ascent = providerSection.getInt("ascent", 0);
					
					if (ascent != 0)
						bitmapProvider.addProperty("ascent", ascent);
					

					JsonArray chars = new JsonArray();

					for (String charLine : providerSection.getStringList("chars")) {
						chars.add(charLine);
					}

					bitmapProvider.add("chars", chars);

					// Default
					bitmapProvider.addProperty("file", "tooltips:font/" + textureName + ".png");
					packGenerator.addDefaultProvider(bitmapProvider.deepCopy());

					// Offset
					bitmapProvider.remove("file");
					bitmapProvider.addProperty("file", "tooltips:font/" + textureName + "_offset.png");
					packGenerator.addOffsetProvider(bitmapProvider.deepCopy());

				}

			}

		}

		try {
			final File fontTexturesDirectory = new File(plugin.getDataFolder(), "data/schemas/textures/font");
			generateOffsetFonts(fontTexturesDirectory, textures);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Take in all of the textures, and generate alternative offset versions of
	 * them. Offset version has all of its pixels shifted 1 to the right and name
	 * has a suffix of _offset. Results are saved into the outputDirectory.
	 */
	private void generateOffsetFonts(File outputDirectory, List<File> textures) throws IOException {

		for (File textureFile : textures) {
			final String textureName = textureFile.getName().replace(".png", "");
			final File outputFile = new File(outputDirectory, textureName + "_offset.png");

			if (outputFile.exists()) {
				continue;
			}

			outputFile.createNewFile();

			BufferedImage original = ImageIO.read(textureFile);
			BufferedImage shifted = new BufferedImage(original.getWidth(), original.getHeight(), original.getType());

			for (int x = 1; x < original.getWidth(); x++) {
				for (int y = 0; y < original.getHeight(); y++) {
					int argb = original.getRGB(x - 1, y);
					int alpha = (argb >> 24) & 0xff; // extract alpha component
					int color = argb & 0xffffff; // extract color components (RGB)
					shifted.setRGB(x, y, (alpha << 24) | color);
				}
			}

			ImageIO.write(shifted, "png", outputFile);

		}

	}

}
