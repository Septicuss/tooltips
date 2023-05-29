package fi.septicuss.tooltips.object.preset.condition.impl;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.object.preset.condition.Condition;
import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.object.preset.condition.type.LocationArgument;
import fi.septicuss.tooltips.object.validation.Validity;
import fi.septicuss.tooltips.utils.Cuboid;

public class InCuboid implements Condition {

	public static final String[] FIRST = { "first", "1", "firstpoint" };
	public static final String[] SECOND = { "second", "2", "secondpoint" };

	@Override
	public boolean check(Player player, Arguments args) {
		org.bukkit.Location first = args.get(FIRST).getAsLocationArgument(player).getLocation();
		org.bukkit.Location second = args.get(SECOND).getAsLocationArgument(player).getLocation();
		org.bukkit.Location current = player.getLocation();

		Cuboid cuboid = new Cuboid(first, second);
		return cuboid.isIn(current);
	}

	@Override
	public Validity valid(Arguments args) {

		if (!args.has(FIRST))
			return Validity.of(false, "First point is missing");

		Validity firstValidity = LocationArgument.validityOf(args.get(FIRST).getAsString());

		if (!firstValidity.isValid())
			return firstValidity;

		if (!args.has(SECOND))
			return Validity.of(false, "Second point is missing");

		Validity secondValidity = LocationArgument.validityOf(args.get(SECOND).getAsString());

		if (!secondValidity.isValid())
			return secondValidity;

		return Validity.TRUE;
	}

}
