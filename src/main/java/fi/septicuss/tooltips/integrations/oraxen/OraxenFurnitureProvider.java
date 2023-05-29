package fi.septicuss.tooltips.integrations.oraxen;

import static io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic.FURNITURE_KEY;

import java.util.List;

import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataType;

import com.google.common.collect.Lists;

import fi.septicuss.tooltips.integrations.FurnitureProvider;
import fi.septicuss.tooltips.utils.cache.furniture.FurnitureCache;
import fi.septicuss.tooltips.utils.cache.furniture.FurnitureWrapper;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;
import io.th0rgal.oraxen.utils.BlockHelpers;

public class OraxenFurnitureProvider implements FurnitureProvider {

	@Override
	public boolean isFurniture(Entity entity) {
		final String itemID = getFurnitureId(entity);
		if (itemID == null) return false;
		return FurnitureCache.contains(itemID);
	}

	@Override
	public boolean isFurniture(Block block) {
		final String itemID = getFurnitureId(block);
		if (itemID == null) return false;
		return FurnitureCache.contains(itemID);
	}

	@Override
	public String getFurnitureId(Entity entity) {
		final String itemID = entity.getPersistentDataContainer().get(FURNITURE_KEY, PersistentDataType.STRING);
		return itemID;
	}

	@Override
	public String getFurnitureId(Block block) {
		if (block.getType() != Material.BARRIER) return null;
		return BlockHelpers.getPDC(block).get(FURNITURE_KEY, PersistentDataType.STRING);
	}

	@Override
	public List<FurnitureWrapper> getAllFurniture() {

		List<FurnitureWrapper> wrappers = Lists.newArrayList();
		MechanicFactory furnitureFactory = MechanicsManager.getMechanicFactory("furniture");

		for (var id : furnitureFactory.getItems()) {
			ItemBuilder builder = OraxenItems.getItemById(id);
			ItemStack built = builder.build();

			if (built == null)
				continue;

			String name;

			if (built.hasItemMeta() && built.getItemMeta().hasDisplayName())
				name = built.getItemMeta().getDisplayName();
			else
				name = built.getType().toString();

			wrappers.add(new FurnitureWrapper(id, name));
		}

		return wrappers;
	}

}
