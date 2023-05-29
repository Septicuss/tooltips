package fi.septicuss.tooltips.object.preset.condition.parser;

import fi.septicuss.tooltips.object.preset.condition.Condition;
import fi.septicuss.tooltips.object.preset.condition.ConditionManager;
import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.object.validation.Validity;

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
			final String name = from;
			if (!conditionManager.exists(name)) {
				warn("Failed to parse unknown condition " + quote(name) + " in preset " + quote(presetName) + " (1)");
				return null;
			}

			Condition condition = conditionManager.get(name);
			Arguments args = new Arguments();
			Validity validity = condition.valid(args);

			if (validity == null || !validity.isValid()) {
				warn("Failed to parse condition " + quote(from) + " in preset " + quote(presetName) + "");
				if (validity.hasReason())
					warn("  -> " + validity.getReason());
				return null;
			}

			return new ParsedCondition(condition, args);
		}

		String name = from.substring(0, firstBracket).strip();
		String argLine = from.substring(firstBracket + 1, from.length() - 1);

		if (!conditionManager.exists(name)) {
			warn("Failed to parse unknown condition " + quote(name) + " in preset " + quote(presetName) + " (2)");
			return null;
		}

		Condition condition = conditionManager.get(name);
		Arguments args = argumentParser.parse(presetName, argLine);

		Validity validity = condition.valid(args);

		if (!validity.isValid()) {
			warn("Failed to parse condition " + quote(from) + " in preset " + quote(presetName) + "");
			if (validity.hasReason())
				warn("  -> " + validity.getReason());
			return null;
		}

		return new ParsedCondition(condition, args);
	}

}
