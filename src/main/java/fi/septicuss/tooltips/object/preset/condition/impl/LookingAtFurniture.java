package fi.septicuss.tooltips.object.preset.condition.impl;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.integrations.FurnitureProvider;
import fi.septicuss.tooltips.object.preset.condition.Condition;
import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.object.validation.Validity;
import fi.septicuss.tooltips.utils.Utils;

public class LookingAtFurniture implements Condition {

	private static final String[] DISTANCE = { "d", "distance" };
	private static final String[] ID = { "id" };

	private FurnitureProvider provider;

	public LookingAtFurniture(FurnitureProvider provider) {
		this.provider = provider;
	}

	@Override
	public boolean check(Player player, Arguments args) {
		String id = null;
		int distance = 3;

		if (args.has(DISTANCE))
			distance = args.get(DISTANCE).getAsInt();

		if (args.has(ID))
			id = args.get(ID).getAsString();

		Block block = player.getTargetBlockExact(distance);

		if (block != null && provider.isFurniture(block)) {
			if (id != null) return provider.getFurnitureId(block).equals(id);
			return true;
		}
		
		Entity entity = Utils.getEntityPlayerIsLookingAt(player, distance, 0, Tooltips.FURNITURE_ENTITIES);

		if (entity != null && provider.isFurniture(entity)) {
			if (id != null) return provider.getFurnitureId(entity).equals(id);
			return true;
		}

		return false;
	}

	@Override
	public Validity valid(Arguments args) {
		
		if (provider  == null) {
			return Validity.of(false, "No furniture plugin present");
		}

		if (args.has(DISTANCE) && !args.get(DISTANCE).isNumber()) {
			return Validity.of(false, "Distance must be a number");
		}

		return Validity.TRUE;
	}

}
