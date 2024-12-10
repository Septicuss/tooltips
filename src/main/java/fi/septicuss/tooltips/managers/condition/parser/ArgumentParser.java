package fi.septicuss.tooltips.managers.condition.parser;

import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.Utils;

public class ArgumentParser implements Parser<Arguments> {

	@Override
	public Arguments parse(String presetName, String from) {
		if (from.isBlank())
			return new Arguments();

		final Arguments arguments = new Arguments();

		final String[] strArguments = Utils.splitStringQuotations(from, ';');

		for (var strArg : strArguments) {
			int indexOfEquals = strArg.indexOf('=');

			if (indexOfEquals == -1) {
				warn("Failed to parse argument " + quote(strArg) + " in preset " + quote(presetName));
				warn("  (Full line = " + quote(from) + ")");
				continue;
			}

			String key = strArg.substring(0, indexOfEquals);
			String value = strArg.substring(indexOfEquals + 1);

			if (Utils.isSurroundedByQuotes(value)) {
				value = Utils.removeQuotes(value);
			}

			arguments.add(key.strip(), new Argument(value.strip()));
		}

		return arguments;
	}

}
