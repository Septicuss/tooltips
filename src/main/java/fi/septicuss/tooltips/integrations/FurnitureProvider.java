package fi.septicuss.tooltips.integrations;

import java.util.List;

import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import fi.septicuss.tooltips.utils.cache.furniture.FurnitureWrapper;

public interface FurnitureProvider {

	public boolean isFurniture(Entity entity);

	public boolean isFurniture(Block block);

	public String getFurnitureId(Entity entity);

	public String getFurnitureId(Block block);

	public List<FurnitureWrapper> getAllFurniture();

}
