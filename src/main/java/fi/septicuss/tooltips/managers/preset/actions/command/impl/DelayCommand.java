package fi.septicuss.tooltips.managers.preset.actions.command.impl;

import java.util.ArrayList;
import java.util.List;

import fi.septicuss.tooltips.utils.Text;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.preset.actions.command.ActionCommand;
import fi.septicuss.tooltips.managers.preset.actions.command.ActionCommands;
import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;
import fi.septicuss.tooltips.utils.validation.Validity;

public class DelayCommand implements ActionCommand {

	@Override
	public void run(Player player, Arguments arguments) {
		
		int ticks = arguments.get("1").getAsInt();
		String commandString = appendFrom(convertArgumentsToArray(arguments), 1);
		
		List<String> commandList = new ArrayList<>();

		for (var command : commandString.split(";")) {
			commandList.add(Text.processText(player, command.strip()));
		}
		
		Bukkit.getScheduler().runTaskLater(Tooltips.get(), () -> {
			for (var command : commandList) {
				
				if (ActionCommands.isValidCommand(command)) {
					ActionCommands.runCommand(player, "none", command);
					continue;
				}
				
				Bukkit.dispatchCommand(Bukkit.getConsoleSender(), command);
			}
		}, ticks);
	
		
		
	}

	@Override
	public Validity validity(Arguments arguments) {
		if (!arguments.has("2")) {
			return Validity.of(false, "Not enough arguments: delay (ticks) (command);(other command)");
		}
		
		Argument timeArg = arguments.get("1");
		
		if (!timeArg.isNumber()) {
			return Validity.of(false, "Ticks must be a number");
		}
		
		return Validity.TRUE;
	}

}
