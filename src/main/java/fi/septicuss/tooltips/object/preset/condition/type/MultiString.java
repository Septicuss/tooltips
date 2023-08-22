package fi.septicuss.tooltips.object.preset.condition.type;

import java.util.List;

import com.google.common.collect.Lists;

public class MultiString {

	private List<String> strings;

	public MultiString(List<String> strings) {
		this.strings = strings;
	}

	public boolean contains(String other) {
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
