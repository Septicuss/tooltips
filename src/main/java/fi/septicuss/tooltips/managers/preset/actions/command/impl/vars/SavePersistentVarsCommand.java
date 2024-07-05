package fi.septicuss.tooltips.managers.preset.actions.command.impl.vars;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.managers.preset.actions.command.ActionCommand;
import fi.septicuss.tooltips.managers.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.validation.Validity;
import fi.septicuss.tooltips.utils.variable.Variables;

public class SavePersistentVarsCommand extends VarCommand implements ActionCommand {

	@Override
	public void run(Player player, Arguments arguments) {
		Variables.PERSISTENT.save();
	}

	@Override
	public Validity validity(Arguments arguments) {
		return Validity.TRUE;
	}

}
