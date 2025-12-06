package fi.septicuss.tooltips.managers.integration.impl.craftengine;

import fi.septicuss.tooltips.managers.integration.providers.FurnitureProvider;
import fi.septicuss.tooltips.managers.integration.wrappers.FurnitureWrapper;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineFurniture;
import net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class CraftEngineFurnitureProvider implements FurnitureProvider {

    @Nullable
    @Override
    public FurnitureWrapper getFurniture(Entity entity) {
        BukkitFurniture furniture = CraftEngineFurniture.getLoadedFurnitureByMetaEntity(entity);
        if (furniture == null) return null;
        String id = furniture.id().asString();
        return new FurnitureWrapper(this.identifier(), id, id);
    }

    @Nullable
    @Override
    public FurnitureWrapper getFurniture(Block block) {
        ImmutableBlockState blockState = CraftEngineBlocks.getCustomBlockState(block);
        if (blockState == null) {
            return null;
        }
        String id = blockState.owner().value().id().asString();
        return new FurnitureWrapper(this.identifier(), id, id);
    }

    @Override
    public FurnitureWrapper getTargetFurniture(Player player) {
        BukkitFurniture furniture = CraftEngineFurniture.rayTrace(player);
        if (furniture == null) return null;
        String id = furniture.id().asString();
        return new FurnitureWrapper(this.identifier(), id, id);
    }

    @Override
    public boolean hasCustomRaytrace() {
        return true;
    }

    @Override
    public String identifier() {
        return "CraftEngine";
    }
}
