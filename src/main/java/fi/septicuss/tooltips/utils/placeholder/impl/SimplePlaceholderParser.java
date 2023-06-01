package fi.septicuss.tooltips.utils.placeholder.impl;

import java.util.function.BiFunction;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.utils.placeholder.PlaceholderParser;

public class SimplePlaceholderParser implements PlaceholderParser {

	private BiFunction<Player, String, String> function;

	public SimplePlaceholderParser(BiFunction<Player, String, String> function) {
		this.function = function;
	}

	@Override
	public String parse(Player player, String placeholder) {
		if (function == null) return null;
		return function.apply(player, placeholder);
	}

}
