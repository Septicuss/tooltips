package fi.septicuss.tooltips.managers.condition.impl.player;

import fi.septicuss.tooltips.managers.condition.Context;
import org.bukkit.entity.Player;

import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.validation.Validity;

public class Sneaking implements Condition {

	@Override
	public boolean check(Player player, Arguments args) {
		return player.isSneaking();
	}

	@Override
	public void writeContext(Player player, Arguments args, Context context) {
		context.put("sneaking", player.isSneaking());
	}

	@Override
	public Validity valid(Arguments args) {
		return Validity.TRUE;
	}

	@Override
	public String id() {
		return "sneaking";
	}
}
