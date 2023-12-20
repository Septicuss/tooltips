package fi.septicuss.tooltips.commands.subcommands;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.commands.TooltipsSubCommand;
import fi.septicuss.tooltips.object.preset.condition.argument.Argument;
import fi.septicuss.tooltips.utils.Colors;
import fi.septicuss.tooltips.utils.Messaging;
import fi.septicuss.tooltips.utils.variable.Variables;
import net.md_5.bungee.api.ChatColor;

public class ListVarsCommand implements TooltipsSubCommand {

	@Override
	public void onCommand(CommandSender sender, Command command, String label, String[] args) {
		Bukkit.getScheduler().runTask(Tooltips.get(), () -> {
			if (args.length <= 1) {
				warn(sender, "Missing scope (players name, 'player' or 'global')");
				return;
			}

			final String scope = args[1];

			if (scope.equalsIgnoreCase("global")) {
				printGlobalVars(sender);
				return;
			}

			if (scope.equalsIgnoreCase("player")) {

				if (!(sender instanceof Player player)) {
					warn(sender, "Must be a player to use this scope");
					return;
				}

				printPlayerVars(sender, player);
				return;
			}

			@SuppressWarnings("deprecation")
			OfflinePlayer player = Bukkit.getOfflinePlayer(scope);
			printPlayerVars(sender, player);

		});

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] relativeArgs) {

		if (relativeArgs.length == 1) {
			String current = relativeArgs[0];

			List<String> results = new ArrayList<>();
			Bukkit.getOnlinePlayers().forEach(player -> results.add(player.getName()));
			results.add("global");
			results.add("player");

			return filter(results, current);
		}

		return new ArrayList<>();
	}

	@Override
	public String getPermission() {
		return "tooltips.command.listvars";
	}

	private void printGlobalVars(CommandSender sender) {

		List<String> localVarNames = Variables.LOCAL.getVarNames();
		List<String> persistentVarNames = Variables.PERSISTENT.getVarNames();
		
		Map<String, Argument> localVars = new HashMap<>();
		Map<String, Argument> persistentVars = new HashMap<>();
		
		for (var varName : localVarNames) {
			Argument variable = Variables.LOCAL.getVar(varName);
			
			if (variable == null || variable.getAsString() == null)
				continue;
			
			localVars.put(varName, variable);
		}
		
		for (var varName : persistentVarNames) {
			Argument variable = Variables.PERSISTENT.getVar(varName);
			
			if (variable == null || variable.getAsString() == null)
				continue;
			
			persistentVars.put(varName, variable);
		}
		
		printVars(sender, "Global", localVars, persistentVars);

	}

	private void printPlayerVars(CommandSender sender, OfflinePlayer player) {

		if (player == null || !player.hasPlayedBefore()) {
			Messaging.send(sender, Colors.INFO + "Couldn't find that player.");
			return;
		}

		List<String> localVarNames = Variables.LOCAL.getVarNames(player);
		List<String> persistentVarNames = Variables.PERSISTENT.getVarNames(player);
		
		Map<String, Argument> localVars = new HashMap<>();
		Map<String, Argument> persistentVars = new HashMap<>();
		
		for (var varName : localVarNames) {
			Argument variable = Variables.LOCAL.getVar(player, varName);
			
			if (variable == null || variable.getAsString() == null)
				continue;
			
			localVars.put(varName, variable);
		}
		
		for (var varName : persistentVarNames) {
			Argument variable = Variables.PERSISTENT.getVar(player, varName);
			
			if (variable == null || variable.getAsString() == null)
				continue;
			
			persistentVars.put(varName, variable);
		}
		
		printVars(sender, player.getName(), localVars, persistentVars);
		
	}

	private void printVars(CommandSender sender, String scope, Map<String, Argument> localVars, Map<String, Argument> persistentVars) {

		print(sender, "");
		print(sender, Colors.PLUGIN + ChatColor.BOLD + scope + " variables");

		print(sender, Colors.PLUGIN +  "Local:");

		if (localVars.isEmpty()) {
			print(sender, Colors.INFO + " None");
		} else {
			for (var entry : localVars.entrySet()) {
				String key = entry.getKey();
				Argument variable = entry.getValue();

				String line = String.format(Colors.INFO + " %s: %s", key, variable.getAsString());
				print(sender, line);
			}
		}

		print(sender, Colors.PLUGIN + "Persistent:");

		if (persistentVars.isEmpty()) {
			print(sender, Colors.INFO + " None");
		} else {
			for (var entry : persistentVars.entrySet()) {
				String key = entry.getKey();
				Argument variable = entry.getValue();

				String line = String.format(Colors.INFO + " %s: %s", key, variable.getAsString());
				print(sender, line);
			}
		}

	}

	private void print(CommandSender sender, String message) {
		Messaging.send(sender, message);
	}

	private List<String> filter(List<String> list, String keyWord) {
		return list.stream().filter(name -> name.contains(keyWord)).collect(Collectors.toList());
	}

	private void warn(CommandSender sender, String message) {
		Messaging.send(sender, Colors.WARN + "[!] " + message);
	}

}
