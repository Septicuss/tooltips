package fi.septicuss.tooltips.integrations.itemsadder;

import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import com.google.common.collect.Lists;

import dev.lone.itemsadder.api.CustomBlock;
import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import fi.septicuss.tooltips.integrations.FurnitureProvider;
import fi.septicuss.tooltips.utils.cache.furniture.FurnitureCache;
import fi.septicuss.tooltips.utils.cache.furniture.FurnitureWrapper;

public class ItemsAdderFurnitureProvider implements FurnitureProvider {

	@SuppressWarnings("deprecation")
	public static final NamespacedKey FURNITURE_KEY = new NamespacedKey("itemsadder", "placeable_entity_item");

	@Override
	public boolean isFurniture(Entity entity) {
		final String itemID = getFurnitureId(entity);
		if (itemID == null)
			return false;
		return FurnitureCache.contains(itemID);
	}

	@Override
	public boolean isFurniture(Block block) {
		final String itemID = getFurnitureId(block);
		if (itemID == null)
			return false;
		return FurnitureCache.contains(itemID);
	}

	@Override
	public String getFurnitureId(Entity entity) {
		if (entity == null)
			return null;
		final String itemID = entity.getPersistentDataContainer().get(FURNITURE_KEY, PersistentDataType.STRING);
		return itemID;
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
