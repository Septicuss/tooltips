package fi.septicuss.tooltips.managers.preset.condition.parser;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.utils.Utils;

public interface Parser<T> {

	public T parse(String presetName, String from);

	default void warn(String message) {
		Tooltips.warn(message);
	}

	default String quote(String message) {
		return Utils.quote(message);
	}

}
