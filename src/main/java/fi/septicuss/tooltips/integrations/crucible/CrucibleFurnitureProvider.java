package fi.septicuss.tooltips.integrations.crucible;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import com.google.common.collect.Lists;

import fi.septicuss.tooltips.integrations.FurnitureProvider;
import fi.septicuss.tooltips.utils.cache.furniture.FurnitureWrapper;
import io.lumine.mythiccrucible.MythicCrucible;
import io.lumine.mythiccrucible.items.CrucibleItem;
import io.lumine.mythiccrucible.items.furniture.FurnitureDataKeys;
import io.lumine.mythiccrucible.utils.CustomBlockData;

public class CrucibleFurnitureProvider implements FurnitureProvider {

	@Override
	public boolean isFurniture(Entity entity) {
		if (entity == null)
			return false;
		return entity.getPersistentDataContainer().has(FurnitureDataKeys.FURNITURE_TYPE, PersistentDataType.STRING);
	}

	@Override
	public boolean isFurniture(Block block) {
		CustomBlockData data = new CustomBlockData(block, MythicCrucible.inst());
		return data.has(FurnitureDataKeys.FURNITURE_TYPE, PersistentDataType.STRING);
	}

	@Override
	public String getFurnitureId(Entity entity) {
		if (entity == null)
			return null;
		return entity.getPersistentDataContainer().get(FurnitureDataKeys.FURNITURE_TYPE, PersistentDataType.STRING);
	}

	@Override
	public String getFurnitureId(Block block) {
		CustomBlockData data = new CustomBlockData(block, MythicCrucible.inst());
		return data.get(FurnitureDataKeys.FURNITURE_TYPE, PersistentDataType.STRING);
	}

	@Override
	public List<FurnitureWrapper> getAllFurniture() {
		Collection<CrucibleItem> items = MythicCrucible.inst().getItemManager().getItems();

		if (items == null || items.isEmpty()) {
			return Lists.newArrayList();
		}

		List<FurnitureWrapper> furniture = new ArrayList<>();

		for (var item : items) {
			String name = item.getDisplayName();
			String id = item.getInternalName();

			if (item.getFurnitureData() != null) {
				furniture.add(new FurnitureWrapper(id, name));
			}
		}

		return furniture;
	}

}
