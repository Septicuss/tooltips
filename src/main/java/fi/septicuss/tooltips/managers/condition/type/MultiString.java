package fi.septicuss.tooltips.managers.condition.type;

import com.google.common.collect.Lists;

import java.util.List;

public class MultiString {

	private final List<String> strings;

	public MultiString(List<String> strings) {
		this.strings = strings;
	}

	public boolean contains(String other) {
		if (other == null) return false;
		return strings.contains(other);
	}

	public boolean containsAnyCase(String other) {
		if (other == null) return false;
		for (String string : strings)
			if (string.equalsIgnoreCase(other))
				return true;
		return false;
	}

	public List<String> getStrings() {
		return strings;
	}

	public static MultiString of(String line) {
		List<String> strings = Lists.newArrayList();
		for (var string : line.split(","))
			strings.add(string.strip());
		return new MultiString(strings);
	}

}
