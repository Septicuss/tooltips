package fi.septicuss.tooltips.managers.condition.type;

import com.google.common.collect.Lists;

import java.util.List;

public class MultiString {

	private List<String> strings;

	public MultiString(List<String> strings) {
		this.strings = strings;
	}

	public boolean contains(String other) {
		if (other == null) return false;
		return strings.contains(other);
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
