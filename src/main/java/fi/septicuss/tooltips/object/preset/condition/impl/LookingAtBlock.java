package fi.septicuss.tooltips.object.preset.condition.impl;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

import fi.septicuss.tooltips.object.preset.condition.Condition;
import fi.septicuss.tooltips.object.preset.condition.argument.Argument;
import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.object.preset.condition.type.EnumOptions;
import fi.septicuss.tooltips.object.preset.condition.type.LocationArgument;
import fi.septicuss.tooltips.object.validation.Validity;
import fi.septicuss.tooltips.utils.Utils;

public class LookingAtBlock implements Condition {

	private static final String[] MATERIAL_ALIASES = { "type", "m", "mat", "material" };
	private static final String[] DISTANCE_ALIASES = { "distance", "d" };
	private static final String[] LOCATION_ALIASES = { "l", "loc", "location" };

	@Override
	public boolean check(Player player, Arguments args) {
		EnumOptions<Material> materials = null;
		LocationArgument location = null;
		int distance = 3;

		if (args.isNumber(DISTANCE_ALIASES))
			distance = args.get(DISTANCE_ALIASES).getAsInt();

		if (args.has(MATERIAL_ALIASES))
			materials = args.get(MATERIAL_ALIASES).getAsEnumOptions(Material.class);

		if (args.has(LOCATION_ALIASES))
			location = args.get(LOCATION_ALIASES).getAsLocationArgument(player);

		var rayTrace = Utils.getRayTraceResult(player, distance);
		
		if (rayTrace == null)
			return false;
		
		Block target = rayTrace.getHitBlock();

		if (target == null) {
			if (materials == null)
				return false;
			return materials.contains(Material.AIR);
		}

		boolean validMaterial = (materials == null ? true : materials.contains(target.getType()));
		boolean validLocation = (location == null ? true : location.getLocation().equals(target.getLocation()));

		return (validMaterial && validLocation);
	}

	@Override
	public Validity valid(Arguments args) {

		if (!args.has(MATERIAL_ALIASES) && !args.has(LOCATION_ALIASES))
			return Validity.of(false, "A material and/or location argument is required");

		if (args.has(MATERIAL_ALIASES)) {
			Argument materialArg = args.get(MATERIAL_ALIASES);
			Validity optionValidity = EnumOptions.validity(Material.class, materialArg.getAsString());

			if (!optionValidity.isValid()) {
				return optionValidity;
			}
		}

		if (args.has(LOCATION_ALIASES)) {
			Argument locationArg = args.get(LOCATION_ALIASES);
			Validity locationValidity = LocationArgument.validityOf(locationArg.getAsString());

			if (!locationValidity.isValid()) {
				return locationValidity;
			}
		}

		return Validity.TRUE;
	}

}
