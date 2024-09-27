package fi.septicuss.tooltips.managers.condition.argument;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.managers.condition.type.EnumOptions;
import fi.septicuss.tooltips.managers.condition.type.LocationArgument;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;

public class Argument {

	private String value;

	public Argument(String value) {
		this.value = value;
	}

	public Argument replacePlaceholders(Player player) {
		return new Argument(Placeholders.replacePlaceholders(player, value));
	}

	public String getAsString() {
		return value;
	}

	public boolean getAsBool() {
		return Boolean.parseBoolean(value);
	}

	public int getAsInt() {
		return Integer.parseInt(value);
	}

	public double getAsDouble() {
		return Double.parseDouble(value);
	}

	public long getAsLong() {
		return Long.parseLong(value);
	}
	
	public float getAsFloat() {
		return Float.parseFloat(value);
	}

	public <E extends Enum<E>> EnumOptions<E> getAsEnumOptions(Class<E> enumType) {
		return EnumOptions.of(enumType, value);
	}
	
	public LocationArgument getAsLocationArgument(Player player) {
		return LocationArgument.of(player, value);
	}

	public boolean isNumber() {
		try {
			Double.parseDouble(value);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
	
	public boolean isBoolean() {
		if (value.equalsIgnoreCase("true") || value.equalsIgnoreCase("false"))
			return true;
		return false;
	}

	@Override
	public String toString() {
		return "Argument{value=" + value + "}";
	}

}