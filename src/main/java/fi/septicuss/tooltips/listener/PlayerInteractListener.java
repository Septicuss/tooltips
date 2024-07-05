package fi.septicuss.tooltips.listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerInteractAtEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.EquipmentSlot;

import fi.septicuss.tooltips.managers.preset.actions.ActionProperties.TooltipAction;
import fi.septicuss.tooltips.tooltip.runnable.TooltipRunnableManager;

public class PlayerInteractListener implements Listener {

	private TooltipRunnableManager runnableManager;

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerInteractEvent event) {
		if (runnableManager == null) return;
		if (event.getHand() == EquipmentSlot.OFF_HAND) return;
		
		final Action eventAction = event.getAction();
		final Player player = event.getPlayer();
		
		if (eventAction == Action.LEFT_CLICK_AIR) {
			runnableManager.runActions(TooltipAction.LEFT_CLICK_AIR, player);
			runnableManager.runActions(TooltipAction.LEFT_CLICK, player);
		}
		
		if (eventAction == Action.LEFT_CLICK_BLOCK) {
			runnableManager.runActions(TooltipAction.LEFT_CLICK_BLOCK, player);
			runnableManager.runActions(TooltipAction.LEFT_CLICK, player);
		}

		if (eventAction == Action.RIGHT_CLICK_AIR) {
			runnableManager.runActions(TooltipAction.RIGHT_CLICK_AIR, player);
			runnableManager.runActions(TooltipAction.RIGHT_CLICK, player);
		}
		
		if (eventAction == Action.RIGHT_CLICK_BLOCK) {
			runnableManager.runActions(TooltipAction.RIGHT_CLICK_BLOCK, player);
			runnableManager.runActions(TooltipAction.RIGHT_CLICK, player);
		}
		
		
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerInteractAtEntityEvent event) {
		if (runnableManager == null) return;
		if (event.getHand() == EquipmentSlot.OFF_HAND) return;
		
		final Player player = event.getPlayer();
		runnableManager.runActions(TooltipAction.RIGHT_CLICK_ENTITY, player);
		runnableManager.runActions(TooltipAction.RIGHT_CLICK, player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(EntityDamageByEntityEvent event) {
		if (runnableManager == null) return;

		if (!(event.getDamager() instanceof Player player)) {
			return;
		}
		
		runnableManager.runActions(TooltipAction.LEFT_CLICK_ENTITY, player);
		runnableManager.runActions(TooltipAction.LEFT_CLICK, player);
	}
	
	public void setRunnableManager(TooltipRunnableManager runnableManager) {
		this.runnableManager = runnableManager;
	}

}
