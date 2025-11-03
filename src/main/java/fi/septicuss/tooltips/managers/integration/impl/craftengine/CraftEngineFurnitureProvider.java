package fi.septicuss.tooltips.managers.integration.impl.craftengine;

import fi.septicuss.tooltips.managers.integration.providers.FurnitureProvider;
import fi.septicuss.tooltips.managers.integration.wrappers.FurnitureWrapper;
import net.momirealms.craftengine.bukkit.api.CraftEngineFurniture;
import net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.jetbrains.annotations.Nullable;


public class CraftEngineFurnitureProvider implements FurnitureProvider {

    @Nullable
    @Override
    public FurnitureWrapper getFurniture(Entity entity) {
        BukkitFurniture furniture = CraftEngineFurniture.getLoadedFurnitureByBaseEntity(entity);
        if (furniture == null) return null;
        String id = furniture.id().asString();
        return new FurnitureWrapper(this.identifier(), id, id);
    }

    @Nullable
    @Override
    public FurnitureWrapper getFurniture(Block block) {
        return null;
    }

    @Override
    public String identifier() {
        return "CraftEngine";
    }
}
