package fi.septicuss.tooltips.integrations.nexo;

import com.google.common.collect.Lists;
import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import fi.septicuss.tooltips.integrations.FurnitureProvider;
import fi.septicuss.tooltips.utils.cache.furniture.FurnitureWrapper;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;

import java.util.List;

public class NexoFurnitureProvider implements FurnitureProvider {
    @Override
    public boolean isFurniture(Entity entity) {
        return NexoFurniture.isFurniture(entity);
    }

    @Override
    public boolean isFurniture(Block block) {
        return NexoFurniture.isFurniture(block.getLocation());
    }

    @Override
    public String getFurnitureId(Entity entity) {
        var mechanic = NexoFurniture.furnitureMechanic(entity);
        if (mechanic == null) return null;
        return mechanic.getItemID();
    }

    @Override
    public String getFurnitureId(Block block) {
        var mechanic = NexoFurniture.furnitureMechanic(block.getLocation());
        if (mechanic == null) return null;
        return mechanic.getItemID();
    }

    @Override
    public List<FurnitureWrapper> getAllFurniture() {
        List<FurnitureWrapper> wrappers = Lists.newArrayList();

        for (var id : NexoFurniture.furnitureIDs()) {
            if (id == null) continue;

            ItemBuilder builder = NexoItems.itemFromId(id);

            if (builder == null) continue;

            String name = builder.hasItemName() ? builder.getItemName().toString() : builder.getType().toString();
            wrappers.add(new FurnitureWrapper(id, name));
        }

        return wrappers;
    }
}
