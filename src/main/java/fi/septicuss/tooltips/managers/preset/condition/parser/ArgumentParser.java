package fi.septicuss.tooltips.managers.preset.condition.parser;

import fi.septicuss.tooltips.managers.preset.condition.argument.Argument;
import fi.septicuss.tooltips.managers.preset.condition.argument.Arguments;

public class ArgumentParser implements Parser<Arguments> {

	@Override
	public Arguments parse(String presetName, String from) {
		if (from.isBlank())
			return new Arguments();

		Arguments arguments = new Arguments();
		String[] strArguments = from.split(";");

		for (var strArg : strArguments) {
			int indexOfEquals = strArg.indexOf('=');

			if (indexOfEquals == -1) {
				warn("Failed to parse argument " + quote(strArg) + " in preset " + quote(presetName) + "");
				warn("  (Full line = " + quote(from) + ")");
				continue;
			}

			String key = strArg.substring(0, indexOfEquals);
			String value = strArg.substring(indexOfEquals + 1, strArg.length());

			arguments.add(key.strip(), new Argument(value.strip()));
		}

		return arguments;
	}

}
