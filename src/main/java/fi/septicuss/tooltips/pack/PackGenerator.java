package fi.septicuss.tooltips.pack;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.utils.FileUtils;
import fi.septicuss.tooltips.utils.Utils;

import java.io.File;
import java.util.LinkedList;

public class PackGenerator {

	private Tooltips plugin;
	private LinkedList<Generator> generators;

	public PackGenerator(Tooltips plugin) {
		this.plugin = plugin;
		this.generators = new LinkedList<>();
	}

	public void generate() {

		Tooltips.log("Generating resources...");

		File targetDirectory = getTargetDirectory();
		FileUtils.createDirectoryIfNotExists(targetDirectory);
		FileUtils.cleanDirectory(targetDirectory);

		PackData packData = new PackData();

		for (Generator generator : generators) {
			try {
				generator.generate(packData, getAssetsDirectory(), targetDirectory);
			} catch (Exception exception) {
				final var generatorName = Utils.quote(generator.getName());
				final var warningMessage = String.format("Generator %s has failed. Error:", generatorName);

				Tooltips.warn(warningMessage);
				exception.printStackTrace();
			}
		}

		boolean copyPack = plugin.getConfig().getBoolean("copy-resource-pack.enabled", false);

		if (copyPack) {
			copyOutput();
		}

	}

	public void copyOutput() {
		final String relativePath = plugin.getConfig().getString("copy-resource-pack.path", null);

		if (relativePath == null) {
			return;
		}

		File copyToDirectory = new File(plugin.getDataFolder().getParentFile(), relativePath);
		FileUtils.createDirectoryIfNotExists(copyToDirectory);
		FileUtils.copyFiles(getTargetDirectory(), copyToDirectory);
	}

	public void registerGenerator(Generator generator) {
		this.generators.add(generator);
	}

	public File getTargetDirectory() {
		return new File(plugin.getDataFolder(), ".generated");
	}

	public File getAssetsDirectory() {
		return new File(plugin.getDataFolder(), "pack/assets");
	}

}
