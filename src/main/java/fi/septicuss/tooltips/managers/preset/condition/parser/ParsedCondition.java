package fi.septicuss.tooltips.managers.preset.condition.parser;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.managers.preset.condition.Condition;
import fi.septicuss.tooltips.managers.preset.condition.argument.Arguments;

public class ParsedCondition {

	private Condition condition;
	private Arguments args;

	public ParsedCondition(Condition condition, Arguments args) {
		this.condition = condition;
		this.args = args;
	}

	public boolean check(Player player) {
		if (condition == null)
			return false;

		return condition.check(player, args);
	}

	public Condition getCondition() {
		return condition;
	}

	public Arguments getArgs() {
		return args;
	}

	@Override
	public String toString() {
		return "ParsedCondition [condition=" + condition + ", args=" + args + "]";
	}

}
