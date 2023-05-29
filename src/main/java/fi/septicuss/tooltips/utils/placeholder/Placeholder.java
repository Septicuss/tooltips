package fi.septicuss.tooltips.utils.placeholder;

import org.bukkit.entity.Player;

import com.comphenix.protocol.wrappers.collection.BiFunction;

public class Placeholder {

	private BiFunction<Player, String, String> function;

	public Placeholder(BiFunction<Player, String, String> function) {
		this.function = function;
	}

	public String getValue(Player player, String placeholder) {
		return this.function.apply(player, placeholder);
	}

	public BiFunction<Player, String, String> getFunction() {
		return function;
	}

}
