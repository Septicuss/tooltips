package fi.septicuss.tooltips.managers.condition.impl.player;

import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.entity.Player;

public class Op implements Condition {

	@Override
	public boolean check(Player player, Arguments args) {
		return player.isOp();
	}

	@Override
	public void writeContext(Player player, Arguments args, Context context) {
		context.put("op", player.isOp());
	}

	@Override
	public Validity valid(Arguments args) {
		return Validity.TRUE;
	}

	@Override
	public String id() {
		return "op";
	}
}
