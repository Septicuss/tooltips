package fi.septicuss.tooltips.commands.subcommands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fi.septicuss.tooltips.commands.TooltipsSubCommand;
import fi.septicuss.tooltips.object.preset.actions.command.ActionCommands;

public class VarsCommand implements TooltipsSubCommand {

	@Override
	public void onCommand(CommandSender sender, Command command, String label, String[] args) {

		StringBuilder stringBuilder = new StringBuilder();
		
		for (int i = 1; i < args.length; i++) {
			stringBuilder.append(args[i] + " ");
		}
		
		Player player = (Player) sender;
		
		ActionCommands.runCommand(player, "none", stringBuilder.toString().strip());
		
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] relativeArgs) {
		return null;
	}

	@Override
	public String getPermission() {
		return "tooltips.command.vars";
	}

}
