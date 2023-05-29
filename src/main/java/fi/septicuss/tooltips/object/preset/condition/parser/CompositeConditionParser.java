package fi.septicuss.tooltips.object.preset.condition.parser;

import fi.septicuss.tooltips.object.preset.condition.composite.CompositeCondition;
import fi.septicuss.tooltips.object.preset.condition.composite.CompositeCondition.Operator;
import fi.septicuss.tooltips.object.preset.condition.composite.CompositeConditionBuilder;
import fi.septicuss.tooltips.utils.Utils;

public class CompositeConditionParser implements Parser<CompositeCondition> {

	private static final char DELIMITER = '\u0000';
	private ConditionParser conditionParser;

	public CompositeConditionParser(ConditionParser conditionParser) {
		this.conditionParser = conditionParser;
	}

	@Override
	public CompositeCondition parse(String presetName, String from) {
		from = from.strip();
		boolean evenRoundBrackets = Utils.sameAmountOfCharsIn(from, '(', ')');

		if (!evenRoundBrackets) {
			warn("Failed to parse condition " + quote(from) + " in preset " + quote(presetName));
			warn("  -> Uneven amount of brackets.");
			return null;
		}

		if (from.startsWith("(") && from.endsWith(")")) {
			from = from.substring(1, from.length() - 1);
		}

		CompositeConditionBuilder fullBuilder = new CompositeConditionBuilder();
		StringBuilder delimiterBuilder = new StringBuilder();

		boolean evenBraceBrackets = Utils.sameAmountOfCharsIn(from, '{', '}');
		boolean evenSquareBrackets = Utils.sameAmountOfCharsIn(from, '[', ']');

		if (!evenBraceBrackets || !evenSquareBrackets) {
			warn("Failed to parse condition " + quote(from) + " in preset " + quote(presetName));
			warn("  -> Uneven amount of brackets.");
			return null;
		}

		// Tracks the current bracket layer
		int layer = 0;

		// Place delimiters on all outside spaces
		boolean hasRoundBrackets = false;
		boolean firstBrace = false;

		// Makes sure that empty parentheses aren't accepted -> (())
		int outerParenthesesSize = 0;

		// Removes extra spaces on the outer layer
		boolean prevSpace = false;

		for (char character : from.toCharArray()) {

			if (character == '(' || character == '{' || character == '[') {
				layer += 1;
			}

			if (character == ')' || character == '}' || character == ']') {
				layer -= 1;
			}

			if (layer == 1 && character == '(') {
				firstBrace = true;
				continue;
			}

			if (layer == 0 && character == ')' && firstBrace == true) {
				if (outerParenthesesSize > 0)
					hasRoundBrackets = true;
				firstBrace = false;
				continue;
			}

			if (character != ' ') {

				if (firstBrace) {
					outerParenthesesSize += 1;
				}

				prevSpace = false;
				delimiterBuilder.append(character);
				continue;
			}

			if (layer > 0) {
				delimiterBuilder.append(character);
				continue;
			}

			if (prevSpace) {
				continue;
			}

			delimiterBuilder.append(DELIMITER);
			prevSpace = true;
		}

		String specialDelimiterString = delimiterBuilder.toString();
		String[] split = specialDelimiterString.split(String.valueOf(DELIMITER));

		// Relevat round brackets eliminated
		if (!hasRoundBrackets) {

			StringBuilder conditionStringBuilder = new StringBuilder();
			StringBuilder afterBuilder = new StringBuilder();

			Operator operator = null;

			for (var str : split) {

				if (operator != null) {
					afterBuilder.append(str + " ");
					continue;
				}

				if (Operator.isOperator(str)) {
					operator = Operator.parseOperator(str);
					continue;
				}

				conditionStringBuilder.append(str + " ");
			}

			String leftConditionLine = conditionStringBuilder.toString().strip();
			ParsedCondition condition = conditionParser.parse(presetName, leftConditionLine);

			if (condition == null)
				return null;

			fullBuilder.with(condition);

			if (operator != null) {
				String remaining = afterBuilder.toString().strip();
				CompositeCondition composite = parse(presetName, remaining);

				if (composite == null)
					return null;

				fullBuilder.append(operator, composite);
			}

			return fullBuilder.build();
		}

		// (meow) AND meow
		// (meow AND meow)
		// ((meow AND meow) OR (meow OR meow))
		// ((meow AND meow) OR meow)
		StringBuilder compositeStringBuilder = new StringBuilder();
		StringBuilder afterBuilder = new StringBuilder();

		// standingon {type=GRASS_BLOCK; hmm={[(grr)]}}

		Operator operator = null;

		for (var str : split) {

			if (operator != null) {
				afterBuilder.append(str + " ");
				continue;
			}

			if (Operator.isOperator(str)) {
				operator = Operator.parseOperator(str);
				continue;
			}

			compositeStringBuilder.append(str + " ");

		}

		String leftCompositeLine = compositeStringBuilder.toString().strip();
		CompositeCondition composite = parse(presetName, leftCompositeLine);

		if (composite == null)
			return null;

		fullBuilder.with(composite);

		if (operator != null) {
			String remaining = afterBuilder.toString().strip();
			CompositeCondition afterComposite = parse(presetName, remaining);

			if (afterComposite == null)
				return null;

			fullBuilder.append(operator, afterComposite);
		}

		return fullBuilder.build();
	}

}
