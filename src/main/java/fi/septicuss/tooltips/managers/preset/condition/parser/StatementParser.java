package fi.septicuss.tooltips.managers.preset.condition.parser;

import fi.septicuss.tooltips.managers.preset.condition.Statement;
import fi.septicuss.tooltips.managers.preset.condition.Statement.Outcome;

public class StatementParser implements Parser<Statement> {

	private CompositeConditionParser compositeParser;

	public StatementParser(CompositeConditionParser compositeParser) {
		this.compositeParser = compositeParser;
	}

	@Override
	public Statement parse(String presetName, String from) {

		// Does not have outcome
		if (from.endsWith(")")) {
			var compositeCondition = compositeParser.parse(presetName, from);
			return new Statement(compositeCondition);
		}

		String[] split = from.split(" ");
		Outcome outcome = null;

		String last = split[split.length - 1];

		if (Outcome.isOutcome(last)) {
			outcome = Outcome.parseOutcome(last);
			from = from.substring(0, from.length() - last.length() - 1);
		}

		var compositeCondition = compositeParser.parse(presetName, from);

		if (compositeCondition == null)
			return null;

		return new Statement(compositeCondition, outcome);
	}

}
