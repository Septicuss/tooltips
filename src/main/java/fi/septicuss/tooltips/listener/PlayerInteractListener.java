package fi.septicuss.tooltips.listener;

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

	private TooltipManager tooltipManager;

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerInteractEvent event) {
		if (tooltipManager == null) return;
		if (event.getHand() == EquipmentSlot.OFF_HAND) return;
		
		final Action eventAction = event.getAction();
		final Player player = event.getPlayer();
		
		if (eventAction == Action.LEFT_CLICK_AIR) {
			tooltipManager.runActions(DefaultTooltipAction.LEFT_CLICK_AIR, player);
			tooltipManager.runActions(DefaultTooltipAction.LEFT_CLICK, player);
		}
		
		if (eventAction == Action.LEFT_CLICK_BLOCK) {
			tooltipManager.runActions(DefaultTooltipAction.LEFT_CLICK_BLOCK, player);
			tooltipManager.runActions(DefaultTooltipAction.LEFT_CLICK, player);
		}

		if (eventAction == Action.RIGHT_CLICK_AIR) {
			tooltipManager.runActions(DefaultTooltipAction.RIGHT_CLICK_AIR, player);
			tooltipManager.runActions(DefaultTooltipAction.RIGHT_CLICK, player);
		}
		
		if (eventAction == Action.RIGHT_CLICK_BLOCK) {
			tooltipManager.runActions(DefaultTooltipAction.RIGHT_CLICK_BLOCK, player);
			tooltipManager.runActions(DefaultTooltipAction.RIGHT_CLICK, player);
		}
		
		
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(PlayerInteractAtEntityEvent event) {
		if (tooltipManager == null) return;
		if (event.getHand() == EquipmentSlot.OFF_HAND) return;
		
		final Player player = event.getPlayer();
		tooltipManager.runActions(DefaultTooltipAction.RIGHT_CLICK_ENTITY, player);
		tooltipManager.runActions(DefaultTooltipAction.RIGHT_CLICK, player);
	}

	@EventHandler(priority = EventPriority.LOWEST)
	public void on(EntityDamageByEntityEvent event) {
		if (tooltipManager == null) return;

		if (!(event.getDamager() instanceof Player player)) {
			return;
		}
		
		tooltipManager.runActions(DefaultTooltipAction.LEFT_CLICK_ENTITY, player);
		tooltipManager.runActions(DefaultTooltipAction.LEFT_CLICK, player);
	}
	
	public void setTooltipManager(TooltipManager tooltipManager) {
		this.tooltipManager = tooltipManager;
	}

}
