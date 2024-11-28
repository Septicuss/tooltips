package fi.septicuss.tooltips.managers.condition.impl;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.condition.type.MultiString;
import fi.septicuss.tooltips.managers.integration.IntegrationManager;
import fi.septicuss.tooltips.managers.integration.wrappers.FurnitureWrapper;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Optional;
import java.util.function.Predicate;

public class LookingAtFurniture implements Condition {

    private static final String[] DISTANCE = {"d", "distance"};
    private static final String[] ID = {"id"};

    private final IntegrationManager integrationManager;

    public LookingAtFurniture(IntegrationManager integrationManager) {
        this.integrationManager = integrationManager;
    }


    @Override
    public boolean check(Player player, Arguments args) {
        return (getLookedAtFurniture(player, args) != null);
    }

    @Override
    public void writeContext(Player player, Arguments args, Context context) {
        final FurnitureWrapper furniture = getLookedAtFurniture(player, args);

        if (furniture != null) {
            context.put("furniture.id", furniture.id());
            context.put("furniture.name", furniture.displayName());
            context.put("furniture.plugin", furniture.plugin());
        }
    }

    private FurnitureWrapper getLookedAtFurniture(Player player, Arguments args) {

        MultiString id = null;
        int distance = 3;

        if (args.has(DISTANCE))
            distance = args.get(DISTANCE).getAsInt();

        if (args.has(ID))
            id = MultiString.of(args.get(ID).getAsString());

        final MultiString finalizedId = id;

        Predicate<Block> blockPredicate = (block -> {
            if (block == null) return false;
            final Optional<FurnitureWrapper> optionalFurniture = integrationManager.getFurniture(block);
            if (optionalFurniture.isEmpty()) return false;
            if (finalizedId == null) return true;
            return (finalizedId.contains(optionalFurniture.get().id()));
        });

        Predicate<Entity> entityFilter = (entity -> {
            if (entity == null) return false;
            if (entity.equals(player)) return false;
            return Tooltips.FURNITURE_ENTITIES.contains(entity.getType());
        });

        var rayTrace = Utils.getRayTrace(player, distance, blockPredicate, entityFilter);

        if (rayTrace == null) {
            return null;
        }

        // Block
        if (rayTrace.getHitBlock() != null) {
            final Block block = rayTrace.getHitBlock();
            final Optional<FurnitureWrapper> optionalFurniture = integrationManager.getFurniture(block);

            if (optionalFurniture.isEmpty()) {
                return null;
            }

            final FurnitureWrapper furniture = optionalFurniture.get();
            final String furnitureId = furniture.id();

            final boolean outcome = (finalizedId == null || finalizedId.contains(furnitureId));
            return outcome ? furniture : null;
        }

        // Entity
        if (rayTrace.getHitEntity() != null) {
            final Entity entity = rayTrace.getHitEntity();
            final Optional<FurnitureWrapper> optionalFurniture = integrationManager.getFurniture(entity);

            if (optionalFurniture.isEmpty()) {
                return null;
            }

            final FurnitureWrapper furniture = optionalFurniture.get();

            final boolean outcome = (finalizedId == null || finalizedId.contains(furniture.id()));
            return outcome ? furniture : null;
        }

        return null;
    }

    @Override
    public Validity valid(Arguments args) {

        if (Tooltips.get().getIntegrationManager().getFurnitureProviders().isEmpty()) {
            return Validity.of(false, "No furniture plugin present");
        }

        if (args.has(DISTANCE) && !args.get(DISTANCE).isNumber()) {
            return Validity.of(false, "Distance must be a number");
        }

        return Validity.TRUE;
    }

    @Override
    public String id() {
        return "lookingatfurniture";
    }
}
