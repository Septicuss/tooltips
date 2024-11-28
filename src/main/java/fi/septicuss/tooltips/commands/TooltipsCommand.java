package fi.septicuss.tooltips.commands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.utils.Colors;
import fi.septicuss.tooltips.utils.Messaging;
import net.md_5.bungee.api.ChatColor;

public class TooltipsCommand implements CommandExecutor, TabCompleter {

	protected final HashMap<String, TooltipsSubCommand> subcommands = new HashMap<>();
	protected final Tooltips plugin;

	public TooltipsCommand(final Tooltips plugin) {
		this.plugin = plugin;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
		final var subcommand = args.length <= 0 ? null : this.subcommands.get(args[0].toLowerCase());
		if (subcommand == null) {
			return sendHelp(sender, command, label, args);
		} else {
			if (!sender.hasPermission(subcommand.getPermission())) {
				Messaging.send(sender, Colors.WARN + "[!] No permission");
				return true;
			}
			subcommand.onCommand(sender, command, label, args);
			return false;
		}
	}

	protected boolean sendHelp(CommandSender sender, Command command, String label, String[] args) {
		if (!sender.hasPermission("tooltips.command.help")) {
			Messaging.send(sender, Colors.WARN + "[!] No permission");
			return true;
		}
		
		Messaging.send(sender, Colors.PLUGIN + ChatColor.BOLD + "Tooltips Help");
		Messaging.send(sender, ChatColor.WHITE + "- " + Colors.PLUGIN + "/tt &freload");
		Messaging.send(sender, ChatColor.WHITE + "- " + Colors.PLUGIN + "/tt &feval {condition}");
		Messaging.send(sender, ChatColor.WHITE + "- " + Colors.PLUGIN + "/tt &fdebug {preset id} {player}");
		Messaging.send(sender, ChatColor.WHITE + "- " + Colors.PLUGIN + "/tt &fsendpreset {player} {preset id} (text)");
		Messaging.send(sender, ChatColor.WHITE + "- " + Colors.PLUGIN + "/tt &fsendtheme {player} {theme id} (text)");
		Messaging.send(sender, ChatColor.WHITE + "- " + Colors.PLUGIN + "/tt &fvars {varcommand} (args)");
		Messaging.send(sender, ChatColor.WHITE + "- " + Colors.PLUGIN + "/tt &flistvars {scope}");
		return true;
	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
		final String firstArg = args[0];
		final List<String> results = new ArrayList<>();

		switch (args.length) {
		case 0:
			return new ArrayList<>(this.subcommands.keySet());
		case 1:

			for (var sub : this.subcommands.keySet()) {
				if (!sender.hasPermission(this.subcommands.get(sub).getPermission()))
					continue;
				if (sub.toLowerCase().indexOf(firstArg.toLowerCase()) == 0) {
					results.add(sub);
				}
			}

			results.sort(Comparator.naturalOrder());
			return results;
		default:
			final String[] relativeArgs = new String[args.length - 1];

			for (int i = 1; i < args.length; i++) {
				relativeArgs[i - 1] = args[i];
			}

			var subCommand = getSubCommand(firstArg);

			if (subCommand == null) {
				return null;
			}
			
			if (!sender.hasPermission(subCommand.getPermission())) {
				return null;
			}

			final List<String> completions = subCommand.onTabComplete(sender, command, label, relativeArgs);

			if (completions == null)
				return null;

			results.addAll(completions);

			return results;
		}
	}

	public void register(String key, TooltipsSubCommand subCommand, String... aliases) {
		this.subcommands.put(key, subCommand);
		Arrays.stream(aliases).forEach((alias) -> this.subcommands.put(alias, subCommand));
	}

	public TooltipsSubCommand getSubCommand(String alias) {
		for (var sub : subcommands.keySet()) {
			if (sub.equalsIgnoreCase(alias))
				return subcommands.get(sub);
		}
		return null;
	}

	public Tooltips getPlugin() {
		return plugin;
	}
}
