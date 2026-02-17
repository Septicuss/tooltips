package fi.septicuss.tooltips.managers.integration.impl.vanilla;

import fi.septicuss.tooltips.managers.integration.providers.BlockProvider;
import fi.septicuss.tooltips.managers.integration.wrappers.BlockWrapper;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Provider for vanilla Minecraft blocks
 */
public class VanillaBlockProvider implements BlockProvider {

    @Override
    public @Nullable BlockWrapper getBlock(@NotNull Block block) {
        final Material material = block.getType();
        return this.getBlockWrapperFromMaterial(material, block.getLocation());
    }

    @Override
    public String identifier() {
        return "minecraft";
    }

    private BlockWrapper getBlockWrapperFromMaterial(Material material, Location location) {
        final String displayName = this.getMaterialDisplayName(material);
        return new BlockWrapper("minecraft", material.toString().toLowerCase(), displayName, location);
    }

    private String getMaterialDisplayName(Material material) {
        String[] nameParts = material.toString().toLowerCase().split("_");
        StringBuilder cleanNameBuilder = new StringBuilder();

        for (String part : nameParts) {
            cleanNameBuilder.append(Character.toTitleCase(part.charAt(0)))
                    .append(part.substring(1))
                    .append(" ");
        }

         return cleanNameBuilder.toString().trim();
    }

}
