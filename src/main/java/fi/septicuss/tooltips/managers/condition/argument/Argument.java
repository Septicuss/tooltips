package fi.septicuss.tooltips.managers.condition.argument;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.condition.type.EnumOptions;
import fi.septicuss.tooltips.managers.condition.type.LocationArgument;
import fi.septicuss.tooltips.utils.Text;
import org.bukkit.entity.Player;

public class Argument {

	private String value;

	public Argument(String value) {
		this.value = value;
	}

	public Argument process(Player player) {
		if (player == null) return this;
		return new Argument(Text.processText(player, value));
	}

	public Argument process(Player player, Context context) {
		if (player == null) return this;
		return new Argument(Text.processTextWithContext(player, this.value, context));
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
		try {
			return Float.parseFloat(value);
		} catch (NumberFormatException e) {
			return 0f;
		}
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