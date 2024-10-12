package fi.septicuss.tooltips.utils.font;

import java.awt.image.BufferedImage;
import java.io.File;
import java.util.HashMap;
import java.util.List;

import org.bukkit.configuration.file.YamlConfiguration;

// Credit goes to https://github.com/Ehhthan for width size code
public class Widths {

	private static final HashMap<Character, SizedChar> WIDTH_MAP = new HashMap<>();
	private static final HashMap<Character, SizedChar> ICON_WIDTH_MAP = new HashMap<Character, SizedChar>();

	// -- SCHEMAS --

	public static void loadFrom(List<String> chars, int height, BufferedImage image) {
		if (chars == null) {
			return;
		}
		
		int rows = chars.size();
		int tileHeight = image.getHeight() / rows;
		
		for (int row = 0; row < rows; row++) {
			final int columns = chars.get(row).length();
			final int tileWidth = (image.getWidth() / columns);

			for (int column = 0; column < columns; column++) {
				BufferedImage sub = image.getSubimage(tileWidth * column, tileHeight * row, tileWidth, tileHeight);
				char character = chars.get(row).toCharArray()[column];
				
				if (character == ' ')
					continue;

				SizedChar sizedChar = getSizedChar(character, sub, height);
				WIDTH_MAP.put(character, sizedChar);
			}
		}
	}
	
	public static void loadCustomWidths(File customWidthsFile) {
		YamlConfiguration widths = YamlConfiguration.loadConfiguration(customWidthsFile);
		
		for (String key : widths.getKeys(false)) {
			char character = key.toCharArray()[0];
			SizedChar sized = getSizedChar(character);
			sized.setOverridingWidth(widths.getDouble(key));
			
			WIDTH_MAP.put(character, sized);
		}
		
	}

	// -- PUBLIC API --

	/**
	 * Returns the sized character object for the given character or empty one if
	 * not defined
	 */
	public static SizedChar getSizedChar(char character) {
		return WIDTH_MAP.getOrDefault(character, new SizedChar(character));
	}

	public static SizedChar getIconSizedChar(char iconCharacter) {
		return ICON_WIDTH_MAP.getOrDefault(iconCharacter, new SizedChar(iconCharacter));
	}

	/**
	 * Manually add a sized character
	 */
	public static void add(SizedChar sizedChar) {
		WIDTH_MAP.put(sizedChar.getCharacter(), sizedChar);
	}

	public static void addIcon(char character, BufferedImage image, int definedHeight) {
		final SizedChar sizedChar = getSizedChar(character, image, definedHeight);
		sizedChar.setIcon(true);
		ICON_WIDTH_MAP.put(character, sizedChar);
	}

	/**
	 * Add a new sized character with given properties
	 */
	public static void add(char character, BufferedImage image, int definedHeight) {
		final SizedChar sizedChar = getSizedChar(character, image, definedHeight);
		sizedChar.setIcon(true);
		WIDTH_MAP.put(character, sizedChar);
	}

	public static double getWidth(char character) {
		final SizedChar sizedChar = getSizedChar(character);
		return sizedChar.getRealWidth();
	}

	public static double getWidth(String string) {
		double total = 0;
		for (char character : string.toCharArray()) {
			total += getWidth(character);
		}
		return total += string.length();
	}

	// -- INTERNAL

	private static SizedChar getSizedChar(char character, BufferedImage image, int definedHeight) {
		int exactWidth = calculateExactWidth(image);
		int absoluteWidth = calculateAbsoluteWidth(image);
		
		SizedChar sizedChar = new SizedChar(character);
		
		// Height
		sizedChar.setHeight(definedHeight);
		sizedChar.setImageHeight(image.getHeight());
		
		// Width
		sizedChar.setExactWidth(exactWidth);
		sizedChar.setAbsoluteWidth(absoluteWidth);
		sizedChar.setImageWidth(image.getWidth());

		return sizedChar;
	}

	public static int calculateAbsoluteWidth(BufferedImage image) {
		int rightMostPixel = 0;
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = image.getRGB(x, y);
				if ((pixel >> 24) != 0x00) { // check if pixel is not transparent
					if (x > rightMostPixel) {
						rightMostPixel = x;
					}
				}
			}
		}
		return (rightMostPixel + 1);
	}

	public static int calculateExactWidth(BufferedImage image) {
		int rightMostPixel = -1;
		int leftMostPixel = -1;
		for (int y = 0; y < image.getHeight(); y++) {
			for (int x = 0; x < image.getWidth(); x++) {
				int pixel = image.getRGB(x, y);
				if ((pixel >> 24) != 0x00) { // check if pixel is not transparent
					if (leftMostPixel == -1 || x < leftMostPixel) {
						leftMostPixel = x;
					}
					if (rightMostPixel == -1 || x > rightMostPixel) {
						rightMostPixel = x;
					}
				}
			}
		}
		return (rightMostPixel - leftMostPixel + 1);
	}

	public static class SizedChar {
		
		private static final double DEFAULT_WIDTH = -1d;
		private static final double DEFAULT_HEIGHT_RATIO = 1d;
		
		double overridingWidth = DEFAULT_WIDTH;
		
		// Character
		char character;

		// Height
		int height;
		int imageHeight;

		// Width
		private int imageWidth;
		private int absoluteWidth;
		private int exactWidth;
		
		// Other
		private boolean icon = false;
		
		public SizedChar(char character) {
			this.character = character;
		}
		
		public boolean hasOverridingWidth() {
			return overridingWidth != DEFAULT_WIDTH;
		}
		
		public double getOverridingWidth() {
			return overridingWidth;
		}
		
		public void setOverridingWidth(double width) {
			this.overridingWidth = width;
		}

		public char getCharacter() {
			return character;
		}

		public int getHeight() {
			return height;
		}

		public void setHeight(int height) {
			this.height = height;
		}

		public int getImageHeight() {
			return imageHeight;
		}

		public void setImageHeight(int imageHeight) {
			this.imageHeight = imageHeight;
		}

		public int getImageWidth() {
			return imageWidth;
		}

		public void setImageWidth(int imageWidth) {
			this.imageWidth = imageWidth;
		}
		
		public int getAbsoluteWidth() {
			return absoluteWidth;
		}
		
		public void setAbsoluteWidth(int absoluteWidth) {
			this.absoluteWidth = absoluteWidth;
		}
		
		public int getExactWidth() {
			return exactWidth;
		}
		
		public void setExactWidth(int exactWidth) {
			this.exactWidth = exactWidth;
		}
		
		public int getNegativeSpace() {
			return absoluteWidth - exactWidth;
		}
		
		public boolean isIcon() {
			return icon;
		}
		public void setIcon(boolean icon) {
			this.icon = icon;
		}
		
		public double getRealWidth() {
			if (hasOverridingWidth())
				return overridingWidth + 0.5;
			
			double ratio = getHeightRatio();
			double mult = (ratio * (double) absoluteWidth);
			double result = mult + 1;
			return result;
		}

		public double getHeightRatio() {
			if (hasOverridingWidth())
				return DEFAULT_HEIGHT_RATIO;
			
			if (imageHeight == 0)
				return 0;
			return ((double) height / (double) imageHeight);
		}
	}

}
