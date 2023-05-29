package fi.septicuss.tooltips.pack;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import com.google.gson.JsonObject;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.object.NamespacedPath;
import fi.septicuss.tooltips.utils.FileUtils;
import net.md_5.bungee.api.ChatColor;

public class PackGenerator {

	private Tooltips plugin;
	private ArrayList<Generator> generators;

	// Space providers used in every text line
	private Set<JsonObject> globalProviders = new HashSet<>();

	// Providers of text
	private Set<JsonObject> defaultProviders = new HashSet<>();
	private Set<JsonObject> offsetProviders = new HashSet<>();

	// Used texture paths
	private Set<NamespacedPath> usedTextures = new HashSet<>();
	private Set<Integer> usedAscents = new HashSet<>();

	public PackGenerator(Tooltips plugin) {
		this.plugin = plugin;
		this.generators = new ArrayList<>();
	}

	public void generate() {
		Tooltips.log("Generating resources...");

		final File generatedDirectory = new File(plugin.getDataFolder(), "generated");
		FileUtils.createDirectoryIfNotExists(generatedDirectory);
		FileUtils.cleanDirectory(generatedDirectory);

		for (Generator generator : generators) {
			try {
				generator.generate();
			} catch (Exception e) {
				Tooltips.warn(String.format("Generator \"%s\" has failed. Stack trace:", generator.getName()));
				e.printStackTrace();
				Tooltips.warn("---");
			}
		}

		if (plugin.getConfig().getBoolean("copy-resource-pack.enabled", false)) {
			final String relativePath = plugin.getConfig().getString("copy-resource-pack.path", null);
			if (relativePath != null) {
				final File targetDirectory = new File(plugin.getDataFolder().getParentFile(), relativePath);
				FileUtils.createDirectoryIfNotExists(targetDirectory);
				FileUtils.copyFiles(generatedDirectory, targetDirectory);
			}
		}

		Tooltips.log(ChatColor.GREEN + "Pack has been generated.");
	}

	public void addUsedAscent(int ascent) {
		this.usedAscents.add(ascent);
	}

	public Set<Integer> getUsedAscents() {
		return Collections.unmodifiableSet(this.usedAscents);
	}

	public void addUsedTexture(NamespacedPath path) {
		this.usedTextures.add(path);
	}

	public Set<NamespacedPath> getUsedTextures() {
		return Collections.unmodifiableSet(this.usedTextures);
	}

	public void registerGenerator(Generator generator) {
		this.generators.add(generator);
	}

	public Set<JsonObject> getDefaultProviders() {
		Set<JsonObject> copy = new HashSet<>();
		defaultProviders.forEach(provider -> copy.add(provider.deepCopy()));
		return Collections.unmodifiableSet(copy);
	}

	public Set<JsonObject> getOffsetProviders() {
		Set<JsonObject> copy = new HashSet<>();
		offsetProviders.forEach(provider -> copy.add(provider.deepCopy()));
		return Collections.unmodifiableSet(copy);
	}

	public Set<JsonObject> getGlobalProviders() {
		Set<JsonObject> copy = new HashSet<>();
		globalProviders.forEach(provider -> copy.add(provider.deepCopy()));
		return Collections.unmodifiableSet(copy);
	}

	public void addDefaultProvider(JsonObject provider) {
		this.defaultProviders.add(provider);
	}

	public void addOffsetProvider(JsonObject provider) {
		this.offsetProviders.add(provider);
	}

	public void addGlobalSpaceProvider(JsonObject provider) {
		this.globalProviders.add(provider);
	}

	public File getGeneratedDirectory() {
		return new File(plugin.getDataFolder(), "generated");
	}

	public File getPackAssetsDirectory() {
		return new File(plugin.getDataFolder(), "pack/assets");
	}
}
