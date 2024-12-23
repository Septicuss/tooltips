package fi.septicuss.tooltips.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public interface TooltipsSubCommand {
	
	void onCommand(CommandSender sender, Command command, String label, String[] args);

	/**
	 * @param sender
	 * @param command
	 * @param label
	 * @param relativeArgs Inside brackets = /tt vars [arg arg]
	 * @return
	 */
	List<String> onTabComplete(CommandSender sender, Command command, String label, String[] relativeArgs);
	
	String getPermission();
	
}
