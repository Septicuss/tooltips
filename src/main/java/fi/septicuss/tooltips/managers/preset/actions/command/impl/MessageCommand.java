package fi.septicuss.tooltips.managers.preset.actions.command.impl;

import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.preset.actions.command.ActionCommand;
import fi.septicuss.tooltips.utils.AdventureUtils;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.entity.Player;

public class MessageCommand implements ActionCommand {

	@Override
	public void run(Player player, Arguments arguments) {

		String message = appendFrom(convertArgumentsToArray(arguments), 0);
		AdventureUtils.sendMessage(player, message);

	}

	@Override
	public Validity validity(Arguments arguments) {
		return Validity.TRUE;
	}

}
