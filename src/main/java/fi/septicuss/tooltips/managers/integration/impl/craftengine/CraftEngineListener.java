package fi.septicuss.tooltips.managers.integration.impl.craftengine;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.preset.actions.DefaultTooltipAction;
import net.momirealms.craftengine.bukkit.api.event.FurnitureInteractEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class CraftEngineListener implements Listener {

    private final Tooltips plugin;

    public CraftEngineListener(Tooltips plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFurnitureInteract(FurnitureInteractEvent event) {
        Optional.ofNullable(plugin.getTooltipManager()).ifPresent(tooltipManager -> {
            tooltipManager.runActions(DefaultTooltipAction.RIGHT_CLICK, event.getPlayer());
        });
    }
}
