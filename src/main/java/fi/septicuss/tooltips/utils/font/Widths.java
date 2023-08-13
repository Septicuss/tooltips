package fi.septicuss.tooltips.utils.font;

import java.awt.Color;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.List;

// Credit goes to https://github.com/Ehhthan for width size code
public class Widths {

	private static final HashMap<Character, SizedChar> WIDTH_MAP = new HashMap<>();

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

				SizedChar sizedChar = getSizedChar(character, sub, height);
				WIDTH_MAP.put(character, sizedChar);
			}
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

	/**
	 * Manually add a sized character
	 */
	public static void add(SizedChar sizedChar) {
		WIDTH_MAP.put(sizedChar.getCharacter(), sizedChar);
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

	@Deprecated
	public static int calculateAbsoluteWidth(BufferedImage image, int xFrom, int yFrom, int xTo, int yTo) {
		int width;
		for (width = xTo - 1; width > xFrom; width--) {
			for (int height = yFrom; height < yTo; height++) {
				if (new Color(image.getRGB(width, height), true).getAlpha() == 255) {
					return width - xFrom + 1;
				}
			}
		}

		return width - xFrom + 1;
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

	public static int calculateWidth(BufferedImage image) {
		return calculateAbsoluteWidth(image, 0, 0, image.getWidth(), image.getHeight());
	}

	public static class SizedChar {
		
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
			double ratio = getHeightRatio();
			double mult = (ratio * (double) absoluteWidth);
			double result = mult + 1;
			return result;
		}

		public double getHeightRatio() {
			if (imageHeight == 0)
				return 0;
			return ((double) height / (double) imageHeight);
		}
	}

}
