package fi.septicuss.tooltips.object.preset.condition.impl;

import java.util.function.Predicate;

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

		final String finalizedId = id;

		Predicate<Block> blockPredicate = (block -> {
			if (block == null) return false;
			if (!provider.isFurniture(block)) return false;
			return (!provider.getFurnitureId(block).equals(finalizedId));
		});
		
		Predicate<Entity> entityFilter = (entity -> {
			if (entity == null) return false;
			if (entity.equals(player)) return false;
			return Tooltips.FURNITURE_ENTITIES.contains(entity.getType());
		});
		
		var rayTrace = Utils.getRayTrace(player, distance, blockPredicate, entityFilter);

		if (rayTrace == null)
			return false;
		
		// Block
		if (rayTrace.getHitBlock() != null) {
			final Block block = rayTrace.getHitBlock();
			if (finalizedId != null)
				return provider.getFurnitureId(block).equals(finalizedId);
			return true;
		}
		
		// Entity
		if (rayTrace.getHitEntity() != null) {
			final Entity entity = rayTrace.getHitEntity();
			if (entity != null && provider.isFurniture(entity)) {
				if (id != null)
					return provider.getFurnitureId(entity).equals(id);
				return true;
			}
		}

		return false;
	}

	@Override
	public Validity valid(Arguments args) {

		if (provider == null) {
			return Validity.of(false, "No furniture plugin present");
		}

		if (args.has(DISTANCE) && !args.get(DISTANCE).isNumber()) {
			return Validity.of(false, "Distance must be a number");
		}

		return Validity.TRUE;
	}

}
