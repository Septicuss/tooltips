package fi.septicuss.tooltips.managers.condition.impl.world;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.validation.Validity;

public class Night implements Condition {

	@Override
	public boolean check(Player player, Arguments args) {
		long worldTime = player.getWorld().getTime();

		return (worldTime >= 13000);
	}

	@Override
	public Validity valid(Arguments args) {
		return Validity.of(true);
	}

	@Override
	public String id() {
		return "night";
	}
}
