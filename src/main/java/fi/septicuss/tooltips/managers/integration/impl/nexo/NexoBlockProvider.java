package fi.septicuss.tooltips.managers.integration.impl.nexo;

import com.nexomc.nexo.api.NexoBlocks;
import com.nexomc.nexo.api.NexoItems;
import com.nexomc.nexo.items.ItemBuilder;
import com.nexomc.nexo.mechanics.custom_block.CustomBlockMechanic;
import fi.septicuss.tooltips.managers.integration.providers.BlockProvider;
import fi.septicuss.tooltips.managers.integration.wrappers.BlockWrapper;
import net.kyori.adventure.text.TextComponent;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class NexoBlockProvider implements BlockProvider {

    @Override
    public @Nullable BlockWrapper getBlock(@NotNull Block block) {
        CustomBlockMechanic customBlockMechanic = NexoBlocks.customBlockMechanic(block);
        if (customBlockMechanic == null) return null;

        String itemId = customBlockMechanic.getItemID();
        ItemBuilder itemBuilder = NexoItems.itemFromId(itemId);

        if (itemBuilder == null) {
            return new BlockWrapper(
                    "nexo",
                    itemId,
                    "",
                    block.getLocation()
            );
        }

        return new BlockWrapper(
                "nexo",
                itemId,
                ((TextComponent) itemBuilder.getItemName()).content(),
                block.getLocation()
        );
    }

    @Override
    public String identifier() {
        return "";
    }
}
