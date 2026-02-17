package fi.septicuss.tooltips.managers.condition.impl.lookingat;

import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.condition.type.EnumOptions;
import fi.septicuss.tooltips.managers.condition.type.MultiLocation;
import fi.septicuss.tooltips.managers.condition.type.MultiString;
import fi.septicuss.tooltips.managers.integration.IntegrationManager;
import fi.septicuss.tooltips.managers.integration.providers.BlockProvider;
import fi.septicuss.tooltips.managers.integration.wrappers.BlockWrapper;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.RayTraceResult;

/**
 * Check if player is looking at a block
 */
public class LookingAtBlock implements Condition {

	private final IntegrationManager integrationManager;

	private static final String[] ID_ALIASES = { "id", "type", "m", "mat", "material" };
	private static final String[] DISTANCE_ALIASES = { "distance", "d" };
	private static final String[] LOCATION_ALIASES = { "l", "loc", "location" };

	public LookingAtBlock(IntegrationManager integrationManager) {
		this.integrationManager = integrationManager;
	}

	@Override
	public boolean check(Player player, Arguments args) {
		MultiString ids = null;
		MultiLocation locations = null;
		int distance = 3;

		if (args.isNumber(DISTANCE_ALIASES))
			distance = args.get(DISTANCE_ALIASES).getAsInt();

		if (args.has(ID_ALIASES))
			ids = args.get(ID_ALIASES).getAsMultiString();

		if (args.has(LOCATION_ALIASES))
			locations = MultiLocation.of(player, args.get(LOCATION_ALIASES).getAsString());


		// 1. Find any blocks with custom raytrace (possibly client-side blocks)
		for (BlockProvider provider : this.integrationManager.getBlockProviders()) {
			if (provider.hasCustomRayTrace()) {
				BlockWrapper targetBlock = provider.getTargetBlock(player);
				// If found custom block fits the criteria, return it
				if (this.checkMatchesConditions(targetBlock, ids, locations)) {
					return true;
				}
			}
		}

		// 2. Use vanilla raytrace to find server-side blocks
		RayTraceResult rayTrace = Utils.getRayTraceResult(player, distance);
		if (rayTrace == null) return false;

		final Block target = rayTrace.getHitBlock();
		if (target == null) return false;

		// Go through all providers
		for (BlockProvider provider : this.integrationManager.getBlockProviders()) {
			BlockWrapper targetBlock = provider.getBlock(target);
			if (this.checkMatchesConditions(targetBlock, ids, locations)) {
				return true;
			}
		}

		return false;
	}

	@Override
	public Validity valid(Arguments args) {

		if (!args.has(ID_ALIASES) && !args.has(LOCATION_ALIASES))
			return Validity.of(false, "An ID and/or location argument is required");

		if (args.has(LOCATION_ALIASES)) {
			Argument locationArg = args.get(LOCATION_ALIASES);
			Validity multiLocationValidity = MultiLocation.validityOf(locationArg.getAsString());

			if (!multiLocationValidity.isValid()) {
				return multiLocationValidity;
			}
		}

		return Validity.TRUE;
	}

	@Override
	public String id() {
		return "lookingatblock";
	}

	private boolean checkMatchesConditions(BlockWrapper wrapper, MultiString ids, MultiLocation locations) {
		// No matches for empty wrapper
		if (wrapper == null) {
			return false;
		}

		// If target IDs are present, check if the wrapper ID is any of them
		if (ids != null) {
			if (!ids.contains(wrapper.id()))
				return false;
		}

		// If target locations are present, check if block location is any of them
		if (locations != null) {
			if (wrapper.location() == null)
				return false;

			if (!locations.contains(wrapper.location()))
				return false;
		}

		return true;
	}


}
