package fi.septicuss.tooltips.pack.impl;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.pack.Generator;
import fi.septicuss.tooltips.pack.PackData;
import fi.septicuss.tooltips.pack.PackData.ProviderType;
import fi.septicuss.tooltips.utils.FileSetup;
import fi.septicuss.tooltips.utils.FileUtils;
import fi.septicuss.tooltips.utils.font.Spaces;

public class SpaceGenerator implements Generator {

//	private static final char ZERO_WIDTH = '\u200c';
	private static final String SPACE_FILE_NAME = "space.json";

	private static final String SPACE_FILE_LOCATION = "default/pack/assets/tooltips/textures/space.png";
	private static final String SPACE_FILE_DESTINATION = "tooltips/textures/space.png";

	private boolean useSpaces;

	public SpaceGenerator(boolean useSpaces) {
		this.useSpaces = useSpaces;
	}

	@Override
	public void generate(PackData packData, File assetsDirectory, File targetDirectory) {

		// 1.
		String json = getSpaceFileJson();
		File spaceFile = new File(targetDirectory, "tooltips/font/" + SPACE_FILE_NAME);
		FileUtils.writeToFile(spaceFile, json);

		// 2.
		var globalProviders = getGlobalSpaceProviders();
		var globalProvidersArray = globalProviders.toArray(new JsonObject[0]);

		packData.addProviders(ProviderType.GLOBAL, globalProvidersArray);

		// 3.
		try {
			File spaceTexture = new File(targetDirectory, SPACE_FILE_DESTINATION);
			FileSetup.copyFromJar(Tooltips.get(), SPACE_FILE_LOCATION, spaceTexture);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
	}

	@Override
	public String getName() {
		return "Space Generator";
	}

	private Set<JsonObject> getGlobalSpaceProviders() {

		Map<String, Integer> offsets = new HashMap<>();
		offsets.put(String.valueOf(Spaces.POSITIVE_ONE), 1);
		offsets.put(String.valueOf(Spaces.NEGATIVE_ONE), -1);

		return getSpaceProviders(offsets);

	}

	private String getSpaceFileJson() {

		JsonObject root = new JsonObject();
		JsonArray providers = new JsonArray();

		Map<String, Integer> offsets = new HashMap<>();

		for (var entry : Spaces.getOffsetMapEntries()) {
			var offset = entry.getKey();
			var chars = String.valueOf(entry.getValue());

			offsets.put(chars, offset);
		}

		for (var provider : getSpaceProviders(offsets)) {
			providers.add(provider);
		}

		root.add("providers", providers);

		return Tooltips.GSON.toJson(root);

	}

	private Set<JsonObject> getSpaceProviders(Map<String, Integer> offsets) {

		Set<JsonObject> result = new HashSet<>();

		if (useSpaces) {

			JsonObject provider = new JsonObject();
			provider.addProperty("type", "space");

			JsonObject advances = new JsonObject();
			for (var entry : offsets.entrySet()) {
				advances.addProperty(entry.getKey(), entry.getValue());
			}
			provider.add("advances", advances);

			result.add(provider);

		} else {

			for (var entry : offsets.entrySet()) {

				JsonObject provider = new JsonObject();
				provider.addProperty("type", "bitmap");
				provider.addProperty("file", "tooltips:space.png");
				provider.addProperty("ascent", -32768);
				provider.addProperty("height", getOldOffset(entry.getValue()));

				JsonArray chars = new JsonArray();
				chars.add(entry.getKey());
				provider.add("chars", chars);

				result.add(provider);
			}

		}

		return result;
	}

	private int getOldOffset(int offset) {
		if (offset < 0) {
			return offset - 2;
		} else if (offset == 1) {
			return -1;
		} else {
			return offset - 1;
		}
	}

}
