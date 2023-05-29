package fi.septicuss.tooltips.pack.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.object.NamespacedPath;
import fi.septicuss.tooltips.object.theme.Theme;
import fi.septicuss.tooltips.object.theme.ThemeManager;
import fi.septicuss.tooltips.pack.Generator;
import fi.septicuss.tooltips.pack.PackGenerator;
import fi.septicuss.tooltips.utils.FileUtils;

public class ThemeGenerator implements Generator {

	public static final String DEFAULT_LINE_FORMAT = "l%d";
	public static final String OFFSET_LINE_FORMAT = "l%do";
	
	private ThemeManager themeManager;
	private File generatedFontsDirectory;
	private PackGenerator packGenerator;
	private boolean useSpaces;

	public ThemeGenerator(PackGenerator packGenerator, ThemeManager themeManager, boolean useSpaces) {
		this.themeManager = themeManager;
		this.packGenerator = packGenerator;
		this.generatedFontsDirectory = new File(packGenerator.getGeneratedDirectory(), "tooltips/font");
		this.useSpaces = useSpaces;
	}

	@Override
	public void generate() {
		try {
			generateThemes();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "Theme Generator";
	}

	private void generateThemes() throws IOException {

		for (Map.Entry<String, Theme> entry : themeManager.getThemes().entrySet()) {
			generateTheme(entry.getKey(), entry.getValue());
		}

	}

	private void generateTheme(String themeName, Theme theme) throws IOException {
		final NamespacedPath texturePath = theme.getPath();
		final File texture = new File(packGenerator.getPackAssetsDirectory(), texturePath.getFullPath());

		if (!texture.exists()) {
			Tooltips.warn(String.format("Failed to generate theme \"%s\" due to missing texture", themeName));
			return;
		}

		packGenerator.addUsedTexture(texturePath);
		
		final File fontDirectory = new File(generatedFontsDirectory, themeName);

		// Creating the main font
		final File main = new File(fontDirectory, "m.json");
		FileUtils.createIfNotExists(main);

		final String json = getThemeJson(theme);
		Files.asCharSink(main, Charset.forName("UTF-8")).write(json);

		// Creating the lines

		int startAscent = theme.getTextStartAscent();

		for (int line = 0; line < theme.getLines(); line++) {
			int readableLine = line + 1;
			int newAscent = startAscent - (line * theme.getTextLineSpacing());
			
			// Adding text line ascents for use in icons
			packGenerator.addUsedAscent(newAscent);
			
			String defaultJson = getLineJson(newAscent, false);
			String offsetJson = getLineJson(newAscent, true);

			final File defaultFile = new File(fontDirectory, String.format(DEFAULT_LINE_FORMAT + ".json", readableLine));
			final File offsetFile = new File(fontDirectory, String.format(OFFSET_LINE_FORMAT + ".json", readableLine));

			FileUtils.createIfNotExists(defaultFile);
			FileUtils.createIfNotExists(offsetFile);

			Files.asCharSink(defaultFile, Charset.forName("UTF-8")).write(defaultJson);
			Files.asCharSink(offsetFile, Charset.forName("UTF-8")).write(offsetJson);
		}

	}

	private String getLineJson(int textAscent, boolean offset) {
		JsonObject root = new JsonObject();
		JsonArray providers = new JsonArray();

		for (JsonObject object : packGenerator.getGlobalProviders()) {
			providers.add(object);
		}

		for (JsonObject object : (offset ? packGenerator.getOffsetProviders() : packGenerator.getDefaultProviders())) {
			int existingAscent = object.has("ascent") ? object.get("ascent").getAsInt() : 0;
			int newAscent = existingAscent + textAscent;

			object.addProperty("ascent", newAscent);
			providers.add(object);
		}

		root.add("providers", providers);
		return Tooltips.GSON.toJson(root);
	}

	private String getThemeJson(Theme theme) {
		JsonObject root = new JsonObject();
		JsonArray providers = new JsonArray();

		providers.add(getSpaceProvider());
		providers.add(
				getTooltipProvider(theme.getPath().getNamespacedPath(), theme.getThemeAscent(), theme.getHeight()));

		root.add("providers", providers);

		return Tooltips.GSON.toJson(root);
	}

	private JsonObject getTooltipProvider(String namespacedPath, int ascent, int height) {
		JsonObject provider = new JsonObject();
		provider.addProperty("type", "bitmap");
		provider.addProperty("file", namespacedPath);
		provider.addProperty("ascent", ascent);
		provider.addProperty("height", height);
		JsonArray chars = new JsonArray();
		chars.add(String.valueOf(ThemeManager.LEFT) + String.valueOf(ThemeManager.CENTER)
				+ String.valueOf(ThemeManager.RIGHT));
		provider.add("chars", chars);
		return provider;
	}

	private JsonObject getSpaceProvider() {
		JsonObject provider = new JsonObject();
		if (useSpaces) {
			provider.addProperty("type", "space");
			JsonObject advances = new JsonObject();
			advances.addProperty(String.valueOf(ThemeManager.OFFSET), -1);
			provider.add("advances", advances);
		} else {
			provider.addProperty("type", "bitmap");
			provider.addProperty("file", "tooltips:negative_space.png");
			provider.addProperty("ascent", -32768);
			provider.addProperty("height", -3);
			JsonArray chars = new JsonArray();
			chars.add(String.valueOf(ThemeManager.OFFSET));
			provider.add("chars", chars);
		}
		return provider;
	}

}
