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
	private static final char DELIMITER = '\uF0A2';

	/**
	 * Strip a path like meow.meow[3] from possible query-related features.
	 * <ul>
	 *     <li>meow.meow[3] -> meow.meow</li>
	 *     <li>meow.meow[length] -> meow.meow</li>
	 * </ul>
	 *
	 * @param path Path
	 * @return Stripped query path
	 */
	public static String stripQueryPath(String path) {
		if (!path.contains("[") ||!path.contains("]")) return path;

		String newPath = path;

		if (path.endsWith("]") && path.contains("[")) {
			int startIndex = path.lastIndexOf('[');
			int endIndex = path.lastIndexOf(']');

			final String contentInsideBrackets = path.substring(startIndex + 1, endIndex);

			if (Utils.isInteger(contentInsideBrackets) || contentInsideBrackets.equalsIgnoreCase("length")) {
				newPath = newPath.substring(0, startIndex);
			}
		}

		return newPath;
	}

	/**
	 * Used to query an object for information with a yaml-like path.
	 * Currently supported:
	 * <ul>
	 *     <li>Indexing lists, by having a [x] at the end</li>
	 *     <li>Length of lists, by having a [length] at the end</li>
	 * </ul>
	 * Right now supports indexing lists, by having a [x] at the end
	 *
	 * @param path Path
	 * @param object Object to be queried
	 * @return Object result of the query
	 */
	public static Object queryObject(String path, Object object) {
		if (object == null) return null;
		if (!path.contains("[") ||!path.contains("]")) return object;

		boolean length = false;
		int index = -1;

		if (path.endsWith("]") && path.contains("[")) {
			int startIndex = path.lastIndexOf('[');
			int endIndex = path.lastIndexOf(']');

			final String contentInsideBrackets = path.substring(startIndex + 1, endIndex);


			if (Utils.isInteger(contentInsideBrackets)) {
				index = Integer.parseInt(contentInsideBrackets);
			} else if (contentInsideBrackets.equalsIgnoreCase("length")) {
				length = true;
			}
		}

		if (object instanceof List<?> list) {
			if (length) {
				return list.size();
			}

			final boolean hasIndex = index != -1;
			final boolean indexInBounds = list.size() > index;

			if (hasIndex && indexInBounds) {
				return list.get(index).toString();
			}

			final List<String> stringList = new ArrayList<>();
			for (var value : list) {
				if (value == null) continue;
				stringList.add(value.toString());
			}
			return String.join(", ", stringList);
		}

		return object;
	}

	public static boolean isSurroundedByQuotes(String value) {
		if (value.startsWith("\"") && value.endsWith("\"")) {
			return true;
		}
		return value.startsWith("'") && value.endsWith("'");
	}

	public static String removeQuotes(String value) {
		return value.substring(1, value.length() - 1);
	}


	public static String[] splitStringQuotations(final String string, final char separator) {
		return placeDelimiters(string).split(String.valueOf(DELIMITER));
	}

	private static String placeDelimiters(final String string) {
		final StringBuilder modifiedStringBuilder = new StringBuilder(string);

		char previousCharacter = DELIMITER;
		char startingQuote = ' ';

		for (int i = 0; i < string.length(); i++) {
			final char character = string.charAt(i);

			boolean isStartingQuoteUnset = (startingQuote == ' ');
			boolean isQuoteCharacter = (character == '\"' || character == '\'');

			if (isStartingQuoteUnset && isQuoteCharacter) {
				startingQuote = character;
				previousCharacter = character;
				continue;
			}

			boolean charIsColon = (character == ',');
			boolean lastCharWasQuote = previousCharacter == startingQuote;

			if (isStartingQuoteUnset && charIsColon) {
				modifiedStringBuilder.setCharAt(i, DELIMITER);
			}

			if (!isStartingQuoteUnset && lastCharWasQuote && charIsColon) {
				modifiedStringBuilder.setCharAt(i, DELIMITER);
				startingQuote = ' ';
			}

			previousCharacter = character;
		}

		return modifiedStringBuilder.toString();
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
