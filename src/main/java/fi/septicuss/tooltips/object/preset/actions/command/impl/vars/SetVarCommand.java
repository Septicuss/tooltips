package fi.septicuss.tooltips.object.preset.actions.command.impl.vars;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import fi.septicuss.tooltips.object.preset.actions.command.ActionCommand;
import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.object.validation.Validity;
import fi.septicuss.tooltips.utils.Expr;
import fi.septicuss.tooltips.utils.Expr.Builder;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;
import fi.septicuss.tooltips.utils.variable.Variables;
import fi.septicuss.tooltips.utils.variable.Variables.VariableProvider;

public class SetVarCommand extends VarCommand implements ActionCommand {

	/**
	 * Protects the expression parser from failing when the expression looks like
	 * this: '1+'
	 */
	private static final String PROTECTIVE_BUFFER = "+0 ";

	private boolean persistent = false;
	private String commandName = "setvar";

	public SetVarCommand(boolean persistent) {
		this.persistent = persistent;
		this.commandName = persistent ? "setpersistentvar" : "setvar";
	}

	@Override
	public void run(Player player, Arguments arguments) {
		String[] args = convertArgumentsToArray(arguments);

		// Determine variables scope & name
		final String scopeArgument = replacePlaceholders(player, args[0]);

		boolean global = isGlobal(scopeArgument);
		Player target = getTarget(scopeArgument);

		if (target == null)
			target = player;

		final String variableName = replacePlaceholders(target, args[1]);

		// Value is the value that this variable will take on
		String value = appendFrom(args, 2);
		value = replacePlaceholders(target, value);

		// Parse possible mathematical expressions in the value ( 1+1 for example )
		final Builder expressionBuilder = new Expr.Builder();

		if (needsParsing(expressionBuilder, value)) {
			float result = parse(expressionBuilder, value);

			if (result % 1 == 0)
				value = String.valueOf(((int) result));
			else
				value = String.valueOf(result);

		}

		// Setting the variable
		VariableProvider provider = (persistent ? Variables.PERSISTENT : Variables.LOCAL);

		if (global) {
			provider.setVar(variableName, value);
			return;
		}

		provider.setVar(target, variableName, value);
	}

	@Override
	public Validity validity(Arguments arguments) {
		String[] args = convertArgumentsToArray(arguments);

		if (args.length < 3)
			return Validity.of(false, "Not enough arguments; " + commandName + " (player/global) name value");

		String target = args[0];
		Player targetPlayer = Bukkit.getPlayerExact(target);

		if (!target.equalsIgnoreCase("global") && !target.equalsIgnoreCase("player") && targetPlayer == null)
			return Validity.of(false,
					"Scope must either be a players name, 'player' or 'global'. Now was " + Utils.quote(args[0]));

		return Validity.TRUE;
	}

	private boolean needsParsing(Builder expressionBuilder, String value) {
		final List<String> tokens = expressionBuilder.tokenize(value);
		final Expr expression = expressionBuilder.parse(value + PROTECTIVE_BUFFER, null, null);

		boolean fullyNumeric = true;

		if (tokens != null)
			for (var token : tokens) {
				if (Builder.OPS.containsKey(token))
					continue;
				if (!Utils.isNumeric(token)) {
					fullyNumeric = false;
					break;
				}
			}

		if (tokens == null || expression == null || !fullyNumeric) {
			return false;
		}

		return true;
	}

	private float parse(Builder expressionBuilder, String value) {
		return expressionBuilder.parse(value, null, null).eval();
	}

	private String replacePlaceholders(Player target, String value) {
		if (target == null)
			return value;
		
		value = value.replace("%player%", target.getName());
		value = Placeholders.replacePlaceholders(target, value);
		return value;
	}

}
