package fi.septicuss.tooltips.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.HashMap;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import fi.septicuss.tooltips.Tooltips;

public class FileSetup {

	private static final String[] CONFIG_TYPES = { "presets", "themes", "icons" };
	private static final Map<String, String> CONFIG_MAP;

	static {
		CONFIG_MAP = new HashMap<>();
		CONFIG_MAP.put("default/config/config.yml", "config.yml");

		for (String configType : CONFIG_TYPES) {
			final String fromPath = String.format("default/config/%1$s/%1$s.yml", configType);
			final String toPath = String.format("%1$s/%1$s.yml", configType);
			CONFIG_MAP.put(fromPath, toPath);
		}
	}

	public static void setup(Tooltips plugin) {
		try {
			setupConfigs(plugin);
			setupData(plugin);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	private static void setupConfigs(Tooltips plugin) throws IOException {

		final File dataFolder = plugin.getDataFolder();

		for (Map.Entry<String, String> entry : CONFIG_MAP.entrySet()) {
			final String fromPath = entry.getKey();
			final String toPath = entry.getValue();
			final File file = new File(dataFolder, toPath);

			if (!file.exists()) {
				final InputStream stream = plugin.getResource(fromPath);
				file.getParentFile().mkdirs();
				Files.copy(stream, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		}

	}

	private static void setupData(Tooltips plugin) throws IOException, URISyntaxException {

		final File dataFolder = plugin.getDataFolder();
		final CodeSource codeSource = plugin.getClass().getProtectionDomain().getCodeSource();
		if (codeSource == null) {
			Tooltips.warn("Failed to read code source");
			return;
		}

		final URL jarUrl = codeSource.getLocation();
		final ZipInputStream zipFile = new ZipInputStream(jarUrl.openStream());

		while (true) {
			final ZipEntry entry = zipFile.getNextEntry();
			if (entry == null) {
				break;
			}

			final String name = entry.getName();

			if (!name.startsWith("default/data") && !name.startsWith("default/pack")) {
				continue;
			}

			String path = name.replaceFirst("default/", "");
			File file = new File(dataFolder, path);

			if (!name.endsWith(".png") && !name.endsWith(".yml") && !name.endsWith(".json")) {
				continue;
			}

			if (file.exists()) {
				continue;
			}
			
			file.getParentFile().mkdirs();
			file.createNewFile();
			Files.copy(zipFile, file.toPath(), StandardCopyOption.REPLACE_EXISTING);

		}

	}

}
