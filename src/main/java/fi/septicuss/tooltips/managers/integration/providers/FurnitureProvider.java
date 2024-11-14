package fi.septicuss.tooltips.managers.integration.providers;

import fi.septicuss.tooltips.managers.integration.wrappers.FurnitureWrapper;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import javax.annotation.Nullable;

public interface FurnitureProvider extends Provider {

    @Nullable FurnitureWrapper getFurniture(Entity entity);

    @Nullable FurnitureWrapper getFurniture(Block block);

}
