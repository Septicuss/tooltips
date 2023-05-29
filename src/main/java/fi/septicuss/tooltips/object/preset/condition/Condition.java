package fi.septicuss.tooltips.object.preset.condition;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.object.validation.Validity;
import fi.septicuss.tooltips.utils.Utils;

public interface Condition {

	public boolean check(Player player, Arguments args);

	public Validity valid(Arguments args);

	default String quote(String message) {
		return Utils.quote(message);
	}
	
}
