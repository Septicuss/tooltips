package fi.septicuss.tooltips.commands.subcommands;

import java.util.List;

import fi.septicuss.tooltips.utils.AdventureUtils;
import net.citizensnpcs.api.util.Messaging;
import net.kyori.adventure.text.Component;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.commands.TooltipsSubCommand;
import fi.septicuss.tooltips.utils.Colors;

public class EvalCommand implements TooltipsSubCommand {

	private Tooltips plugin;

	public EvalCommand(Tooltips plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onCommand(CommandSender sender, Command command, String label, String[] args) {

		if (!(sender instanceof Player player)) {
			AdventureUtils.sendMessage(sender, Colors.WARN + "[!] Must be a player to use this command.");
			return;
		}
		
		if (args.length < 2) {
			AdventureUtils.sendMessage(sender, Colors.WARN + "[!] Missing condition");
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
			AdventureUtils.sendMessage(sender, Colors.WARN + "[!] An error occured while trying to parse condition");
			return;
		}

        boolean result = statement.getCompositeCondition().check(player);

		AdventureUtils.sendMessage(sender,
				"Condition result: " + (result ? Colors.PLUGIN : Colors.WARN) + result);

		if (statement.hasOutcome())
			AdventureUtils.sendMessage(sender,
					" Outcome: " + (statement.getOutcome().asBoolean() ? Colors.PLUGIN : Colors.WARN)
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
