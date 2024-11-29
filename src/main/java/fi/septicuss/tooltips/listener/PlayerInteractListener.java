package fi.septicuss.tooltips.listener;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.preset.actions.DefaultTooltipAction;
import fi.septicuss.tooltips.managers.tooltip.TooltipManager;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

public class PlayerInteractListener implements Listener {

	private final Tooltips plugin;

	public PlayerInteractListener(Tooltips plugin) {
		this.plugin = plugin;
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerInteractEvent event) {
		final TooltipManager manager = getManager();

		if (manager == null) return;
		if (event.getHand() == EquipmentSlot.OFF_HAND) return;
		
		final Action eventAction = event.getAction();
		final Player player = event.getPlayer();
		
		if (eventAction == Action.LEFT_CLICK_AIR) {
			manager.runActions(DefaultTooltipAction.LEFT_CLICK_AIR, player);
			manager.runActions(DefaultTooltipAction.LEFT_CLICK, player);
		}
		
		if (eventAction == Action.LEFT_CLICK_BLOCK) {
			manager.runActions(DefaultTooltipAction.LEFT_CLICK_BLOCK, player);
			manager.runActions(DefaultTooltipAction.LEFT_CLICK, player);
		}

		if (eventAction == Action.RIGHT_CLICK_AIR) {
			manager.runActions(DefaultTooltipAction.RIGHT_CLICK_AIR, player);
			manager.runActions(DefaultTooltipAction.RIGHT_CLICK, player);
		}
		
		if (eventAction == Action.RIGHT_CLICK_BLOCK) {
			manager.runActions(DefaultTooltipAction.RIGHT_CLICK_BLOCK, player);
			manager.runActions(DefaultTooltipAction.RIGHT_CLICK, player);
		}
		
		
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerInteractAtEntityEvent event) {
		final TooltipManager manager = getManager();

		if (manager == null) return;
		if (event.getHand() == EquipmentSlot.OFF_HAND) return;
		
		final Player player = event.getPlayer();
		manager.runActions(DefaultTooltipAction.RIGHT_CLICK_ENTITY, player);
		manager.runActions(DefaultTooltipAction.RIGHT_CLICK, player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(EntityDamageByEntityEvent event) {
		final TooltipManager manager = getManager();

		if (manager == null) return;

		if (!(event.getDamager() instanceof Player player)) {
			return;
		}
		
		manager.runActions(DefaultTooltipAction.LEFT_CLICK_ENTITY, player);
		manager.runActions(DefaultTooltipAction.LEFT_CLICK, player);
	}

	private TooltipManager getManager() {
		return plugin.getTooltipManager();
	}

}
