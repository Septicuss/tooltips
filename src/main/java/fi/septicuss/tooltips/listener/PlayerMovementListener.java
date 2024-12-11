package fi.septicuss.tooltips.listener;

import java.util.ArrayList;
import java.util.List;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.integration.IntegrationManager;
import fi.septicuss.tooltips.managers.integration.providers.AreaProvider;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import fi.septicuss.tooltips.utils.cache.area.CurrentAreaCache;

public class PlayerMovementListener implements Listener {

	private final IntegrationManager integrationManager;

	public PlayerMovementListener(IntegrationManager integrationManager) {
		this.integrationManager = integrationManager;
	}

	@EventHandler
	public void on(PlayerMoveEvent event) {

		var from = event.getFrom();
		var to = event.getTo();

		if (!hasBlockChanged(from, to)) {
			return;
		}

		if (!integrationManager.getAreaProviders().isEmpty()) {
			cacheApplicableAreas(event.getPlayer(), to);
		}

	}

	private void cacheApplicableAreas(Player player, Location location) {
		List<String> applicable = new ArrayList<>();

		for (AreaProvider provider : integrationManager.getAreaProviders().values()) {
			final List<String> providerAreas = provider.getApplicableAreas(location);

			if (providerAreas == null) {
				continue;
			}

			for (String area : providerAreas) {
				applicable.add(provider.identifier().toLowerCase() + ":" + area);
			}
		}

		CurrentAreaCache.put(player, applicable);
	}

	private boolean hasBlockChanged(Location from, Location to) {
		return !(from.getBlockX() == to.getBlockX() && from.getBlockY() == to.getBlockY()
				&& from.getBlockZ() == to.getBlockZ());
	}

}
