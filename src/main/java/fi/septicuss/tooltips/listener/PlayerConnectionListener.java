package fi.septicuss.tooltips.listener;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.utils.cache.player.LookingAtCache;
import fi.septicuss.tooltips.utils.cache.tooltip.TooltipCache;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerConnectionListener implements Listener {

	@EventHandler
	public void on(PlayerQuitEvent event) {
		TooltipCache.remove(event.getPlayer());
		LookingAtCache.remove(event.getPlayer());
		Tooltips.get().getTooltipManager().removePlayerTooltipData(event.getPlayer());
	}
	
	
}
