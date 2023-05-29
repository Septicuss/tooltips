package fi.septicuss.tooltips.utils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.md_5.bungee.api.ChatColor;

public class Colors {
	
	public static final String PLUGIN_COLOR = "#d33682";
	public static final String PLUGIN_COLOR_INFO = "#ffffff";
	public static final String PLUGIN_COLOR_WARN = "#852051";
	
	private static final char COLOR_CHAR = ChatColor.COLOR_CHAR;
	private static final Pattern PATTERN = Pattern.compile("#" + "([A-Fa-f0-9]{6})");

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

	public static String quote(String message) {
		return "\"" + message + "\"";
	}

	
}
