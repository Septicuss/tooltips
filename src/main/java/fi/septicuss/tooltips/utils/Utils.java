package fi.septicuss.tooltips.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

import fi.septicuss.tooltips.integrations.FurnitureProvider;
import fi.septicuss.tooltips.utils.cache.furniture.FurnitureCache;
import net.md_5.bungee.api.ChatColor;

public class Utils {

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

	public static boolean isNumeric(String str) {
		return str.matches("-?\\d+(\\.\\d+)?"); // match a number with optional '-' and decimal.
	}

	public static int count(String string, char character) {
		int amount = 0;
		for (var ch : string.toCharArray())
			if (ch == character)
				amount++;
		return amount;
	}
	
	public static String getFurnitureDisplayName(FurnitureProvider provider, String id) {
		return ChatColor.stripColor(FurnitureCache.getFurniture(id).displayName());
	}

	public static boolean sameAmountOfCharsIn(String string, char first, char second) {
		return (count(string, first) == count(string, second));
	}
	public static Entity getEntityPlayerIsLookingAt(Player player, double maxDistance, double raySize,
			List<EntityType> validEntities) {
		RayTraceResult result = player.getWorld().rayTraceEntities(player.getEyeLocation(),
				player.getEyeLocation().getDirection(), maxDistance, e -> {
					if (e.equals(player))
						return false;
					if (validEntities == null || validEntities.isEmpty())
						return true;
					return (validEntities.contains(e.getType()));
				});
		return result == null ? null : result.getHitEntity();
	}

	public static List<String> color(List<String> message) {
		List<String> result = new ArrayList<>();
		message.forEach(line -> result.add(color(line)));
		return result;
	}

	public static List<String> stripColor(List<String> message) {
		List<String> result = new ArrayList<>();
		message.forEach(line -> result.add(ChatColor.stripColor(line)));
		return result;
	}

	public static <T extends Enum<T>> boolean enumExists(Class<T> enumType, String name) {
		for (T constant : enumType.getEnumConstants()) {
			if (constant.name().compareToIgnoreCase(name) == 0) {
				return true;
			}
		}
		return false;
	}

}
