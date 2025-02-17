package fi.septicuss.tooltips.managers.integration.providers;

import fi.septicuss.tooltips.managers.integration.wrappers.FurnitureWrapper;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

public interface FurnitureProvider extends Provider {

    @Nullable FurnitureWrapper getFurniture(Entity entity);

    @Nullable FurnitureWrapper getFurniture(Block block);

    default boolean hasCustomRaytrace() {
        return false;
    }

    default FurnitureWrapper getTargetFurniture(Player player) {
        return null;
    }

}
