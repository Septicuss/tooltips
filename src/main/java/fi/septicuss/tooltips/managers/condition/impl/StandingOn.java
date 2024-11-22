package fi.septicuss.tooltips.managers.condition.impl;

import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.condition.type.EnumOptions;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;

public class StandingOn implements Condition {

	private static final String[] TYPE_ALIASES = { "type", "m", "mat", "material" };

	@Override
	public boolean check(Player player, Arguments args) {
		Argument typeArg = args.get(TYPE_ALIASES);
		String typeStr = typeArg.getAsString();

		EnumOptions<Material> materials = EnumOptions.of(Material.class, typeStr);
		Block under = player.getLocation().getBlock().getRelative(BlockFace.DOWN);

		return materials.contains(under.getType());
	}

	@Override
	public Validity valid(Arguments args) {
		if (!args.has(TYPE_ALIASES))
			return Validity.of(false, quote("type") + " argument is required");

		String multiMaterialString = args.get(TYPE_ALIASES).getAsString();
		Validity materialValidity = EnumOptions.validity(Material.class, multiMaterialString);

		if (!materialValidity.isValid())
			return materialValidity;

		return Validity.of(true);
	}

}
