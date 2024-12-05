package fi.septicuss.tooltips.utils;

import com.google.common.collect.Lists;
import fi.septicuss.tooltips.managers.integration.impl.papi.PAPI;
import org.bukkit.entity.Player;

import java.util.List;

public class Placeholders {

	public static String replacePlaceholders(Player player, String str) {
		if (!str.contains("%")) {
			return str;
		}

		return PAPI.replacePlaceholders(player, str.replace("%player%", player.getName()));
	}

	public static List<String> replacePlaceholders(Player player, List<String> str) {
		final List<String> result = Lists.newArrayList();
		for (String line : str) {
			result.add(replacePlaceholders(player, line));
		}
		return result;
	}

}
