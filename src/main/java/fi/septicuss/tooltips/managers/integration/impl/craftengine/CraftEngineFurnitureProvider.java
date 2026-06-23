package fi.septicuss.tooltips.managers.integration.impl.craftengine;

import fi.septicuss.tooltips.managers.integration.providers.FurnitureProvider;
import fi.septicuss.tooltips.managers.integration.wrappers.FurnitureWrapper;
import net.momirealms.craftengine.bukkit.api.CraftEngineBlocks;
import net.momirealms.craftengine.bukkit.api.CraftEngineFurniture;
import net.momirealms.craftengine.bukkit.entity.furniture.BukkitFurniture;
import net.momirealms.craftengine.core.block.BlockDefinition;
import net.momirealms.craftengine.core.block.ImmutableBlockState;
import net.momirealms.craftengine.core.block.setting.BlockSettingsModifier;
import net.momirealms.craftengine.core.block.setting.BlockSettingsModifierType;
import net.momirealms.craftengine.core.block.setting.BlockSettingsModifiers;
import net.momirealms.craftengine.core.entity.furniture.FurnitureDefinition;
import net.momirealms.craftengine.core.entity.furniture.setting.FurnitureSettingsModifier;
import net.momirealms.craftengine.core.entity.furniture.setting.FurnitureSettingsModifierType;
import net.momirealms.craftengine.core.entity.furniture.setting.FurnitureSettingsModifiers;
import net.momirealms.craftengine.core.util.CustomDataType;
import net.momirealms.craftengine.core.util.Key;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Nullable;

public class CraftEngineFurnitureProvider implements FurnitureProvider {
    public static final CustomDataType<String> FURNITURE_NAME_KEY = new CustomDataType<>();
    public static final CustomDataType<String> BLOCK_NAME_KEY = new CustomDataType<>();
    public static final FurnitureSettingsModifierType<FurnitureSettingsModifier> FURNITURE_NAME = FurnitureSettingsModifiers.register(
            Key.of("tooltips", "furniture_name"),
            value -> settings -> settings.addCustomData(FURNITURE_NAME_KEY, value.getAsString())
    );
    public static final BlockSettingsModifierType<BlockSettingsModifier> BLOCK_NAME = BlockSettingsModifiers.register(
            Key.of("tooltips", "block_name"),
            value -> settings -> settings.addCustomData(BLOCK_NAME_KEY, value.getAsString())
    );

    @Nullable
    @Override
    public FurnitureWrapper getFurniture(Entity entity) {
        BukkitFurniture furniture = CraftEngineFurniture.getLoadedFurnitureByMetaEntity(entity);
        if (furniture == null) {
            furniture = CraftEngineFurniture.getLoadedFurnitureByCollider(entity);
            if (furniture == null) return null;
        }
        FurnitureDefinition definition = furniture.config;
        String furnitureName = definition.settings().getCustomData(FURNITURE_NAME_KEY);
        return new FurnitureWrapper(this.identifier(), definition.id().asString(), furnitureName != null ? furnitureName : definition.translationKey());
    }

    @Nullable
    @Override
    public FurnitureWrapper getFurniture(Block block) {
        ImmutableBlockState blockState = CraftEngineBlocks.getCustomBlockState(block);
        if (blockState == null) return null;
        BlockDefinition definition = blockState.owner().value();
        String blockName = blockState.settings().getCustomData(BLOCK_NAME_KEY);
        return new FurnitureWrapper(this.identifier(), definition.id().asString(), blockName != null ? blockName : definition.translationKey());
    }

    @Override
    public FurnitureWrapper getTargetFurniture(Player player) {
        BukkitFurniture target = CraftEngineFurniture.rayTrace(player, 10.0);
        if (target == null) {
            return null;
        }
        final String id = target.id().asString();
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
