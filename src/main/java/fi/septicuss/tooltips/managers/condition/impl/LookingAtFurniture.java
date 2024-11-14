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


    @Override
    public boolean check(Player player, Arguments args, Context context) {
        final IntegrationManager manager = Tooltips.get().getIntegrationManager();

        MultiString id = null;
        int distance = 3;

        if (args.has(DISTANCE))
            distance = args.get(DISTANCE).getAsInt();

        if (args.has(ID))
            id = MultiString.of(args.get(ID).getAsString());

        final MultiString finalizedId = id;

        Predicate<Block> blockPredicate = (block -> {
            if (block == null) return false;
            final Optional<FurnitureWrapper> optionalFurniture = manager.getFurniture(block);
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
            return false;
        }

        // Block
        if (rayTrace.getHitBlock() != null) {
            final Block block = rayTrace.getHitBlock();
            final Optional<FurnitureWrapper> optionalFurniture = manager.getFurniture(block);

            if (optionalFurniture.isEmpty()) {
                return true;
            }

            final FurnitureWrapper furniture = optionalFurniture.get();
            final String furnitureId = furniture.id();

            final boolean outcome = (finalizedId == null || finalizedId.contains(furnitureId));

            if (outcome) {
                context.put("furniture.id", furniture.id());
                context.put("furniture.name", furniture.displayName());
            }

            return outcome;
        }

        // Entity
        if (rayTrace.getHitEntity() != null) {
            final Entity entity = rayTrace.getHitEntity();
            final Optional<FurnitureWrapper> optionalFurniture = manager.getFurniture(entity);

            if (optionalFurniture.isEmpty()) {
                return false;
            }

            final FurnitureWrapper furniture = optionalFurniture.get();

            final boolean outcome = (finalizedId == null || finalizedId.contains(furniture.id()));

            if (outcome) {
                context.put("furniture.id", furniture.id());
                context.put("furniture.name", furniture.displayName());
            }

            return outcome;
        }

        return false;
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

}
