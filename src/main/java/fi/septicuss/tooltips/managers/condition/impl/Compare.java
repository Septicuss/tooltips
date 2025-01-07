package fi.septicuss.tooltips.managers.condition.impl;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.condition.type.Operation;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.entity.Player;

public class Compare implements Condition {

	private static final String[] OPERATION_ALIASES = { "o", "oper", "operation" };
	private static final String[] FIRST_VALUE_ALIASES = { "1", "first", };
	private static final String[] SECOND_VALUE_ALIASES = { "2", "second" };

	@Override
	public boolean check(Player player, Arguments args) {

		Argument firstArg = args.get(FIRST_VALUE_ALIASES);
		Argument secondArg = args.get(SECOND_VALUE_ALIASES);

		final Context workingContext = Tooltips.getPlayerTooltipData(player).getWorkingContext();
		
		firstArg = firstArg.process(player, workingContext);

		if (secondArg == null) {
			return firstArg.getAsBool();
		}

		secondArg = secondArg.process(player, workingContext);

		if (firstArg.isNumber() && (secondArg.isNumber() || secondArg.getAsString().isEmpty())) {
			Operation operation = Operation.EQUAL;

			if (args.has(OPERATION_ALIASES)) {
				operation = Operation.parseOperation(args.get(OPERATION_ALIASES).getAsString());
			}

			float first = firstArg.getAsFloat();
			float second = secondArg.getAsFloat();

			switch (operation) {
			case EQUAL:
				return (first == second);
			case GREATER_THAN:
				return (first > second);
			case GREATER_THAN_OR_EQUAL:
				return (first >= second);
			case LESS_THAN:
				return (first < second);
			case LESS_THAN_OR_EQUAL:
				return (first <= second);
			default:
				return false;
			}
		}

		return (firstArg.getAsString().equals(secondArg.getAsString()));
	}

	@Override
	public Validity valid(Arguments args) {

		if (args.areEmpty())
			return Validity.of(false, "Empty arguments, must at least have first value");

		if (args.has(OPERATION_ALIASES)) {
			String operation = args.get(OPERATION_ALIASES).getAsString();
			if (Operation.parseOperation(operation) == null) {
				return Validity.of(false, "Unknown operation " + quote(operation));
			}
		}

		if (!args.has(FIRST_VALUE_ALIASES)) {
			return Validity.of(false, "First value must be defined");
		}

		return Validity.TRUE;
	}

	@Override
	public String id() {
		return "compare";
	}
}
