package fi.septicuss.tooltips.managers.integration.providers;

import fi.septicuss.tooltips.managers.integration.wrappers.BlockWrapper;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


/**
 * Provider responsible for block compatibility
 */
public interface BlockProvider extends Provider {

    @Nullable
    BlockWrapper getBlock(@NotNull Block block);

    /**
     * Check whether this provider has its own way of raytracing to find blocks belonging to this provider.
     *
     * @return True if this provider has its own way of raytracing to find blocks
     */
    default boolean hasCustomRayTrace() {
        return false;
    }

    /**
     * Method which allows the provider to specify its own way of getting a players targeted block.
     * <p>
     * This method is only used if {@link #hasCustomRayTrace()} returns true.
     *
     * @param player The player whose targeted block to find
     * @return A wrapper for a targeted block or null if no target block was found
     */
    default BlockWrapper getTargetBlock(Player player) {
        return null;
    }

}
