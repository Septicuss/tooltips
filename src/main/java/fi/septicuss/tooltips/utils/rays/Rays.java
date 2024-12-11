package fi.septicuss.tooltips.utils.rays;

import fi.septicuss.tooltips.Tooltips;
import org.bukkit.FluidCollisionMode;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;
import org.bukkit.util.Vector;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.BiFunction;
import java.util.function.Predicate;

public class Rays {

    public static final ConcurrentHashMap<Integer, RayTraceResult> CACHED_BLOCK_RAYS = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Integer, RayTraceResult> CACHED_ENTITY_RAYS = new ConcurrentHashMap<>();
    public static final ConcurrentHashMap<Integer, RayTraceResult> CACHED_FURNITURE_RAYS = new ConcurrentHashMap<>();


    public static void cachedRay(String id, Player player, RayTraceResult result) {

    }


    public static BiFunction<Entity, List<EntityType>, Predicate<Entity>> DEFAULT_ENTITY_FILTER = (source, filter) -> {
        return entity -> {
            if (entity == null) return false;
            if (entity.equals(source)) return false;
            if (filter == null || filter.isEmpty()) return true;
            return (filter.contains(entity.getType()));
        };
    };

    public static RayTraceResult combinedRayTrace(Player player, float distance) {
        return combinedRayTrace(player, distance, null, null);
    }

    public static RayTraceResult combinedRayTrace(Player player, float distance, Predicate<Block> blockFilter, Predicate<Entity> entityFilter) {
        return combinedRayTrace(player.getEyeLocation(), player.getEyeLocation().getDirection(), distance, blockFilter, entityFilter);
    }

    public static RayTraceResult combinedRayTrace(Location origin, Vector direction, float distance, Predicate<Block> blockFilter, Predicate<Entity> entityFilter) {
        final int hash = getHash(origin, direction, distance);
        if (CACHED_BLOCK_RAYS.containsKey(hash)) return CACHED_BLOCK_RAYS.get(hash);
        if (CACHED_ENTITY_RAYS.containsKey(hash)) return CACHED_ENTITY_RAYS.get(hash);

        final World world = origin.getWorld();
        if (world == null) return null;

        final RayTraceResult blockRayTrace = Rays.blockRayTrace(origin, direction, distance);

        if (blockRayTrace == null || blockRayTrace.getHitBlock() == null || !blockFilter.test(blockRayTrace.getHitBlock())) {
            return Rays.entityRayTrace(origin, direction, distance, entityFilter);
        }

        return blockRayTrace;
    }

    public static RayTraceResult blockRayTrace(Location origin, Vector direction, float distance) {
        final int hash = getHash(origin, direction, distance);
        if (CACHED_BLOCK_RAYS.containsKey(hash)) return CACHED_BLOCK_RAYS.get(hash);

        final World world = origin.getWorld();
        if (world == null) return null;

        final RayTraceResult result = world.rayTraceBlocks(
                origin,
                direction,
                distance,
                FluidCollisionMode.NEVER,
                false
        );

        Rays.cacheBlockResult(hash, result);
        return result;
    }

    public static RayTraceResult entityRayTrace(Location origin, Vector direction, float distance, Predicate<Entity> filter) {
        final int hash = getHash(origin, direction, distance);
        if (CACHED_ENTITY_RAYS.containsKey(hash)) return CACHED_ENTITY_RAYS.get(hash);

        final World world = origin.getWorld();
        if (world == null) return null;

        RayTraceResult result = world.rayTrace(origin, direction, distance, FluidCollisionMode.NEVER, true, 0.5, filter);

        Rays.cacheEntityResult(hash, result);
        return result;
    }

    public static RayTraceResult furnitureRayTrace(Player player, float distance) {
        final Location origin = player.getEyeLocation();
        final Vector direction = origin.getDirection();

        final int hash = getHash(origin, direction, distance);
        if (CACHED_FURNITURE_RAYS.containsKey(hash)) return CACHED_FURNITURE_RAYS.get(hash);

        final World world = origin.getWorld();
        if (world == null) return null;

        final Predicate<Entity> furniturePredicate = entity -> {
            for (final var provider : Tooltips.get().getIntegrationManager().getFurnitureProviders().values())
                if (provider.getFurniture(entity) != null) return true;
            return false;
        };

        final RayTraceResult result = world.rayTrace(origin, direction, distance, FluidCollisionMode.NEVER, true, 0.6, furniturePredicate);
        Rays.cacheFurnitureResult(hash, result);

        return result;
    }

    public static void clearCache() {
        CACHED_BLOCK_RAYS.clear();
        CACHED_ENTITY_RAYS.clear();
        CACHED_FURNITURE_RAYS.clear();
    }

    private static void cacheBlockResult(final int hash, final RayTraceResult result) {
        if (result == null) return;
        CACHED_BLOCK_RAYS.put(hash, result);
    }

    private static void cacheEntityResult(final int hash, final RayTraceResult result) {
        if (result == null) return;
        CACHED_ENTITY_RAYS.put(hash, result);
    }

    private static void cacheFurnitureResult(final int hash, final RayTraceResult result) {
        if (result == null) return;
        CACHED_FURNITURE_RAYS.put(hash, result);
    }


    private static int getHash(final Location origin, final Vector direction, final float distance) {
        return Objects.hash(origin, direction, distance);
    }

}
