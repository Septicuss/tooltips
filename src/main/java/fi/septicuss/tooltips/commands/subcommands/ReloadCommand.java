package fi.septicuss.tooltips.commands.subcommands;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.commands.TooltipsSubCommand;
import fi.septicuss.tooltips.utils.AdventureUtils;
import fi.septicuss.tooltips.utils.Colors;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.List;

public class ReloadCommand implements TooltipsSubCommand {

	private Tooltips plugin;

	public ReloadCommand(Tooltips plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onCommand(CommandSender sender, Command command, String label, String[] args) {
		AdventureUtils.sendMessage(sender, Colors.PLUGIN + "Reloading plugin...");
		plugin.reload();
		AdventureUtils.sendMessage(sender, Colors.PLUGIN + "Reloaded!");
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
