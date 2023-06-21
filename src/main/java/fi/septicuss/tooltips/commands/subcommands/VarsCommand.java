package fi.septicuss.tooltips.commands.subcommands;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.commands.TooltipsSubCommand;
import fi.septicuss.tooltips.object.preset.actions.command.ActionCommand;
import fi.septicuss.tooltips.object.preset.actions.command.ActionCommands;
import fi.septicuss.tooltips.object.preset.actions.command.impl.vars.VarCommand;
import fi.septicuss.tooltips.utils.Colors;
import fi.septicuss.tooltips.utils.Messaging;

public class VarsCommand implements TooltipsSubCommand {

	@Override
	public void onCommand(CommandSender sender, Command command, String label, String[] args) {
		
		if (!(sender instanceof Player)) {
			Messaging.send(sender, Colors.PLUGIN_COLOR_WARN + "[!] Must be a player to use this command.");
			return;
		}
		
		Bukkit.getScheduler().runTask(Tooltips.get(), () -> {
			StringBuilder stringBuilder = new StringBuilder();

			for (int i = 1; i < args.length; i++) {
				stringBuilder.append(args[i] + " ");
			}

			Player player = (Player) sender;

			ActionCommands.runCommand(player, "none", stringBuilder.toString().strip());
		});
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] relativeArgs) {

		if (relativeArgs.length == 1) {
			Set<Entry<String, ActionCommand>> entrySet = ActionCommands.getEntries();
			List<String> commands = entrySet.stream()
					.filter(entry -> (entry.getValue() instanceof VarCommand))
					.collect(ArrayList::new, (x, y) -> x.add(y.getKey()), (a, b) -> a.addAll(b));
			return commands;
		}

		return null;
	}

	@Override
	public String getPermission() {
		return "tooltips.command.vars";
	}

}
