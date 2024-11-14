package fi.septicuss.tooltips.managers.condition.parser;

import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import org.bukkit.entity.Player;

public class ParsedCondition {

	private Condition condition;
	private Arguments args;

	public ParsedCondition(Condition condition, Arguments args) {
		this.condition = condition;
		this.args = args;
	}

	public boolean check(Player player, Context context) {
		if (condition == null)
			return false;

		return condition.check(player, args, context);
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
