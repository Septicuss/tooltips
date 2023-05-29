package fi.septicuss.tooltips.pack.impl;

import java.io.File;

import com.google.common.io.Files;

import fi.septicuss.tooltips.object.NamespacedPath;
import fi.septicuss.tooltips.pack.Generator;
import fi.septicuss.tooltips.pack.PackGenerator;
import fi.septicuss.tooltips.utils.FileUtils;

public class TextureGenerator implements Generator {

	private PackGenerator packGenerator;

	public TextureGenerator(PackGenerator packGenerator) {
		this.packGenerator = packGenerator;
	}

	@Override
	public void generate() {
		try {
			File packAssets = packGenerator.getPackAssetsDirectory();
			File generated = packGenerator.getGeneratedDirectory();

			for (NamespacedPath texturePath : packGenerator.getUsedTextures()) {
				File textureFile = new File(packAssets, texturePath.getFullPath());
				File destination = new File(generated, texturePath.getFullPath());
				
				FileUtils.createFileIfNotExists(destination);
				Files.copy(textureFile, destination);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public String getName() {
		return "Texture Generator";
	}

}
