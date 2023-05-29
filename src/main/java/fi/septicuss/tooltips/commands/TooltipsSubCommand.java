package fi.septicuss.tooltips.commands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public interface TooltipsSubCommand {
	
	void onCommand(CommandSender sender, Command command, String label, String[] args);

	List<String> onTabComplete(CommandSender sender, Command command, String label, String[] relativeArgs);
	
	String getPermission();
	
}
