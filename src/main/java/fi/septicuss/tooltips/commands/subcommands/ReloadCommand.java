package fi.septicuss.tooltips.commands.subcommands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.commands.TooltipsSubCommand;
import fi.septicuss.tooltips.utils.Colors;
import fi.septicuss.tooltips.utils.Messaging;

public class ReloadCommand implements TooltipsSubCommand {

	private Tooltips plugin;

	public ReloadCommand(Tooltips plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onCommand(CommandSender sender, Command command, String label, String[] args) {
		Messaging.send(sender, Colors.PLUGIN + "Reloading plugin...");
		plugin.reload();
		Messaging.send(sender, Colors.PLUGIN + "Reloaded!");
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] relativeArgs) {
		return null;
	}

	@Override
	public String getPermission() {
		return "tooltips.command.reload";
	}

}
