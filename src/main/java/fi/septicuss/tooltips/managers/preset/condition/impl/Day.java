package fi.septicuss.tooltips.managers.preset.condition.impl;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.managers.preset.condition.Condition;
import fi.septicuss.tooltips.managers.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.validation.Validity;

public class Day implements Condition {

	@Override
	public boolean check(Player player, Arguments args) {
		long worldTime = player.getWorld().getTime();

		return (worldTime >= 0 && worldTime < 13000);
	}

	@Override
	public Validity valid(Arguments args) {
		return Validity.of(true);
	}

}
