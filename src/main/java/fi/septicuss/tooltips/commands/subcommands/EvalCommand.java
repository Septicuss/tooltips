package fi.septicuss.tooltips.commands.subcommands;

import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.commands.TooltipsSubCommand;
import fi.septicuss.tooltips.utils.Colors;
import fi.septicuss.tooltips.utils.Messaging;

public class EvalCommand implements TooltipsSubCommand {

	private Tooltips plugin;

	public EvalCommand(Tooltips plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (args.length < 2) {
			Messaging.send(sender, Colors.PLUGIN_COLOR_WARN + "[!] Missing condition");
			return;
		}

		StringBuilder builder = new StringBuilder();

		for (int i = 1; i < args.length; i++) {
			builder.append(args[i] + " ");
		}

		String conditionStr = builder.toString();
		var parser = plugin.getConditionManager().getStatementParser();
		var statement = parser.parse("eval", conditionStr);

		if (statement == null) {
			Messaging.send(sender, Colors.PLUGIN_COLOR_WARN + "[!] An error occured while trying to parse condition");
			return;
		}

		Player player = (Player) sender;

		boolean result = statement.getCondition().check(player);

		Messaging.send(sender,
				"Condition result: " + (result ? Colors.PLUGIN_COLOR : Colors.PLUGIN_COLOR_WARN) + result);

		if (statement.hasOutcome())
			Messaging.send(sender,
					" Outcome: " + (statement.getOutcome().asBoolean() ? Colors.PLUGIN_COLOR : Colors.PLUGIN_COLOR_WARN)
							+ statement.getOutcome().toString());

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] relativeArgs) {
		return Lists.newArrayList();
	}

	@Override
	public String getPermission() {
		return "tooltips.command.eval";
	}

}
