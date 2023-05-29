package fi.septicuss.tooltips.object.preset.actions.command;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.object.preset.actions.command.impl.MessageCommand;
import fi.septicuss.tooltips.object.preset.actions.command.impl.SoundCommand;
import fi.septicuss.tooltips.object.preset.condition.argument.Argument;
import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.object.validation.Validity;
import fi.septicuss.tooltips.utils.Utils;

public class ActionCommands {

	private static final Map<String, ActionCommand> COMMANDS;

	static {
		COMMANDS = new HashMap<>();
		COMMANDS.put("sound", new SoundCommand());
		COMMANDS.put("message", new MessageCommand());
//		COMMANDS.put("setvar", new SetVarCommand(false));
//		COMMANDS.put("setpersistentvar", new SetVarCommand(true));
	}

	public static boolean isValidCommand(String fullCommand) {
		String first = getFirstArgument(fullCommand);

		for (var commandName : COMMANDS.keySet())
			if (first.equalsIgnoreCase(commandName))
				return true;

		return false;
	}

	public static void runCommand(Player player, String presetId, String fullCommand) {
		String[] split = fullCommand.split(" ");
		String first = split[0].toLowerCase();

		ActionCommand command = COMMANDS.get(first);

		if (command == null)
			return;

		Arguments arguments = new Arguments();

		for (int i = 1; i < split.length; i++) {
			arguments.add(String.valueOf(i), new Argument(split[i]));
		}

		Validity validity = command.validity(arguments);

		if (validity == null || !command.validity(arguments).isValid()) {
			Tooltips.warn(
					"Failed to run action command " + Utils.quote(first) + " in preset " + Utils.quote(presetId) + ":");
			if (validity.hasReason())
				Tooltips.warn("  -> " + validity.getReason());
			return;
		}

		command.run(player, arguments);
	}

	private static String getFirstArgument(String fullCommand) {
		String[] split = fullCommand.split(" ");
		return split[0];
	}

}
