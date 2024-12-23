package fi.septicuss.tooltips.managers.integration.impl.papi;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class PAPI {

	public static List<String> replacePlaceholders(Player player, List<String> text) {
		if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			return text;
		}

		return PlaceholderAPI.setPlaceholders(player, text);
	}

	public static String replacePlaceholders(Player player, String text) {
		if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI")) {
			return text;
		}

		return PlaceholderAPI.setPlaceholders(player, text);
	}

}
