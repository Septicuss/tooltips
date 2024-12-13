package fi.septicuss.tooltips.managers.tooltip.tasks;

import fi.septicuss.tooltips.managers.integration.impl.axgens.AxGensCache;
import fi.septicuss.tooltips.utils.cache.furniture.FurnitureCache;
import fi.septicuss.tooltips.utils.cache.tooltip.TooltipCache;
import fi.septicuss.tooltips.utils.rays.Rays;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task responsible for periodically clearing the caches
 */
public class CacheTask extends BukkitRunnable {

    @Override
    public void run() {

        Rays.clearCache();
        TooltipCache.clear();
        FurnitureCache.clear();

        if (Bukkit.getPluginManager().isPluginEnabled("AxGens")) {
            AxGensCache.clear();
        }

    }

}
