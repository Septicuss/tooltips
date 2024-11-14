package fi.septicuss.tooltips.utils.cache.furniture;

import fi.septicuss.tooltips.managers.integration.wrappers.FurnitureWrapper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FurnitureCache {

	private static final Map<String, FurnitureWrapper> CACHE_MAP = new HashMap<>();

	public static void cacheAll(List<FurnitureWrapper> list) {
		for (var furniture : list)
			cache(furniture);
	}
	
	public static void clear() {
		CACHE_MAP.clear();
	}

	public static void cache(FurnitureWrapper furniture) {
		CACHE_MAP.put(furniture.id(), furniture);
	}

	public static boolean contains(String id) {
		return CACHE_MAP.containsKey(id);
	}

	public static FurnitureWrapper getFurniture(String id) {
		return CACHE_MAP.get(id);
	}

}
