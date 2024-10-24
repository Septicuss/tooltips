package fi.septicuss.tooltips.integrations.oraxen;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import com.google.common.collect.Lists;

import fi.septicuss.tooltips.integrations.FurnitureProvider;
import fi.septicuss.tooltips.utils.cache.furniture.FurnitureWrapper;
import io.th0rgal.oraxen.api.OraxenFurniture;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.mechanics.MechanicFactory;
import io.th0rgal.oraxen.mechanics.MechanicsManager;

public class OraxenFurnitureProvider implements FurnitureProvider {

	@Override
	public boolean isFurniture(Entity entity) {
		return OraxenFurniture.isFurniture(entity);
	}

	@Override
	public boolean isFurniture(Block block) {
		return OraxenFurniture.isFurniture(block);
	}

	@Override
	public String getFurnitureId(Entity entity) {
		var mechanic = OraxenFurniture.getFurnitureMechanic(entity);
		if (mechanic == null) return null;
		return mechanic.getItemID();
	}

	@Override
	public String getFurnitureId(Block block) {
		var mechanic = OraxenFurniture.getFurnitureMechanic(block);
		if (mechanic == null) return null;
		return mechanic.getItemID();
	}

	@Override
	public List<FurnitureWrapper> getAllFurniture() {

		List<FurnitureWrapper> wrappers = Lists.newArrayList();
		MechanicFactory furnitureFactory = MechanicsManager.getMechanicFactory("furniture");

		for (var id : furnitureFactory.getItems()) {

			if (id == null)
				continue;

			ItemBuilder builder = OraxenItems.getItemById(id);

			if (builder == null)
				continue;

			String name;

			if (builder.hasItemName())
				name = builder.getItemName();
			else
				name = builder.getType().toString();

			wrappers.add(new FurnitureWrapper(id, name));
		}

		return wrappers;
	}

}
