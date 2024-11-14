package fi.septicuss.tooltips.managers.integration.impl.oraxen;

import fi.septicuss.tooltips.managers.integration.providers.FurnitureProvider;
import fi.septicuss.tooltips.managers.integration.wrappers.FurnitureWrapper;
import io.th0rgal.oraxen.api.OraxenFurniture;
import io.th0rgal.oraxen.api.OraxenItems;
import io.th0rgal.oraxen.items.ItemBuilder;
import io.th0rgal.oraxen.mechanics.provided.gameplay.furniture.FurnitureMechanic;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

public class OraxenFurnitureProvider implements FurnitureProvider {

    @Override
    public FurnitureWrapper getFurniture(Entity entity) {
        if (!OraxenFurniture.isFurniture(entity)) {
            return null;
        }

        final FurnitureMechanic mechanic = OraxenFurniture.getFurnitureMechanic(entity);
        if (mechanic == null) return null;

        return this.getWrapperFromMechanic(mechanic);
    }

    @Override
    public FurnitureWrapper getFurniture(Block block) {
        if (!OraxenFurniture.isFurniture(block)) {
            return null;
        }

        final FurnitureMechanic mechanic = OraxenFurniture.getFurnitureMechanic(block);
        if (mechanic == null) return null;

        return this.getWrapperFromMechanic(mechanic);
    }

    private FurnitureWrapper getWrapperFromMechanic(final FurnitureMechanic mechanic) {
        final String id = mechanic.getItemID();
        final String name = this.getFurnitureName(id);

        if (name == null) {
            return null;
        }

        return new FurnitureWrapper(this.identifier(), id, name);
    }


    private String getFurnitureName(final String itemId) {
        final ItemBuilder builder = OraxenItems.getItemById(itemId);

        if (builder == null) {
            return null;
        }

        final boolean hasName = builder.hasItemName();
        return (hasName ? builder.getItemName() : builder.getType().toString());
    }

    @Override
    public String identifier() {
        return "Oraxen";
    }

}
