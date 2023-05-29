package fi.septicuss.tooltips.object.preset.actions.command;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.object.validation.Validity;

public interface ActionCommand {

	public void run(Player player, Arguments arguments);
	
	public Validity validity(Arguments arguments);
	
	public default String getArgument(int index) {
		return String.valueOf(index);
	}
}
