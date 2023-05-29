package fi.septicuss.tooltips.object.preset.actions.command.impl.vars;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.object.preset.actions.command.ActionCommand;
import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.object.validation.Validity;
import fi.septicuss.tooltips.utils.Expr;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;

public class SetVarCommand extends VarCommand implements ActionCommand {

	private boolean persistent = false;
	private String commandName = "setvar";

	public SetVarCommand(boolean persistent) {
		this.persistent = persistent;
		this.commandName = persistent ? "setpersistentvar" : "setvar";
	}

	@Override
	public void run(Player player, Arguments arguments) {
		String[] args = convertArgumentsToArray(arguments);

		boolean global = isGlobal(args[0]);
		String varName = args[1];

		StringBuilder valueBuilder = new StringBuilder();
		for (int i = 2; i < args.length; i++) {
			valueBuilder.append(args[i] + " ");
		}

		String value = valueBuilder.toString().strip().replace("%player%", player.getName());
		value = Placeholders.replacePlaceholders(player, value);

		var builder = new Expr.Builder();
		var expression = builder.parse(value, null, null);

		Object finalValue = null;

		if (expression == null) {
			finalValue = value;
		} else {
			float numericalResult = expression.eval();
			finalValue = numericalResult;
		}

		player.sendMessage("Final value = " + finalValue.toString());

	}

	@Override
	public Validity validity(Arguments arguments) {
		String[] args = convertArgumentsToArray(arguments);

		if (args.length < 3)
			return Validity.of(false, "Not enough arguments; " + commandName + " (player/global) name value");

		if (!isValidScope(args[0]))
			return Validity.of(false, "Scope must either be player or global, now was " + Utils.quote(args[0]));

		return Validity.TRUE;
	}

}
