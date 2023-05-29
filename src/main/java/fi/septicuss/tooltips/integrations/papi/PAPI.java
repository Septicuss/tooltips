package fi.septicuss.tooltips.integrations.papi;

import java.util.List;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.integrations.IntegratedPlugin;
import me.clip.placeholderapi.PlaceholderAPI;

public class PAPI {

	public static List<String> replacePlaceholders(Player player, List<String> text) {
		if (!IntegratedPlugin.PAPI.isEnabled()) {
			return text;
		}

		return PlaceholderAPI.setPlaceholders(player, text);
	}

	public static String replacePlaceholders(Player player, String text) {
		if (!IntegratedPlugin.PAPI.isEnabled()) {
			return text;
		}

		return PlaceholderAPI.setPlaceholders(player, text);
	}

}
