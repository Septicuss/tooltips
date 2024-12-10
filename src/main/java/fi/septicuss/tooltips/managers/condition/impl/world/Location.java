package fi.septicuss.tooltips.managers.condition.impl.world;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.condition.type.MultiLocation;
import fi.septicuss.tooltips.utils.validation.Validity;

public class Location implements Condition {

	private static final String[] LOCATION = { "l", "loc", "location" };

	@Override
	public boolean check(Player player, Arguments args) {
		MultiLocation locations = MultiLocation.of(player, args.get(LOCATION).getAsString());
		org.bukkit.Location playerLocation = player.getLocation();
		
		for (org.bukkit.Location location : locations.getLocations()) {
			if (playerLocation.getBlockX() == location.getBlockX() && 
				playerLocation.getBlockY() == location.getBlockY() && 
				playerLocation.getBlockZ() == location.getBlockZ()) {
				return true;
			}
		}
		
		return false;
	}

	@Override
	public Validity valid(Arguments args) {
		if (!args.has(LOCATION)) {
			return Validity.of(false, "Location argument is required");
		}

		Validity validity = MultiLocation.validityOf(args.get(LOCATION).getAsString());

		if (!validity.isValid()) {
			return validity;
		}

		return Validity.TRUE;
	}

	@Override
	public String id() {
		return "location";
	}
}
