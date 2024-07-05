package fi.septicuss.tooltips.managers.preset.condition.impl;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.managers.preset.condition.Condition;
import fi.septicuss.tooltips.managers.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.validation.Validity;

public class Sneaking implements Condition {

	@Override
	public boolean check(Player player, Arguments args) {
		return player.isSneaking();
	}

	@Override
	public Validity valid(Arguments args) {
		return Validity.TRUE;
	}

}
