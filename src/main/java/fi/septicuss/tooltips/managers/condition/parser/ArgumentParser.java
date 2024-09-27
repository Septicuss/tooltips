package fi.septicuss.tooltips.managers.condition.parser;

import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;

import javax.annotation.Nonnull;
import java.util.HashSet;
import java.util.Set;

public class ArgumentParser implements Parser<Arguments> {

	private static final char DELIMITER = '\uF0A2';

	@Override
	public Arguments parse(String presetName, String from) {
		if (from.isBlank())
			return new Arguments();

		final Arguments arguments = new Arguments();

		final String processed = this.placeDelimiters(from);
		final String[] strArguments = processed.split(String.valueOf(DELIMITER));

		for (var strArg : strArguments) {
			int indexOfEquals = strArg.indexOf('=');

			if (indexOfEquals == -1) {
				warn("Failed to parse argument " + quote(strArg) + " in preset " + quote(presetName));
				warn("  (Full line = " + quote(from) + ")");
				continue;
			}

			String key = strArg.substring(0, indexOfEquals);
			String value = strArg.substring(indexOfEquals + 1);

			if (isSurroundedByQuotes(value)) {
				final int valueLength = value.length();
				value = value.substring(1, valueLength - 1);
			}

			arguments.add(key.strip(), new Argument(value.strip()));
		}

		return arguments;
	}

	/**
	 * Place delimiters in the correct places, accounting for quotes.
	 *
	 * @param str The string to place delimiters in.
	 * @return New string, with delimiters placed.
	 */
	private @Nonnull String placeDelimiters(final String str) {
		final Set<Integer> delimiterIndices = new HashSet<>();

		char previousCharacter = DELIMITER;
		char startingQuote = ' ';

		for (int i = 0; i < str.length(); i++) {
			final char character = str.charAt(i);

			boolean isStartingQuoteUnset = (startingQuote == ' ');
			boolean isQuoteCharacter = (character == '\"' || character == '\'');

			if (isStartingQuoteUnset && isQuoteCharacter) {
				startingQuote = character;
				previousCharacter = character;
				continue;
			}

			boolean charIsSemicolon = (character == ';');
			boolean lastCharWasQuote = previousCharacter == startingQuote;

			if (isStartingQuoteUnset && charIsSemicolon) {
				delimiterIndices.add(i);
			}

			if (!isStartingQuoteUnset && lastCharWasQuote && charIsSemicolon) {
				delimiterIndices.add(i);
				startingQuote = ' ';
			}

			previousCharacter = character;
		}

		final StringBuilder modifiedStringBuilder = new StringBuilder(str);
		for (final int i : delimiterIndices) {
			modifiedStringBuilder.setCharAt(i, DELIMITER);
		}

		return modifiedStringBuilder.toString();
	}

	private boolean isSurroundedByQuotes(String value) {
		if (value.startsWith("\"") && value.endsWith("\"")) {
			return true;
		}
        return value.startsWith("'") && value.endsWith("'");
    }

}
