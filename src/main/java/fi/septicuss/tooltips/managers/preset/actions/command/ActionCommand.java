package fi.septicuss.tooltips.managers.preset.actions.command;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.validation.Validity;

public interface ActionCommand {

	void run(Player player, Arguments arguments);
	
	Validity validity(Arguments arguments);
	
	default String[] convertArgumentsToArray(Arguments arguments) {
		String[] args = new String[arguments.size()];
		for (int i = 1; i <= arguments.size(); i++) {
			args[i - 1] = arguments.get(String.valueOf(i)).getAsString();
		}
		return args;
	}

	default String appendFrom(String[] args, int startIndex) {
		StringBuilder builder = new StringBuilder();
		for (int i = startIndex; i < args.length; i++)
			builder.append(args[i] + " ");
		return builder.toString().strip();
	}
}
