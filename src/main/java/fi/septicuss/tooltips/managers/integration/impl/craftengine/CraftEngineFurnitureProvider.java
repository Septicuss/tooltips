package fi.septicuss.tooltips.managers.integration.impl.craftengine;

import com.nexomc.nexo.api.NexoFurniture;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.furniture.FurnitureMechanic;
import fi.septicuss.tooltips.managers.integration.providers.FurnitureProvider;
import fi.septicuss.tooltips.managers.integration.wrappers.FurnitureWrapper;
import net.kyori.adventure.text.TextComponent;
import net.momirealms.craftengine.bukkit.api.CraftEngineFurniture;
import net.momirealms.craftengine.bukkit.api.CraftEngineItems;
import net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;


public class CraftEngineFurnitureProvider implements FurnitureProvider {

    @Nullable
    @Override
    public FurnitureWrapper getFurniture(Entity entity) {
        if (!CraftEngineFurniture.isFurniture(entity)) {
            return null;
        }

        BukkitFurniture mechanic = CraftEngineFurniture.getLoadedFurnitureByBaseEntity(entity);
        if (mechanic == null) return null;

        // In CraftEngine, furniture and ItemStack are not forcibly associated.
        // It is entirely possible to create a furniture entity by vanilla displays entity.
        // If you need furniture's name, I recommend maintaining it yourself in data.
        final String id = mechanic.config().id().asString();
        return new FurnitureWrapper(this.identifier(), id, id);
    }

    @Nullable
    @Override
    public FurnitureWrapper getFurniture(Block block) {
        // CraftEngine has no furniture by blocks.
        return null;
    }

    @Override
    public FurnitureWrapper getTargetFurniture(Player player) {
        return this.getFurniture(NexoFurniture.findTargetFurniture(player));
    }

    @Override
    public String identifier() {
        return "CraftEngine";
    }
}
