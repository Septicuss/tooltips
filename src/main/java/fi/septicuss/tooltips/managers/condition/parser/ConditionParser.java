package fi.septicuss.tooltips.managers.condition.parser;

import fi.septicuss.tooltips.managers.condition.ConditionManager;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;

public class ConditionParser implements Parser<ParsedCondition> {

	private ConditionManager conditionManager;
	private ArgumentParser argumentParser;

	public ConditionParser(ConditionManager conditionManager, ArgumentParser argumentParser) {
		this.conditionManager = conditionManager;
		this.argumentParser = argumentParser;
	}

	@Override
	public ParsedCondition parse(String presetName, String from) {
		// Just for sure
		from = from.strip();

		int firstBracket = from.indexOf("{");

		boolean hasOpeningBracket = (firstBracket != -1);
		boolean endsWithBracket = from.endsWith("}");

		// Brackets missing / misplaced
		if (!endsWithBracket || !hasOpeningBracket) {
			final Arguments args = new Arguments();
			return new ParsedCondition(this.conditionManager, presetName, from, args);
		}

		String name = from.substring(0, firstBracket).strip();
		String argLine = from.substring(firstBracket + 1, from.length() - 1);

		if (!conditionManager.exists(name)) {
			warn("condition-" + name + "-" + presetName, "Failed to parse unknown condition " + quote(name) + " in preset " + quote(presetName) + " (2)");
			return null;
		}

		final Arguments args = argumentParser.parse(presetName, argLine);
		return new ParsedCondition(this.conditionManager, presetName, name, args);
	}

}
