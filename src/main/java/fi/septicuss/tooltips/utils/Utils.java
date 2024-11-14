package fi.septicuss.tooltips.utils;

import com.google.gson.JsonObject;
import net.md_5.bungee.api.ChatColor;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

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

	public static String withSuffix(long count) {
		if (count < 1000) return "" + count;
		int exp = (int) (Math.log(count) / Math.log(1000));
		return String.format("%.1f%c",
				count / Math.pow(1000, exp),
				"kMGTPE".charAt(exp-1));
	}
	
	public static Set<JsonObject> getJsonSetCopy(Set<JsonObject> set) {
		Set<JsonObject> copy = new HashSet<>();
		if (set == null) return copy;
		set.forEach(object -> copy.add(object.deepCopy()));
		return copy;
	}
	
	public static boolean isInteger(String str) {
	    if (str == null) {
	        return false;
	    }
	    int length = str.length();
	    if (length == 0) {
	        return false;
	    }
	    int i = 0;
	    if (str.charAt(0) == '-') {
	        if (length == 1) {
	            return false;
	        }
	        i = 1;
	    }
	    for (; i < length; i++) {
	        char c = str.charAt(i);
	        if (c < '0' || c > '9') {
	            return false;
	        }
	    }
	    return true;
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

	public static boolean sameAmountOfCharsIn(String string, char first, char second) {
		return (count(string, first) == count(string, second));
	}

	public static RayTraceResult getRayTrace(Player player, double maxDistance, Predicate<Block> validBlock,
			Predicate<Entity> entityFilter) {
		final Location eyeLocation = player.getEyeLocation();
		final Vector direction = eyeLocation.getDirection();

		var blockRay = player.getWorld().rayTraceBlocks(eyeLocation, direction, maxDistance, FluidCollisionMode.NEVER,
				false);

		if (blockRay == null || blockRay.getHitBlock() == null || !validBlock.test(blockRay.getHitBlock())) {
			var entityRay = player.getWorld().rayTraceEntities(eyeLocation, direction, maxDistance, 0, entityFilter);
			return entityRay;
		}

		return blockRay;

	}
	
	public static RayTraceResult getRayTraceResult(Player player, double maxDistance) {
		return getRayTraceResult(player, maxDistance, null);
	}
	
	public static RayTraceResult getRayTraceResult(Player player, double maxDistance, List<EntityType> validEntities) {
		final Location eyeLocation = player.getEyeLocation();
		final Vector direction = eyeLocation.getDirection();

		return player.getWorld().rayTrace(eyeLocation, direction, maxDistance, FluidCollisionMode.NEVER, false, 0, entity -> {
			if (entity == null) return false;
			if (entity.equals(player)) return false;
			if (validEntities == null || validEntities.isEmpty()) return true;
			return (validEntities.contains(entity.getType()));
		});
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
