package fi.septicuss.tooltips.managers.integration.impl.itemsadder;

import dev.lone.itemsadder.api.CustomBlock;
import fi.septicuss.tooltips.managers.integration.providers.BlockProvider;
import fi.septicuss.tooltips.managers.integration.wrappers.BlockWrapper;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ItemsAdderBlockProvider implements BlockProvider {

    @Override
    public @Nullable BlockWrapper getBlock(@NotNull Block block) {
        CustomBlock customBlock = CustomBlock.byAlreadyPlaced(block);
        if (customBlock == null) return null;

        return new BlockWrapper(
                "itemsadder",
                customBlock.getNamespacedID(),
                customBlock.getDisplayName(),
                block.getLocation()
        );
    }

    @Override
    public String identifier() {
        return "itemsadder";
    }

}
