package fi.septicuss.tooltips.object.preset.actions.command.impl;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.object.preset.actions.command.ActionCommand;
import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.object.validation.Validity;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;

public class MessageCommand implements ActionCommand {

	@Override
	public void run(Player player, Arguments arguments) {
		
		String message = appendFrom(convertArgumentsToArray(arguments), 0);
		message = Utils.color(Placeholders.replacePlaceholders(player, message));
		
		player.sendMessage(message);
		
	}

	@Override
	public Validity validity(Arguments arguments) {
		return Validity.TRUE;
	}

}
