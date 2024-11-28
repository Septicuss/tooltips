package fi.septicuss.tooltips.managers.condition;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.validation.Validity;

public interface Condition {

	default boolean check(Player player, Arguments args) {
		return false;
	}

	default void writeContext(Player player, Arguments args, Context context) { }

	Validity valid(Arguments args);

	String id();

	default String quote(String message) {
		return Utils.quote(message);
	}
	
}
