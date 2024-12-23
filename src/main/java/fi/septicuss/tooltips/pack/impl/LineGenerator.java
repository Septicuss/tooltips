package fi.septicuss.tooltips.pack.impl;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.schema.SchemaManager;
import fi.septicuss.tooltips.managers.schema.SchemaPart;
import fi.septicuss.tooltips.pack.Generator;
import fi.septicuss.tooltips.pack.PackData;
import fi.septicuss.tooltips.pack.PackData.ProviderType;
import fi.septicuss.tooltips.utils.FileUtils;
import fi.septicuss.tooltips.utils.Utils;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class LineGenerator implements Generator {

	// Name of a regular line
	public static final String REGULAR_LINE_FORMAT = "%d";
	
	// Name of an offset line
	public static final String OFFSET_LINE_FORMAT = "%do";
	
	// Where the textures get put inside the generated dir
	private static final String OUTPUT_FONT_TEXTURE_PATH = "tooltips/textures/font";

	// Where the files for each line get put inside the generated dir
	private static final String OUTPUT_FONT_LINE_PATH = "tooltips/font/lines";

	private SchemaManager schemaManager;

	public LineGenerator(SchemaManager schemaManager) {
		this.schemaManager = schemaManager;
	}

	@Override
	public void generate(PackData packData, File assetsDirectory, File targetDirectory) {

		final File schemaDirectory = schemaManager.getSchemaDirectory();
		final File sourceTextureDirectory = new File(schemaDirectory, "textures");

		final Set<SchemaPart> schemaParts = schemaManager.getSchemaParts();

		final Set<JsonObject> offsetProviders = new HashSet<>();
		final Set<JsonObject> regularProviders = new HashSet<>();

		File copiedTextureDirectory = new File(targetDirectory, OUTPUT_FONT_TEXTURE_PATH);
		FileUtils.createDirectoryIfNotExists(copiedTextureDirectory);

		schemaParts.forEach(part -> {

			String fullTexturePath = part.schemaConfig().getString("texture");
			String texturePath = fullTexturePath;

			if (fullTexturePath.endsWith(".png")) {
				texturePath = fullTexturePath.substring(0, fullTexturePath.length() - 4);
			}

			// 1.
			File sourceTexture = new File(sourceTextureDirectory, texturePath + ".png");
			File copiedTexture = new File(copiedTextureDirectory, texturePath + ".png");

			FileUtils.createFileIfNotExists(copiedTexture);
			FileUtils.copyFile(sourceTexture, copiedTexture);

			// 2.
			try {
				BufferedImage copiedImage = ImageIO.read(copiedTexture);
				BufferedImage converted = convertToARGB(copiedImage);
				BufferedImage offsetImage = generateOffsetTexture(converted);

				File offsetTexture = new File(copiedTextureDirectory, texturePath + "_offset.png");
				FileUtils.createFileIfNotExists(offsetTexture);

				writeImageToFile(offsetImage, offsetTexture);
			} catch (Exception exception) {
				Tooltips.log("Error while generating schema " + part.schemaConfig().getName() + ":");
				exception.printStackTrace();
			}

			// 3.

			JsonObject regularProvider = part.schemaProvider().deepCopy();
			regularProvider.addProperty("file", "tooltips:font/" + texturePath + ".png");

			JsonObject offsetProvider = part.schemaProvider().deepCopy();
			offsetProvider.addProperty("file", "tooltips:font/" + texturePath + "_offset.png");

			regularProviders.add(regularProvider);
			offsetProviders.add(offsetProvider);
		});
		
		// 4.
		packData.getUsedAscents().forEach(ascent -> {
			Set<JsonObject> regulars = Utils.getJsonSetCopy(regularProviders);
			Set<JsonObject> offsets = Utils.getJsonSetCopy(offsetProviders);
			
			addAscentToProviders(regulars, ascent);
			addAscentToProviders(offsets, ascent);
			
			regulars.addAll(packData.getProviders(ProviderType.GLOBAL));
			offsets.addAll(packData.getProviders(ProviderType.GLOBAL));
			
			File linesDirectory = new File(targetDirectory, OUTPUT_FONT_LINE_PATH);
			
			FileUtils.createDirectoryIfNotExists(linesDirectory);
			
			File regularFile = new File(linesDirectory, String.format(REGULAR_LINE_FORMAT + ".json", ascent));
			File offsetFile = new File(linesDirectory, String.format(OFFSET_LINE_FORMAT + ".json", ascent));
			
			writeProvidersToFile(regulars, regularFile);
			writeProvidersToFile(offsets, offsetFile);
		});

	}

	@Override
	public String getName() {
		return "Line Generator";
	}
	
	private BufferedImage convertToARGB(BufferedImage source) {
	    BufferedImage converted = new BufferedImage(source.getWidth(), source.getHeight(), BufferedImage.TYPE_INT_ARGB);

	    Graphics2D g = converted.createGraphics();
	    g.drawImage(source, 0, 0, null);
	    g.dispose();

	    return converted;
	}

	private BufferedImage generateOffsetTexture(BufferedImage source) {
	    BufferedImage shifted = new BufferedImage(source.getWidth(), source.getHeight(), source.getType());

	    for (int x = 1; x < source.getWidth(); x++) {
	        for (int y = 0; y < source.getHeight(); y++) {
	            int argb = source.getRGB(x - 1, y);
	            int alpha = (argb >> 24) & 0xff; // extract alpha component
	            int color = argb & 0x00ffffff; // extract color components (RGB), mask out alpha
	            shifted.setRGB(x, y, (alpha << 24) | color);
	        }
	    }

	    return shifted;
	}


	private void writeImageToFile(BufferedImage image, File file) throws IOException {
		ImageIO.write(image, "png", file);
	}
	
	private void writeProvidersToFile(Set<JsonObject> providers, File file) {
		FileUtils.createFileIfNotExists(file);
		
		JsonObject root = new JsonObject();
		JsonArray providerArray = new JsonArray();
		
		for (var provider : providers) {
			providerArray.add(provider);
		}
		
		root.add("providers", providerArray);
		
		String json = Tooltips.GSON.toJson(root);
		FileUtils.writeToFile(file, json);
	}

	private void addAscentToProviders(Set<JsonObject> providers, int ascent) {
		for (var provider : providers) {
			int currentAscent = (provider.has("ascent") ? provider.get("ascent").getAsInt() : 0);
			int completeAscent = currentAscent + ascent;
			provider.addProperty("ascent", completeAscent);
		}
	}

}
