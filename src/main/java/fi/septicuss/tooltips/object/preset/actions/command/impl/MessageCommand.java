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
		
		StringBuilder builder = new StringBuilder();
		
		for (int i = 1; i <= arguments.size(); i++) {
			builder.append(arguments.get(String.valueOf(1)).getAsString());
		}
		
		String message = Utils.color(Placeholders.replacePlaceholders(player, builder.toString()));
		player.sendMessage(message);
		
	}

	@Override
	public Validity validity(Arguments arguments) {
		return Validity.TRUE;
	}

}
