package fi.septicuss.tooltips.managers.tooltip.tasks;

import fi.septicuss.tooltips.utils.rays.Rays;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Task responsible for periodically clearing the caches
 */
public class CacheTask extends BukkitRunnable {

    @Override
    public void run() {

        Rays.clearCache();

    }

}
