package fi.septicuss.tooltips.managers.integration.impl.nexo;

import com.nexomc.nexo.api.events.furniture.NexoFurnitureInteractEvent;
import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.preset.actions.DefaultTooltipAction;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import java.util.Optional;

public class NexoListener implements Listener {

    private final Tooltips plugin;

    public NexoListener(Tooltips plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onFurnitureInteract(NexoFurnitureInteractEvent event) {
        Optional.ofNullable(plugin.getTooltipManager()).ifPresent(tooltipManager -> {
            tooltipManager.runActions(DefaultTooltipAction.RIGHT_CLICK, event.getPlayer());
        });
    }

}
