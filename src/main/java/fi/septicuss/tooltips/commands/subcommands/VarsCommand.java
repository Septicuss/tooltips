package fi.septicuss.tooltips.commands.subcommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.commands.TooltipsSubCommand;
import fi.septicuss.tooltips.managers.preset.actions.command.ActionCommand;
import fi.septicuss.tooltips.managers.preset.actions.command.ActionCommands;
import fi.septicuss.tooltips.managers.preset.actions.command.impl.vars.VarCommand;
import fi.septicuss.tooltips.utils.Colors;
import fi.septicuss.tooltips.utils.Messaging;
import fi.septicuss.tooltips.utils.validation.Validity;

public class VarsCommand implements TooltipsSubCommand {

	private static Map<String, VarCommand> COMMANDS = null;
	private static List<String> COMMAND_NAMES = null;

	public VarsCommand() {
		cacheCommands();
	}

	/**
	 * Variable commands have the format;
	 * 
	 * /tt vars [command] [scope] (key) (value)
	 * 
	 * Where scope can be:
	 * - a players name
	 * - 'player'
	 * - 'global'
	 * 
	 */
	@Override
	public void onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length == 1) {
			warn(sender, "Missing variable command.");
			return;
		}

		boolean global = false;
		Player target = null;
		String scope = null;

		if (args.length >= 3)
			scope = args[2];

		if (scope == null) {
			warn(sender, "Missing scope argument.");
			return;
		}

		if (scope.equals("player")) {
			if (sender instanceof Player player) {
				target = player;
			} else {
				warn(sender, "Cannot use scope 'player' when using this command from console.");
				return;
			}
		}

		if (scope.equals("global"))
			global = true;

		if (target == null) // Attempt to find player from scope
			target = Bukkit.getPlayerExact(scope);

		if (!global && target == null) {
			warn(sender, "Target player not found.");
			return;
		}

		final Player player = target;

		Bukkit.getScheduler().runTask(Tooltips.get(), () -> {
			StringBuilder stringBuilder = new StringBuilder();

			for (int i = 1; i < args.length; i++) {
				stringBuilder.append(args[i] + " ");
			}
			
			final String fullCommand = stringBuilder.toString().strip();
			final String presetId = "vars command";
			
			Validity result = ActionCommands.runCommand(player, presetId, fullCommand);

			if (result.isValid()) {
				info(sender, "Successfully updated variables.");
				return;
			}
			
			warn(sender, "Error: " + Colors.INFO + result.getReason());
		});
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] relativeArgs) {

		if (relativeArgs.length == 1) {
			String commandName = relativeArgs[0];
			return filter(COMMAND_NAMES, commandName);
		}

		if (relativeArgs.length == 2) {
			String commandName = relativeArgs[0];
			String target = relativeArgs[1];

			if (!COMMANDS.containsKey(commandName.toLowerCase())) {
				return Lists.newArrayList();
			}

			List<String> choices = new ArrayList<>();

			for (Player player : Bukkit.getOnlinePlayers()) {
				choices.add(player.getName());
			}

			choices.add("player");
			choices.add("global");

			return filter(choices, target);
		}

		if (relativeArgs.length == 3) {
			return List.of("key");
		}

		if (relativeArgs.length == 4) {
			return List.of("value");
		}

		return Lists.newArrayList();
	}

	@Override
	public String getPermission() {
		return "tooltips.command.vars";
	}
	
	private List<String> filter(List<String> list, String keyWord) {
		return list.stream().filter(name -> name.contains(keyWord)).collect(Collectors.toList());
	}
	
	private void info(CommandSender sender, String message) {
		Messaging.send(sender, Colors.PLUGIN + "[!] " + message);
	}
	
	private void warn(CommandSender sender, String message) {
		Messaging.send(sender, Colors.WARN + "[!] " + message);
	}

	private void cacheCommands() {

		COMMAND_NAMES = new ArrayList<>();
		COMMANDS = new HashMap<>();

		for (Map.Entry<String, ActionCommand> entry : ActionCommands.getEntries()) {
			String name = entry.getKey();
			ActionCommand command = entry.getValue();

			if (!(command instanceof VarCommand varCommand)) {
				continue;
			}

			COMMAND_NAMES.add(name);
			COMMANDS.put(name, varCommand);

		}
	}

}
