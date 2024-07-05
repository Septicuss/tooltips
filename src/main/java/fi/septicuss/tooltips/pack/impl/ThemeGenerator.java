package fi.septicuss.tooltips.pack.impl;

import java.io.File;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.theme.Theme;
import fi.septicuss.tooltips.managers.theme.ThemeManager;
import fi.septicuss.tooltips.pack.Generator;
import fi.septicuss.tooltips.pack.PackData;
import fi.septicuss.tooltips.pack.PackData.ProviderType;
import fi.septicuss.tooltips.utils.FileUtils;

public class ThemeGenerator implements Generator {

	private ThemeManager themeManager;
	
	public ThemeGenerator(ThemeManager themeManager) {
		this.themeManager = themeManager;
	}
	
	@Override
	public void generate(PackData packData, File assetsDirectory, File targetDirectory) {
		
		File themeDirectory = new File(targetDirectory, "tooltips/font/themes");
		FileUtils.createDirectoryIfNotExists(themeDirectory);
		
		for (var theme : themeManager.getThemes().values()) {
			String themeId = theme.getId();
			
			packData.addUsedTexture(theme.getPath());

			for (int line = 0; line < theme.getLines(); line++) {
				int ascent = theme.getTextStartAscent() - (line * theme.getTextLineSpacing());
				packData.addUsedAscent(ascent);
			}
			
			File themeFile = new File(themeDirectory, themeId + ".json");
			FileUtils.createFileIfNotExists(themeFile);
			
			JsonObject root = getFullThemeJson(packData, theme);
			String json = Tooltips.GSON.toJson(root);

			FileUtils.writeToFile(themeFile, json);
		}
		
	}

	@Override
	public String getName() {
		return "Theme Generator";
	}
	
	private JsonObject getFullThemeJson(PackData packData, Theme theme) {
		JsonObject root = new JsonObject();
		JsonArray providers = new JsonArray();

		var path = theme.getPath().getNamespacedPath();
		var ascent = theme.getThemeAscent();
		var height = theme.getHeight();
		
		providers.add(getThemeProvider(path, ascent, height));
		
		for (var provider : packData.getProviders(ProviderType.GLOBAL)) {
			providers.add(provider);
		}
		
		root.add("providers", providers);
		return root;
	}
	
	private JsonObject getThemeProvider(String namespacedPath, int ascent, int height) {
		JsonObject provider = new JsonObject();
		provider.addProperty("type", "bitmap");
		provider.addProperty("file", namespacedPath);
		provider.addProperty("ascent", ascent);
		provider.addProperty("height", height);
		
		JsonArray chars = new JsonArray();
		
		StringBuilder charLineBuilder = new StringBuilder();
		charLineBuilder.append(ThemeManager.LEFT);
		charLineBuilder.append(ThemeManager.CENTER);
		charLineBuilder.append(ThemeManager.RIGHT);
		
		chars.add(charLineBuilder.toString());
		provider.add("chars", chars);
		
		return provider;
	}

}
