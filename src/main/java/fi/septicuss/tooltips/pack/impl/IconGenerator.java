package fi.septicuss.tooltips.pack.impl;

import java.io.File;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.icon.Icon;
import fi.septicuss.tooltips.managers.icon.IconManager;
import fi.septicuss.tooltips.pack.Generator;
import fi.septicuss.tooltips.pack.PackData;
import fi.septicuss.tooltips.pack.PackData.ProviderType;
import fi.septicuss.tooltips.utils.FileUtils;
import fi.septicuss.tooltips.utils.Utils;

public class IconGenerator implements Generator {

	public static final String ICON_FONT_FORMAT = "i%d";
	private static final String ICON_PATH = "tooltips/font/icons/" + ICON_FONT_FORMAT + ".json";

	private IconManager iconManager;
	
	public IconGenerator(IconManager iconManager) {
		this.iconManager = iconManager;
	}
	
	@Override
	public void generate(PackData packData, File assetsDirectory, File targetDirectory) {
		
		
		Set<JsonObject> iconProviders = new HashSet<>();
		
		iconManager.getAllIcons().forEach(icon -> {
			// 1.
			packData.addUsedTexture(icon.getPath());
			
			// 2.
			iconProviders.add(getProviderFromIcon(icon));
		});
		
		
		// 3.
		packData.getUsedAscents().forEach(ascent -> {
			Set<JsonObject> copy = Utils.getJsonSetCopy(iconProviders);
			addAscentToProviders(copy, ascent);
			
			for (var globalProvider : packData.getProviders(ProviderType.GLOBAL)) {
				copy.add(globalProvider);
			}
			
			JsonObject root = new JsonObject();
			JsonArray array = new JsonArray();
			
			copy.forEach(array::add);
			root.add("providers", array);

			File iconFontFile = new File(targetDirectory, String.format(ICON_PATH, ascent));
			FileUtils.createFileIfNotExists(iconFontFile);
			FileUtils.writeToFile(iconFontFile, Tooltips.GSON.toJson(root));
		});
		
	}

	@Override
	public String getName() {
		return "Icon Generator";
	}
	
	private void addAscentToProviders(Set<JsonObject> providers, int ascent) {
		for (var provider : providers) {
			int currentAscent = (provider.has("ascent") ? provider.get("ascent").getAsInt() : 0);
			int completeAscent = currentAscent + ascent;

			provider.addProperty("ascent", completeAscent);
		}
	}
	
	public JsonObject getProviderFromIcon(Icon icon) {
		JsonObject provider = new JsonObject();
		provider.addProperty("type", "bitmap");
		provider.addProperty("file", icon.getPath().getNamespacedPath());
		provider.addProperty("ascent", icon.getAscent());
		provider.addProperty("height", icon.getHeight());
		
		JsonArray chars = new JsonArray();
		chars.add(icon.getUnicode());
		
		provider.add("chars", chars);
		return provider;
	}

}
