package fi.septicuss.tooltips.object.preset.actions.command.impl.vars;

import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;

public abstract class VarCommand {

	public String[] convertArgumentsToArray(Arguments arguments) {
		String[] args = new String[arguments.size()];
		for (int i = 1; i <= arguments.size(); i++) {
			args[i - 1] = arguments.get(String.valueOf(i)).getAsString();
		}
		return args;
	}

	public boolean isGlobal(String scopeArgument) {
		return (scopeArgument.equalsIgnoreCase("global"));
	}

	public boolean isValidScope(String scopeArgument) {
		return (scopeArgument.equalsIgnoreCase("player") || scopeArgument.equalsIgnoreCase("global"));
	}

}
