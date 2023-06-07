package fi.septicuss.tooltips.utils.placeholder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import fi.septicuss.tooltips.integrations.papi.PAPI;

public class Placeholders {

	private static Map<String, PlaceholderParser> LOCAL_PLACEHOLDERS = new HashMap<>();

	public static String replacePlaceholders(Player player, String str) {

		if (!str.contains("%")) {
			return str;
		}

		str = str.replace("%player%", player.getName());

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			char character = str.charAt(i);

			if (character == '%') {
				int from = i + 1;

				String sub = str.substring(from);
				int nextPercent = sub.indexOf('%');

				if (nextPercent == -1) {
					builder.append(character);
					continue;
				}

				int to = i + nextPercent + 1;
				String placeholder = str.substring(from, to);

				String result = null;

				for (var parser : LOCAL_PLACEHOLDERS.values()) {
					result = parser.parse(player, placeholder);
					if (result != null)
						break; // Found first
				}

				if (result != null) {
					builder.append(result);
					i = to;
				} else {
					builder.append(character);
				}

				continue;
			}

			builder.append(character);

		}

		return PAPI.replacePlaceholders(player, builder.toString());

	}

	public static List<String> replacePlaceholders(Player player, List<String> str) {
		List<String> result = Lists.newArrayList();
		str.forEach(line -> result.add(replacePlaceholders(player, line)));
		return result;
	}

	public static void addLocal(String key, PlaceholderParser placeholderParser) {
		LOCAL_PLACEHOLDERS.put(key, placeholderParser);
	}

	public static void removeLocal(String key) {
		LOCAL_PLACEHOLDERS.remove(key);
	}

	public static void clearLocal() {
		LOCAL_PLACEHOLDERS.clear();
	}

}
