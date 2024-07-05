package fi.septicuss.tooltips.managers.preset.condition.impl;

import org.bukkit.GameMode;
import org.bukkit.entity.Player;

import fi.septicuss.tooltips.managers.preset.condition.Condition;
import fi.septicuss.tooltips.managers.preset.condition.argument.Argument;
import fi.septicuss.tooltips.managers.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.preset.condition.type.EnumOptions;
import fi.septicuss.tooltips.utils.validation.Validity;

public class Gamemode implements Condition {

	private static final String[] GAMEMODE_ALIASES = { "gamemode", "gm" };

	@Override
	public boolean check(Player player, Arguments args) {
		EnumOptions<GameMode> options = args.get(GAMEMODE_ALIASES).getAsEnumOptions(GameMode.class);
		return options.contains(player.getGameMode());
	}

	@Override
	public Validity valid(Arguments args) {
		if (!args.has(GAMEMODE_ALIASES)) {
			return Validity.of(false, "Missing gamemode argument");
		}

		Argument arg = args.get(GAMEMODE_ALIASES);
		Validity optionValidity = EnumOptions.validity(GameMode.class, arg.getAsString());

		if (!optionValidity.isValid()) {
			return optionValidity;
		}

		return Validity.of(true);
	}

}
