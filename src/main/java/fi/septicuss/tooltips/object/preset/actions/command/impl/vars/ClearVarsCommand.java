package fi.septicuss.tooltips.object.preset.actions.command.impl.vars;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fi.septicuss.tooltips.object.preset.actions.command.ActionCommand;
import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.object.validation.Validity;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;
import fi.septicuss.tooltips.utils.variable.Variables;
import fi.septicuss.tooltips.utils.variable.Variables.VariableProvider;

public class ClearVarsCommand extends VarCommand implements ActionCommand {

	private boolean persistent;
	private String commandName;

	public ClearVarsCommand(boolean persistent) {
		this.persistent = persistent;
		this.commandName = (persistent ? "clearpersistentvars" : "clearvars");
	}

	@Override
	public void run(Player player, Arguments arguments) {
		String[] args = convertArgumentsToArray(arguments);

		final String scopeArgument = Placeholders.replacePlaceholders(player, args[0]);
		final boolean global = isGlobal(scopeArgument);

		Player target = getTarget(scopeArgument);

		if (target == null)
			target = player;

		// Clearing vars
		VariableProvider provider = (persistent ? Variables.PERSISTENT : Variables.LOCAL);

		if (global) {
			provider.clearAllVars();
			return;
		}

		provider.clearAllVars(target);
	}

	@Override
	public Validity validity(Arguments arguments) {
		String[] args = convertArgumentsToArray(arguments);

		if (args.length < 1)
			return Validity.of(false, "Not enough arguments; " + commandName + " (player/global)");

		String target = args[0];
		Player targetPlayer = Bukkit.getPlayerExact(target);

		if (!target.equalsIgnoreCase("global") && !target.equalsIgnoreCase("player") && targetPlayer == null)
			return Validity.of(false,
					"Scope must either be a players name, 'player' or 'global'. Now was " + Utils.quote(args[0]));

		return Validity.TRUE;
	}

}
