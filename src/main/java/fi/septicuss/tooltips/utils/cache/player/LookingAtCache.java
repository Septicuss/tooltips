package fi.septicuss.tooltips.utils.cache.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class LookingAtCache {

	private static final Map<UUID, String> lookingAtFurnitureIdMap = new HashMap<>();

	public static boolean contains(Player player) {
		return (lookingAtFurnitureIdMap.containsKey(player.getUniqueId()));
	}

	public static String get(Player player) {
		return (lookingAtFurnitureIdMap.get(player.getUniqueId()));
	}

	public static void put(Player player, String value) {
		lookingAtFurnitureIdMap.put(player.getUniqueId(), value);
	}
	
	public static void remove(Player player) {
		lookingAtFurnitureIdMap.remove(player.getUniqueId());
	}

}
