package fi.septicuss.tooltips.managers.integration.impl.nexo;

import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import fi.septicuss.tooltips.managers.integration.providers.FurnitureProvider;
import fi.septicuss.tooltips.managers.integration.wrappers.FurnitureWrapper;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;


public class NexoFurnitureProvider implements FurnitureProvider {
    @Nullable
    @Override
    public FurnitureWrapper getFurniture(Entity entity) {
        if (!NexoFurniture.isFurniture(entity)) {
            return null;
        }

        final FurnitureMechanic mechanic = NexoFurniture.furnitureMechanic(entity);
        if (mechanic == null) return null;

        return this.getWrapperFromMechanic(mechanic);
    }

    @Nullable
    @Override
    public FurnitureWrapper getFurniture(Block block) {
        final Location location = block.getLocation();
        if (!NexoFurniture.isFurniture(location)) {
            return null;
        }

        final FurnitureMechanic mechanic = NexoFurniture.furnitureMechanic(location);
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
        final ItemBuilder builder = NexoItems.itemFromId(itemId);

        if (builder == null) {
            return null;
        }

        final boolean hasName = builder.hasItemName();
        return (hasName ? ((TextComponent) builder.getItemName()).content() : builder.getType().toString());
    }

    @Override
    public String identifier() {
        return "";
    }
}
