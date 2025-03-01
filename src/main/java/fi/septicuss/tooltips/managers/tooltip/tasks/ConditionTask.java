package fi.septicuss.tooltips.managers.tooltip.tasks;

import fi.septicuss.tooltips.managers.tooltip.TooltipManager;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.PlayerTooltipData;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Manages:
 * - checking which preset should be shown to a given player based on conditions
 * - saving the data provided by conditions (context)
 */
public class ConditionTask extends BukkitRunnable {

    private final TooltipManager manager;

    public ConditionTask(final TooltipManager tooltipManager) {
        this.manager = tooltipManager;
    }

    @Override
    public void run() {

        var onlinePlayers = Bukkit.getOnlinePlayers();
        if (onlinePlayers.isEmpty())
            return;

        for (Player player : onlinePlayers) {

            final PlayerTooltipData data = manager.getPlayerTooltipData(player);

            for (var holderEntry : manager.getHolders().entrySet()) {
                var id = holderEntry.getKey();
                var holder = holderEntry.getValue();

                data.clearWorkingContext();
                data.setCheckedPreset(id);

                final boolean conditionResult = holder.evaluate(player, data.getWorkingContext());

                final boolean hasCurrentPreset = data.hasCurrentPreset();
                final boolean isSamePreset = hasCurrentPreset && data.getCurrentPreset().equals(id);

                /* Handling the same preset */

                if (isSamePreset) {
                    // Same preset true again, no actions taken
                    if (conditionResult) {
                        data.clearPendingContext();
                        data.updatePendingContext(data.getWorkingContext());
                        break;
                    }

                    // Same preset false, reset current preset
                    data.setCurrentPreset(null);
                    continue;
                }

                /* Handling a new preset */
                if (conditionResult) {
                    data.clearPendingContext();
                    data.updatePendingContext(data.getWorkingContext());
                    data.setCurrentPreset(id);
                    break;
                }
            }



        }
    }


}
