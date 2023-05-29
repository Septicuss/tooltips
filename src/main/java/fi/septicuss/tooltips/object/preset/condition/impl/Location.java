package fi.septicuss.tooltips.object.preset.condition.impl;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.object.preset.condition.Condition;
import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.object.preset.condition.type.LocationArgument;
import fi.septicuss.tooltips.object.validation.Validity;

public class Location implements Condition {

	private static final String[] LOCATION = { "l", "loc", "location" };

	@Override
	public boolean check(Player player, Arguments args) {
		LocationArgument locationArg = args.get(LOCATION).getAsLocationArgument(player);
		org.bukkit.Location location = locationArg.getLocation();
		org.bukkit.Location playerLocation = player.getLocation();
		
		return (playerLocation.getBlockX() == location.getBlockX() && playerLocation.getBlockY() == location.getBlockY()
				&& playerLocation.getBlockZ() == location.getBlockZ());
	}

	@Override
	public Validity valid(Arguments args) {
		if (!args.has(LOCATION)) {
			return Validity.of(false, "Location argument is required");
		}

		Validity validity = LocationArgument.validityOf(args.get(LOCATION).getAsString());

		if (!validity.isValid()) {
			return validity;
		}

		return Validity.TRUE;
	}

}
