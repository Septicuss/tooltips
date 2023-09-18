package fi.septicuss.tooltips.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.security.CodeSource;
import java.util.HashSet;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fi.septicuss.tooltips.Tooltips;

public class FileSetup {

	private static final String[] SECONDARY_CONFIG_TYPES = { "presets", "themes", "icons" };

	public static void setupFiles(Tooltips plugin) {

		setupConfigs(plugin);

		try {
			setupData(plugin);
		} catch (IOException | URISyntaxException e) {
			e.printStackTrace();
		}
	}

	public static void setupConfigs(Tooltips plugin) {

		try {
			setupMainConfig(plugin);
		} catch (IOException e) {
			Tooltips.warn("Failed to set up config.yml");
			e.printStackTrace();
		}

		try {
			setupSecondaryConfigs(plugin);
		} catch (IOException e) {
			Tooltips.warn("Failed to set up secondary configs");
			e.printStackTrace();
		}

	}

	private static void setupMainConfig(Tooltips plugin) throws IOException {

		final String internalPath = "default/config/config.yml";
		final String targetPath = "config.yml";

		final File dataFolder = plugin.getDataFolder();
		final File existingConfigFile = new File(dataFolder, targetPath);

		if (!existingConfigFile.exists()) {
			copyFromJar(plugin, internalPath, existingConfigFile);
		}

		// Update existing config if needed
		final InputStreamReader reader = new InputStreamReader(plugin.getResource(internalPath));

		final FileConfiguration existingConfig = YamlConfiguration.loadConfiguration(existingConfigFile);
		final FileConfiguration internalConfig = YamlConfiguration.loadConfiguration(reader);

		for (String key : internalConfig.getKeys(true)) {
			if (!existingConfig.contains(key)) {
				existingConfig.set(key, internalConfig.get(key));
				existingConfig.setComments(key, internalConfig.getComments(key));
			}
		}
		
		existingConfig.save(existingConfigFile);

	}

	private static void setupSecondaryConfigs(Tooltips plugin) throws IOException {

		final File dataFolder = plugin.getDataFolder();

		for (String configType : SECONDARY_CONFIG_TYPES) {
			final String fromPath = String.format("default/config/%1$s/%1$s.yml", configType);
			final String toPath = String.format("%1$s/%1$s.yml", configType);

			final File file = new File(dataFolder, toPath);

			if (!file.exists()) {
				copyFromJar(plugin, fromPath, file);
			}
		}

	}

	public static void copyFromJar(Tooltips plugin, String internalPath, File targetFile) throws IOException {
		final InputStream stream = plugin.getResource(internalPath);
		if (stream == null)
			return;

		targetFile.getParentFile().mkdirs();

		Files.copy(stream, targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
	}

	// Relocates from jar default/data to plugins/Tooltips/data
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

			if (!name.startsWith("default/.data") && !name.startsWith("default/pack")) {
				continue;
			}

			String path = name.replaceFirst("default/", "");
			File file = new File(dataFolder, path);
			
			if (!name.endsWith(".png") && !name.endsWith(".yml") && !name.endsWith(".json")) {
				continue;
			}

			file.getParentFile().mkdirs();
			
			if (file.exists() && !name.endsWith(".png")) {
				continue;
			}
			
			file.createNewFile();
			Files.copy(zipFile, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
		}

	}
	
	public static void performMigration(Tooltips plugin) {
		
		File dataFolder = plugin.getDataFolder();

		File oldVariablesFolder = new File(dataFolder, "data/variables");
		File newVariablesFolder = new File(dataFolder, ".data/variables");

		if (oldVariablesFolder != null && oldVariablesFolder.exists()) {
			FileUtils.copyFiles(oldVariablesFolder, newVariablesFolder);
		}
		
		Set<File> deletedFiles = new HashSet<>();
		
		deletedFiles.add(new File(dataFolder, "generated"));
		deletedFiles.add(new File(dataFolder, "data"));
		
		deletedFiles.forEach(file -> {
			if (file.isDirectory()) {
				FileUtils.cleanDirectory(file);
			}
			
			file.delete();
		});
		
	}

}
