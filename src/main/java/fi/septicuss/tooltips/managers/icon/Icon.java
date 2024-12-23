package fi.septicuss.tooltips.managers.icon;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.utils.NamespacedPath;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.validation.Validatable;
import org.bukkit.configuration.ConfigurationSection;

import java.io.File;

public class Icon implements Validatable {

	private String path;
	private String file;
	private NamespacedPath texturePath;
	private int ascent;
	private int height;
	private double overridingWidth;

	private char unicode = ' ';
	private boolean valid = false;

	public Icon(String path, String file, ConfigurationSection iconSection) {
		this.path = path;
		this.file = file;
		this.ascent = iconSection.getInt("ascent");
		this.height = iconSection.getInt("height");
		this.overridingWidth = iconSection.getDouble("width", 0);

		String pathStr = iconSection.getString("path");

		if (pathStr == null) {
			Tooltips.warn(String.format("Icon " + Utils.quote(path) + " does not define a path to the texture."));
			return;
		}

		this.texturePath = new NamespacedPath(pathStr, "textures");

		var textureFile = new File(Tooltips.getPackAssetsFolder(), texturePath.getFullPath());
		var fileExists = (textureFile.exists());

		if (!fileExists) {
			Tooltips.warn("Icon " + Utils.quote(path) + " uses an invalid texture " + Utils.quote(texturePath.getNamespacedPath()));
			return;
		}

		this.valid = true;
	}

	public String getPath() {
		return path;
	}

	public NamespacedPath getTexturePath() {
		return texturePath;
	}

	public int getAscent() {
		return ascent;
	}

	public int getHeight() {
		return height;
	}

	public char getUnicode() {
		return unicode;
	}

	public void setUnicode(char unicode) {
		this.unicode = unicode;
	}

	public double getOverridingWidth() {
		return overridingWidth;
	}

	public boolean hasUnicode() {
		return (unicode != ' ');
	}

	@Override
	public boolean isValid() {
		return this.valid;
	}

}
