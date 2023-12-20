package fi.septicuss.tooltips.object.preset.condition.argument;

import java.util.HashMap;
import java.util.Set;

public class Arguments {

	private HashMap<String, Argument> argumentMap = new HashMap<>();

	public Arguments() {
		argumentMap = new HashMap<>();
	}

	public void add(String name, Argument argument) {
		argumentMap.put(name, argument);
	}

	public Argument get(String... aliases) {
		for (var alias : aliases) {
			if (alias == null) continue;
			if (argumentMap.containsKey(alias)) {
				return argumentMap.get(alias);
			}
		}
		return null;
	}

	public boolean has(String... aliases) {
		for (var alias : aliases)
			if (argumentMap.containsKey(alias))
				return true;

		return false;
	}

	public boolean isNumber(String... aliases) {
		if (!has(aliases))
			return false;
		try {
			Double.parseDouble(get(aliases).getAsString());
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public boolean isBoolean(String... aliases) {
		if (!has(aliases)) {
			return false;
		}
		String value = get(aliases).getAsString();
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
			return true;
		return false;
	}
	
	public void remove(String... aliases) {
		for (var alias : aliases)
			argumentMap.remove(alias);
	}
	
	public Set<String> keys() {
		return argumentMap.keySet();
	}

	public boolean areEmpty() {
		return argumentMap.isEmpty();
	}

	public int size() {
		return argumentMap.size();
	}

	@Override
	public String toString() {
		return "Arguments{" + argumentMap.toString() + "}";
	}

}
