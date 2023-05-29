package fi.septicuss.tooltips.listener;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import fi.septicuss.tooltips.integrations.AreaProvider;
import fi.septicuss.tooltips.utils.cache.area.CurrentAreaCache;

public class PlayerMovementListener implements Listener {

	private AreaProvider areaProvider;

	public PlayerMovementListener(AreaProvider areaProvider) {
		this.areaProvider = areaProvider;
	}

	@EventHandler
	public void on(PlayerMoveEvent event) {

		var from = event.getFrom();
		var to = event.getTo();

		if (!hasBlockChanged(from, to)) {
			return;
		}

		cacheApplicableAreas(event.getPlayer(), to);

	}

	private void cacheApplicableAreas(Player player, Location location) {
		List<String> applicable = areaProvider.getApplicableAreas(location);
		CurrentAreaCache.put(player, applicable);
	}

	private boolean hasBlockChanged(Location from, Location to) {
		return !(from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY()
				&& from.getBlockZ() == to.getBlockZ());
	}

}
