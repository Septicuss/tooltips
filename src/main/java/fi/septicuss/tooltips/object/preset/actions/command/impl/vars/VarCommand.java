package fi.septicuss.tooltips.object.preset.actions.command.impl.vars;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public abstract class VarCommand {

	public Player getTarget(String scopeArgument) {
		return Bukkit.getPlayerExact(scopeArgument);
	}

	public boolean isGlobal(String scopeArgument) {
		return (scopeArgument.equalsIgnoreCase("global"));
	}

}
