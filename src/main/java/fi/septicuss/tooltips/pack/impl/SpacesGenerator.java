package fi.septicuss.tooltips.pack.impl;

import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Map;

import com.google.common.io.Files;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.pack.Generator;
import fi.septicuss.tooltips.pack.PackGenerator;
import fi.septicuss.tooltips.utils.FileUtils;
import fi.septicuss.tooltips.utils.font.Spaces;

public class SpacesGenerator implements Generator {

	private PackGenerator packGenerator;
	private boolean useSpaces;

	public SpacesGenerator(PackGenerator packGenerator, boolean useSpaces) {
		this.packGenerator = packGenerator;
		this.useSpaces = useSpaces;
	}

	@Override
	public void generate() {
		try {
			registerProviders();
			generateSpaceFiles();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "Spaces Generator";
	}
	
	private void registerProviders() {

		if (useSpaces) {
			final JsonObject provider = new JsonObject();
			provider.addProperty("type", "space");
			JsonObject advances = new JsonObject();
			advances.addProperty(" ", 3);
			advances.addProperty("\uE000", -1);
			advances.addProperty("\uE001", 1);
			advances.addProperty("\u200c", 0);
			provider.add("advances", advances);
			packGenerator.addGlobalSpaceProvider(provider);
		} else {
			JsonObject provider = new JsonObject();
			provider.addProperty("type", "bitmap");
			provider.addProperty("file", "tooltips:negative_space.png");
			provider.addProperty("ascent", -32768);

			JsonArray chars = new JsonArray();
			chars.add("\uE000");

			JsonObject first = provider.deepCopy();
			first.addProperty("height", -3);
			first.add("chars", chars);
			packGenerator.addGlobalSpaceProvider(first.deepCopy());

			chars = new JsonArray();
			chars.add("\uE001");

			JsonObject second = provider.deepCopy();
			second.addProperty("height", -1);
			second.add("chars", chars);
			packGenerator.addGlobalSpaceProvider(second.deepCopy());
		}

	}

	private void generateSpaceFiles() throws IOException {

		JsonObject root = new JsonObject();
		JsonArray providers = new JsonArray();

		if (useSpaces) {
			JsonObject spaceProvider = new JsonObject();
			spaceProvider.addProperty("type", "space");
			JsonObject advances = new JsonObject();

			for (Map.Entry<Integer, Character> spaceEntry : Spaces.getOffsetMapEntries()) {
				advances.addProperty(String.valueOf(spaceEntry.getValue()), spaceEntry.getKey());
			}
			spaceProvider.add("advances", advances);
			providers.add(spaceProvider);
		} else {
			for (Map.Entry<Integer, Character> spaceEntry : Spaces.getOffsetMapEntries()) {
				int offset = spaceEntry.getKey();
				int realOffset = 0;

				if (offset < 0) {
					realOffset = offset - 2;
				} else if (offset == 1) {
					realOffset = -1;
				} else {
					realOffset = offset - 1;
				}

				JsonObject spaceProvider = new JsonObject();
				spaceProvider.addProperty("type", "bitmap");
				spaceProvider.addProperty("file", "tooltips:negative_space.png");
				spaceProvider.addProperty("ascent", -32768);
				spaceProvider.addProperty("height", realOffset);
				JsonArray chars = new JsonArray();
				chars.add(String.valueOf(spaceEntry.getValue()));
				spaceProvider.add("chars", chars);

				providers.add(spaceProvider);
			}
		}

		root.add("providers", providers);

		final String json = Tooltips.GSON.toJson(root);

		final File generatedDirectory = packGenerator.getGeneratedDirectory();
		final File spacesFile = new File(generatedDirectory, "tooltips/font/s.json");

		FileUtils.createIfNotExists(spacesFile);

		Files.asCharSink(spacesFile, Charset.forName("UTF-8")).write(json);
	}


}
