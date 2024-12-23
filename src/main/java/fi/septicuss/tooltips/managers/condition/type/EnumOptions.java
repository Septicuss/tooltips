package fi.septicuss.tooltips.managers.condition.type;

import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.validation.Validity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class EnumOptions<E extends Enum<E>> {

	private List<E> options;

	public EnumOptions(List<E> options) {
		this.options = options;
	}

	public boolean contains(E other) {
		return options.contains(other);
	}
	
	public List<E> getOptions() {
		return Collections.unmodifiableList(options);
	}

	public static <E extends Enum<E>> EnumOptions<E> of(Class<E> enumType, String line) {
		final String[] split = line.split(",");
		final List<E> list = new ArrayList<>();

		for (int i = 0; i < split.length; i++) {
			var str = split[i].strip().toUpperCase();
			list.add(Enum.valueOf(enumType, str));
		}

		return new EnumOptions<>(list);
	}

	public static <E extends Enum<E>> Validity validity(Class<E> enumType, String optionString) {
		final String[] split = optionString.split(",");

		for (int i = 0; i < split.length; i++) {
			var str = split[i].strip().toUpperCase();

			try {
				Enum.valueOf(enumType, str);
			} catch (IllegalArgumentException e) {
				return Validity.of(false,
						"Enum " + Utils.quote(str) + " is not specified for type " + Utils.quote(enumType.getName()));
			}
		}

		return Validity.of(true);
	}

}
