package fi.septicuss.tooltips.object.preset.actions.command.impl.vars;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;

public abstract class VarCommand {

	public String[] convertArgumentsToArray(Arguments arguments) {
		String[] args = new String[arguments.size()];
		for (int i = 1; i <= arguments.size(); i++) {
			args[i - 1] = arguments.get(String.valueOf(i)).getAsString();
		}
		return args;
	}

	public String appendFrom(String[] args, int startIndex) {
		StringBuilder builder = new StringBuilder();
		for (int i = startIndex; i < args.length; i++)
			builder.append(args[i] + " ");
		return builder.toString().strip();
	}
	
	public Player getTarget(String scopeArgument) {
		return Bukkit.getPlayerExact(scopeArgument);
	}

	public boolean isGlobal(String scopeArgument) {
		return (scopeArgument.equalsIgnoreCase("global"));
	}

}
