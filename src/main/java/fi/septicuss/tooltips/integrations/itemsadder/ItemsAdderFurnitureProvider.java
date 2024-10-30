package fi.septicuss.tooltips.integrations.itemsadder;

import java.util.List;

import fi.septicuss.tooltips.Tooltips;
import org.bukkit.block.Block;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;

import com.google.common.collect.Lists;

import dev.lone.itemsadder.api.CustomFurniture;
import dev.lone.itemsadder.api.CustomStack;
import fi.septicuss.tooltips.integrations.FurnitureProvider;
import fi.septicuss.tooltips.utils.cache.furniture.FurnitureWrapper;

public class ItemsAdderFurnitureProvider implements FurnitureProvider {

	private static final String FURNITURE_BEHAVIOUR_PATH = "items.%s.behaviours.furniture";
	private static final String TOOLTIPS_NAME_PATH = "items.%s.tooltips-name";

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
		if (entity == null) {
			return null;
		}

		if (!Tooltips.FURNITURE_ENTITIES.contains(entity.getType())) {
			return null;
		}

		try {
			final CustomFurniture custom = CustomFurniture.byAlreadySpawned(entity);

			if (custom == null)
				return null;
			return custom.getNamespacedID();
		} catch (RuntimeException exception) {
			return null;
		}
	}

	@Override
	public String getFurnitureId(Block block) {
		if (block == null) {
			return null;
		}

		try {
			CustomFurniture custom = CustomFurniture.byAlreadySpawned(block);
			if (custom == null)
				return null;
			return custom.getNamespacedID();
		} catch (RuntimeException exception) {
			return null;
		}
	}

	@Override
	public List<FurnitureWrapper> getAllFurniture() {

		List<FurnitureWrapper> wrappers = Lists.newArrayList();

		for (var namespacedId : CustomFurniture.getNamespacedIdsInRegistry()) {
			CustomStack stack = CustomFurniture.getInstance(namespacedId);

			if (!isFurnitureStack(stack)) {
				continue;
			}
			
			String name = getStackName(stack);
			wrappers.add(new FurnitureWrapper(namespacedId, name));
		}

		return wrappers;
	}

	/**
	 * ItemsAdder may return customstacks that claim to be furniture but may
	 * actually not be.
	 * 
	 * We check that by making sure that the ItemsAdder configuration for this item
	 * has a "furniture" behaviour.
	 */
	private boolean isFurnitureStack(CustomStack stack) {
		if (stack == null) {
			return false;
		}

		final FileConfiguration config = stack.getConfig();

		final var id = stack.getId();
		final var behaviourPath = String.format(FURNITURE_BEHAVIOUR_PATH, id);

		return config.isConfigurationSection(behaviourPath);
	}

	/**
	 * If using the internationalization feature of ItemsAdder, the display name returned
	 * may be empty / null. That's why if a display name is not set or is empty, we get the
	 * name from other sources.
	 */
	private String getStackName(CustomStack stack) {
		if (stack == null) {
			return null;
		}
		
		if (stack.getDisplayName() != null && !stack.getDisplayName().strip().isBlank()) {
			return stack.getDisplayName();
		}
		
		final FileConfiguration config = stack.getConfig();
		
		final var id = stack.getId();
		final var tooltipsNamePath = String.format(TOOLTIPS_NAME_PATH, id);
		
		return config.getString(tooltipsNamePath, id);
	}

}
