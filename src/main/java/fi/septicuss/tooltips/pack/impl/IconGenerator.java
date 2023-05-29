package fi.septicuss.tooltips.pack.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.object.NamespacedPath;
import fi.septicuss.tooltips.object.icon.Icon;
import fi.septicuss.tooltips.object.icon.IconManager;
import fi.septicuss.tooltips.pack.Generator;
import fi.septicuss.tooltips.pack.PackGenerator;
import fi.septicuss.tooltips.utils.FileUtils;

public class IconGenerator implements Generator {

	private PackGenerator packGenerator;
	private IconManager iconManager;

	private HashMap<Icon, JsonObject> iconProviderMap = new HashMap<>();

	public IconGenerator(PackGenerator packGenerator, IconManager iconManager) {
		this.packGenerator = packGenerator;
		this.iconManager = iconManager;
	}

	@Override
	public void generate() {
		final Set<Icon> icons = iconManager.getAllIcons();

		addUsedTextures(icons);
		prepareProviders(icons);
		generateFonts();
	}

	@Override
	public String getName() {
		return "Icon Generator";
	}

	/**
	 * Part of the generation process. Add every defined icons texture into the used
	 * texture list.
	 * 
	 * @param icons
	 */
	private void addUsedTextures(Set<Icon> icons) {
		for (Icon icon : icons) {
			final NamespacedPath path = icon.getPath();
			packGenerator.addUsedTexture(path);
		}
	}

	/**
	 * Preload all necessary icon providers
	 * 
	 * @param icons
	 */
	private void prepareProviders(Set<Icon> icons) {
		for (Icon icon : icons) {
			JsonObject provider = new JsonObject();
			provider.addProperty("type", "bitmap");
			provider.addProperty("file", icon.getPath().getNamespacedPath());
			provider.addProperty("ascent", icon.getAscent());
			provider.addProperty("height", icon.getHeight());

			JsonArray chars = new JsonArray();
			chars.add(icon.getUnicode());

			provider.add("chars", chars);

			iconProviderMap.put(icon, provider);
		}
	}

	/**
	 * Generates icon font files for all used ascents. Icons are not added to
	 * individual text line font files because that would be too costly on memory.
	 */
	private void generateFonts() {
		File fontDirectory = new File(packGenerator.getGeneratedDirectory(), "tooltips/font");
		Set<Integer> ascents = packGenerator.getUsedAscents();

		for (int ascent : ascents) {
			final String fileName = String.format(IconManager.ICON_FONT_FORMAT, ascent);
			final File ascentedIconFile = new File(fontDirectory, fileName + ".json");

			FileUtils.createFileIfNotExists(ascentedIconFile);

			JsonObject root = new JsonObject();
			JsonArray providers = new JsonArray();

			packGenerator.getGlobalProviders().forEach(provider -> providers.add(provider));

			for (Map.Entry<Icon, JsonObject> entry : iconProviderMap.entrySet()) {
				final Icon icon = entry.getKey();
				final JsonObject provider = entry.getValue();

				provider.addProperty("ascent", ascent + icon.getAscent());
				providers.add(provider);
			}

			root.add("providers", providers);

			final String json = Tooltips.GSON.toJson(root);

			try {
				Files.asCharSink(ascentedIconFile, Charset.forName("UTF-8")).write(json);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

}
