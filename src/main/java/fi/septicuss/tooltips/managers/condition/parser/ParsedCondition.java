package fi.septicuss.tooltips.managers.condition.parser;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.ConditionManager;
import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.entity.Player;

public class ParsedCondition {

	private final ConditionManager conditionManager;
	private final String presetName;
	private final String conditionName;
	private final Arguments args;

	public ParsedCondition(ConditionManager conditionManager, String presetName, String conditionName, Arguments args) {
		this.conditionManager = conditionManager;
		this.conditionName = conditionName;
		this.presetName = presetName;
		this.args = args;
	}

	public boolean check(Player player, Context context) {
		final Condition condition = this.getCondition();

		if (condition == null) {
			Tooltips.warn("condition-" + conditionName + "-" + presetName, "Tried to run an unknown condition " + Utils.quote(conditionName) + " in preset " + Utils.quote(presetName));
			return false;
		}

		final Validity validity = condition.valid(args);

		if (validity == null) {
			Tooltips.warn("condition-" + conditionName + "-" + presetName, "Condition " + Utils.quote(conditionName) + " in preset " + Utils.quote(presetName) + " returned a null Validity.");
			return false;
		}

		if (!validity.isValid()) {
			String reason = validity.hasReason() ? validity.getReason() : "No reason provided.";
			Tooltips.warn("condition-" + conditionName + "-" + presetName, "Failed to validate condition " + Utils.quote(conditionName) + " in preset " + Utils.quote(presetName));
			Tooltips.warn("condition-" + conditionName + "-" + presetName + "-reason", "  -> " + reason);
			return false;
		}

		final boolean result = condition.check(player, args);

		if (result) {
			condition.writeContext(player, args, context);
		}

		context.put("condition." + condition.id(), result);

		return result;
	}

	public Condition getCondition() {
		return this.conditionManager.get(this.conditionName);
	}

	public Arguments getArgs() {
		return args;
	}

	@Override
	public String toString() {
		return "ParsedCondition [condition=" + conditionName + ", args=" + args + "]";
	}

}
