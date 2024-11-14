package fi.septicuss.tooltips.utils.placeholder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import fi.septicuss.tooltips.managers.integration.impl.papi.PAPI;

public class Placeholders {

	private static Map<String, PlaceholderParser> LOCAL_PLACEHOLDERS = new HashMap<>();

	public static String replacePlaceholders(Player player, String str) {

		if (!str.contains("%")) {
			return str;
		}

		str = str.replace("%player%", player.getName());

		StringBuilder builder = new StringBuilder();

		for (int i = 0; i < str.length(); i++) {
			char head = str.charAt(i);

			if (head == '%') {
				boolean found = false;

				for (int j = str.length() - 1; j > 0; j--) {
					if (i >= j) break;
					char tail = str.charAt(j);
					if (tail != '%')
						continue;

					String placeholder = str.substring(i + 1, j);
					String result = null;

					if (LOCAL_PLACEHOLDERS.containsKey(placeholder.toLowerCase())) {
						var parser = LOCAL_PLACEHOLDERS.get(placeholder.toLowerCase());
						result = parser.parse(player, placeholder);
					}

					if (result != null) {
						builder.append(result);
						found = true;
						i = j;
						break;
					}

				}

				if (!found) {
					builder.append(head);
				}

				continue;

			}

			builder.append(head);

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
