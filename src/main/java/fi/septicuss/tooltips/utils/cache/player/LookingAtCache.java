package fi.septicuss.tooltips.utils.cache.player;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;
import org.bukkit.entity.Player;

public class LookingAtCache {

	private static final Map<LookingAtKey, String> yawMap = new HashMap<>();

	public static boolean contains(Player player) {
		return (yawMap.containsKey(getKey(player)));
	}

	public static String get(Player player) {
		return (yawMap.get(getKey(player)));
	}
	
	public static void put(Player player, String value) {
		yawMap.put(getKey(player), value);
	}
	
	private static LookingAtKey getKey(Player player) {
		UUID uuid = player.getUniqueId();
		Location location = player.getLocation();
		return new LookingAtKey(uuid, location.getX(), location.getY(), location.getZ(), location.getYaw(), location.getPitch());
	}

	private static record LookingAtKey(UUID uuid, double x, double y, double z, float yaw, float pitch) {

	}

}
