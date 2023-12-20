package fi.septicuss.tooltips.utils;

import java.awt.Color;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.collect.Lists;

import net.md_5.bungee.api.ChatColor;

public class Colors {

	/**
	 * Credit to Phoenix616 for Color & gradient related utils
	 * https://github.com/Phoenix616/MineDown/blob/master/src/main/java/de/themoep/minedown/Util.java
	 */

	public static final String PLUGIN = "#d33682";
	public static final String INFO = "#ffffff";
	public static final String WARN = "#852051";

	private static final char COLOR_CHAR = ChatColor.COLOR_CHAR;
	private static final Pattern PATTERN = Pattern.compile("#" + "([A-Fa-f0-9]{6})");

	private static Map<Integer, List<ChatColor>> GRADIENT_CACHE = new HashMap<>();
	private static Map<ChatColor, Color> legacyColors = new LinkedHashMap<>();

	static {
		legacyColors.put(ChatColor.BLACK, new Color(0x000000));
		legacyColors.put(ChatColor.DARK_BLUE, new Color(0x0000AA));
		legacyColors.put(ChatColor.DARK_GREEN, new Color(0x00AA00));
		legacyColors.put(ChatColor.DARK_AQUA, new Color(0x00AAAA));
		legacyColors.put(ChatColor.DARK_RED, new Color(0xAA0000));
		legacyColors.put(ChatColor.DARK_PURPLE, new Color(0xAA00AA));
		legacyColors.put(ChatColor.GOLD, new Color(0xFFAA00));
		legacyColors.put(ChatColor.GRAY, new Color(0xAAAAAA));
		legacyColors.put(ChatColor.DARK_GRAY, new Color(0x555555));
		legacyColors.put(ChatColor.BLUE, new Color(0x05555FF));
		legacyColors.put(ChatColor.GREEN, new Color(0x55FF55));
		legacyColors.put(ChatColor.AQUA, new Color(0x55FFFF));
		legacyColors.put(ChatColor.RED, new Color(0xFF5555));
		legacyColors.put(ChatColor.LIGHT_PURPLE, new Color(0xFF55FF));
		legacyColors.put(ChatColor.YELLOW, new Color(0xFFFF55));
		legacyColors.put(ChatColor.WHITE, new Color(0xFFFFFF));
	}

	public static String color(String message) {
		Matcher matcher = PATTERN.matcher(message);
		StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);
		while (matcher.find()) {
			String group = matcher.group(1);
			matcher.appendReplacement(buffer,
					COLOR_CHAR + "x" + COLOR_CHAR + group.charAt(0) + COLOR_CHAR + group.charAt(1) + COLOR_CHAR
							+ group.charAt(2) + COLOR_CHAR + group.charAt(3) + COLOR_CHAR + group.charAt(4) + COLOR_CHAR
							+ group.charAt(5));
		}

		String processedString = ChatColor.translateAlternateColorCodes('&', matcher.appendTail(buffer).toString());
		return processedString;
	}

	public static List<ChatColor> parseGradientColors(int length, String gradientColor) {
		List<ChatColor> gradientColors = Lists.newArrayList();
		String[] colors = gradientColor.split("-");
		
		for (String color : colors) {
			ChatColor chatColor = ChatColor.of(color);
			gradientColors.add(chatColor);
		}
		
		return gradientColors;
	}
	
	public static List<ChatColor> createGradient(int length, List<ChatColor> gradient) {

		int hash = hashGradientParams(length, gradient);

		if (GRADIENT_CACHE.containsKey(hash)) {
			return GRADIENT_CACHE.get(hash);
		}

		List<ChatColor> colors = Lists.newArrayList();
		if (gradient.size() < 2 || length < 2) {
			if (gradient.isEmpty()) {
				return gradient;
			}
			return Collections.singletonList(gradient.get(0));
		}

		float fPhase = 0;

		float sectorLength = (float) (length - 1) / (gradient.size() - 1);
		float factorStep = 1.0f / (sectorLength);

		long index = 0;

		int colorIndex = 0;

		for (long i = 0; i < length; i++) {

			if (factorStep * index > i) {
				colorIndex++;
				index = 0;
			}

			float factor = factorStep * (index++ + fPhase);

			if (factor > 1) {
				factor = 1 - (factor - 1);
			}

			Color color = interpolate(
					getColor(gradient.get(colorIndex)),
					getColor(gradient.get(Math.min(gradient.size() - 1, colorIndex + 1))),
					factor
			);
			
			
			if (color != null) {
				colors.add(ChatColor.of(color));
			}

		}

		GRADIENT_CACHE.put(hash, colors);

		return colors;

	}
	

	private static Color getColor(ChatColor color) {
		if (legacyColors.containsKey(color)) {
			return legacyColors.get(color);
		}

		if (color.getName().startsWith("#")) {
			return new Color(Integer.parseInt(color.getName().substring(1), 16));
		}

		return null;
	}

	private static Color interpolate(Color color1, Color color2, float factor) {
		if (color1 == null || color2 == null) {
			return null;
		}
		return new Color(Math.round(color1.getRed() + factor * (color2.getRed() - color1.getRed())),
				Math.round(color1.getGreen() + factor * (color2.getGreen() - color1.getGreen())),
				Math.round(color1.getBlue() + factor * (color2.getBlue() - color1.getBlue())));
	}

	private static int hashGradientParams(int length, List<ChatColor> gradient) {
		return Objects.hash(length, gradient);
	}

	public static String quote(String message) {
		return "\"" + message + "\"";
	}

}
