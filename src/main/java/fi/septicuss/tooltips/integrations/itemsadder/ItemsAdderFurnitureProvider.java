package fi.septicuss.tooltips.integrations.itemsadder;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import com.google.common.collect.Lists;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import fi.septicuss.tooltips.integrations.FurnitureProvider;
import fi.septicuss.tooltips.utils.cache.furniture.FurnitureWrapper;

public class ItemsAdderFurnitureProvider implements FurnitureProvider {

	@Override
	public boolean isFurniture(Entity entity) {
		final String itemID = getFurnitureId(entity);
		return (itemID != null);
	}

	@Override
	public boolean isFurniture(Block block) {
		final String itemID = getFurnitureId(block);
		return (itemID != null);
	}

	@Override
	public String getFurnitureId(Entity entity) {
		CustomFurniture custom = CustomFurniture.byAlreadySpawned(entity);
		if (custom == null)
			return null;
		return custom.getNamespacedID();
	}

	@Override
	public String getFurnitureId(Block block) {
		CustomBlock custom = CustomBlock.byAlreadyPlaced(block);
		if (custom == null)
			return null;
		return custom.getNamespacedID();
	}

	@Override
	public List<FurnitureWrapper> getAllFurniture() {

		List<FurnitureWrapper> wrappers = Lists.newArrayList();

		for (var namespacedId : CustomFurniture.getNamespacedIdsInRegistry()) {
			String name = CustomFurniture.getInstance(namespacedId).getDisplayName();
			CustomStack stack = CustomFurniture.getInstance(namespacedId);

			String furnitureBehaviourPath = ("items." + stack.getId() + ".behaviours.furniture");

			if (stack.getConfig().getConfigurationSection(furnitureBehaviourPath) == null) {
				continue;
			}

			if (name == null) {
				name = stack.getId();
			}
			
			wrappers.add(new FurnitureWrapper(namespacedId, name));
		}

		return wrappers;
	}
	
}
