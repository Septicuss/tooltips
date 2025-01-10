package fi.septicuss.tooltips.managers.integration.impl.itemsadder;

import dev.lone.itemsadder.api.CustomFurniture;
import fi.septicuss.tooltips.managers.integration.providers.FurnitureProvider;
import fi.septicuss.tooltips.managers.integration.wrappers.FurnitureWrapper;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class ItemsAdderFurnitureProvider implements FurnitureProvider {

    @Override
    public FurnitureWrapper getFurniture(Block block) {
        if (block == null) {
            return null;
        }

        try {
            final CustomFurniture furniture = CustomFurniture.byAlreadySpawned(block);
            return this.getWrapperFromFurniture(furniture);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    @Override
    public FurnitureWrapper getFurniture(Entity entity) {
        if (entity == null) {
            return null;
        }

        try {
            final CustomFurniture furniture = CustomFurniture.byAlreadySpawned(entity);
            return this.getWrapperFromFurniture(furniture);
        } catch (RuntimeException exception) {
            return null;
        }
    }

    private FurnitureWrapper getWrapperFromFurniture(final CustomFurniture furniture) {
        if (furniture == null) {
            return null;
        }

        final String id = furniture.getNamespacedID();
        final String name = furniture.getDisplayName();

        return new FurnitureWrapper(this.identifier(), id, name);
    }

    @Override
    public String identifier() {
        return "ItemsAdder";
    }
}
