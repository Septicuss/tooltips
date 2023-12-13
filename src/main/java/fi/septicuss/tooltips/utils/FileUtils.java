package fi.septicuss.tooltips.utils;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import fi.septicuss.tooltips.Tooltips;

public class FileUtils {

	public static List<FileConfiguration> getAllConfigsFrom(Tooltips plugin, String path) {
		final File directory = new File(plugin.getDataFolder(), path);

		List<FileConfiguration> result = new ArrayList<>();
		List<File> files = getAllFilesFromDirectory(directory);

		for (File file : files) {
			if (file == null || file.isDirectory() || !file.getName().endsWith(".yml")) {
				continue;
			}

			result.add(YamlConfiguration.loadConfiguration(file));
		}

		return result;
	}

	private static List<File> getAllFilesFromDirectory(File directory) {
		List<File> result = new ArrayList<>();

		for (File file : directory.listFiles()) {
			if (file.isDirectory()) {
				result.addAll(getAllFilesFromDirectory(file));
				continue;
			}

			result.add(file);
		}

		return result;
	}

	public static void copyFiles(File fromDirectory, File toDirectory) {
	    try {
	        Path sourceDir = fromDirectory.toPath();
	        Path destDir = toDirectory.toPath();

	        Files.walk(fromDirectory.toPath()).filter(Files::isRegularFile).forEach(source -> {
	            try {
	                Path dest = destDir.resolve(sourceDir.relativize(source));
	                Files.createDirectories(dest.getParent());

	                // Read the content of the source file into a byte array
	                byte[] data = Files.readAllBytes(source);

	                // Write the byte array to the destination file
	                Files.write(dest, data, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);

	            } catch (IOException e) {
	                e.printStackTrace();
	            }
	        });
	    } catch (IOException e) {
	        e.printStackTrace();
	    }
	}

	public static void copyFileToDirectory(File file, File toDirectory) {
		try {
			File targetFile = new File(toDirectory, file.getName());

			if (!targetFile.exists()) {
				targetFile.getParentFile().mkdirs();
				targetFile.createNewFile();
			}

			Files.copy(file.toPath(), targetFile.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void copyFile(File from, File to) {
		try {
			Files.copy(from.toPath(), to.toPath(), StandardCopyOption.REPLACE_EXISTING);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static void writeToFile(File file, String str) {
		try {
			FileUtils.createIfNotExists(file);
			BufferedWriter writer = Files.newBufferedWriter(file.toPath());
			writer.write(str);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void cleanDirectory(File directory) {
		if (directory == null)
			return;
		if (directory.listFiles() == null)
			return;
		for (File file : directory.listFiles()) {
			if (file == null) {
				continue;
			}
			if (file.isDirectory()) {
				cleanDirectory(file);
			}

			file.delete();
		}
	}

	public static void createIfNotExists(File file) {
		if (file.isDirectory() && !file.isFile())
			createDirectoryIfNotExists(file);
		else
			createFileIfNotExists(file);
	}

	public static void createDirectoryIfNotExists(File directory) {
		if (!directory.exists())
			directory.mkdirs();
	}

	public static void createFileIfNotExists(File file) {
		if (!file.exists()) {
			try {
				file.getParentFile().mkdirs();
				file.createNewFile();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
