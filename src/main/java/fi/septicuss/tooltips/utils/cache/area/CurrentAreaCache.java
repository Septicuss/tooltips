package fi.septicuss.tooltips.utils.cache.area;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.bukkit.entity.Player;

public class CurrentAreaCache {

	private static final Map<UUID, List<String>> CACHED_AREAS = new HashMap<>();

	public static void put(Player player, List<String> areaIds) {
		final UUID uuid = player.getUniqueId();

		if (areaIds == null || areaIds.isEmpty()) {
			CACHED_AREAS.remove(uuid);
			return;
		}

		CACHED_AREAS.put(uuid, areaIds);
	}

	public static List<String> get(Player player) {
		final UUID uuid = player.getUniqueId();

		if (!CACHED_AREAS.containsKey(uuid)) {
			return null;
		}

		return Collections.unmodifiableList(CACHED_AREAS.get(uuid));
	}

	public static boolean has(Player player) {
		final UUID uuid = player.getUniqueId();
		if (!CACHED_AREAS.containsKey(uuid)) {
			return false;
		}
		return CACHED_AREAS.containsKey(uuid);
	}

}
