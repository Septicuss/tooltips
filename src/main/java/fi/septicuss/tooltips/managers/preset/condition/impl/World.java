package fi.septicuss.tooltips.managers.preset.condition.impl;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.managers.preset.condition.Condition;
import fi.septicuss.tooltips.managers.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.preset.condition.type.MultiString;
import fi.septicuss.tooltips.utils.validation.Validity;

public class World implements Condition {

	private static final String[] WORLD = { "name" };

	@Override
	public boolean check(Player player, Arguments args) {
		MultiString multi = MultiString.of(args.get(WORLD).getAsString());
		return multi.contains(player.getWorld().getName());
	}

	@Override
	public Validity valid(Arguments args) {
		if (!args.has(WORLD)) {
			return Validity.of(false, "Must have the world name argument");
		}

		return Validity.TRUE;
	}

}
