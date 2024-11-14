package fi.septicuss.tooltips.managers.integration.impl.crucible;

import fi.septicuss.tooltips.managers.integration.providers.FurnitureProvider;
import fi.septicuss.tooltips.managers.integration.wrappers.FurnitureWrapper;
import io.lumine.mythiccrucible.MythicCrucible;
import io.lumine.mythiccrucible.items.CrucibleItem;
import io.lumine.mythiccrucible.items.furniture.FurnitureDataKeys;
import io.lumine.mythiccrucible.utils.CustomBlockData;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.persistence.PersistentDataType;

import java.util.Optional;

public class CrucibleFurnitureProvider implements FurnitureProvider {


    @Override
    public FurnitureWrapper getFurniture(Entity entity) {
        if (entity == null) {
            return null;
        }

        final String id = entity.getPersistentDataContainer().get(FurnitureDataKeys.FURNITURE_TYPE, PersistentDataType.STRING);
        return this.getWrapperFromId(id);
    }

    @Override
    public FurnitureWrapper getFurniture(Block block) {
        if (block == null) {
            return null;
        }

        final CustomBlockData data = new CustomBlockData(block, MythicCrucible.inst());
        final String id = data.get(FurnitureDataKeys.FURNITURE_TYPE, PersistentDataType.STRING);

        return this.getWrapperFromId(id);
    }

    private FurnitureWrapper getWrapperFromId(final String id) {
        if (id == null) {
            return null;
        }

        final Optional<CrucibleItem> optionalItem = MythicCrucible.inst().getItemManager().getItem(id);

        if (optionalItem.isEmpty()) {
            return null;
        }

        final CrucibleItem item = optionalItem.get();
        final String name = item.getDisplayName();
        final String itemId = item.getInternalName();

        return new FurnitureWrapper(this.identifier(), itemId, name);
    }

    @Override
    public String identifier() {
        return "Crucible";
    }
}
