package fi.septicuss.tooltips.managers.integration.impl.axgens;

import com.artillexstudios.axgens.generators.Generator;
import com.artillexstudios.axgens.generators.GeneratorArea;
import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

public class LookingAtAxGen implements Condition {

    private static final String[] DISTANCE_ALIASES = { "distance", "d" };

    @Override
    public boolean check(Player player, Arguments args) {
        int distance = 3;

        if (args.isNumber(DISTANCE_ALIASES))
            distance = args.get(DISTANCE_ALIASES).getAsInt();

        var rayTrace = Utils.getRayTraceResult(player, distance);

        if (rayTrace == null) {
            return false;
        }

        final Block target = rayTrace.getHitBlock();

        if (target == null) {
            return false;
        }

        final Location blockLocation = target.getLocation();
        final Generator generator = GeneratorArea.getGeneratorAt(blockLocation);

        if (generator == null) {
            return false;
        }

        AxGensCache.cache(player, generator);
        return true;
    }

    @Override
    public void writeContext(Player player, Arguments args, Context context) {
        final AxGensCache.AxGen axGen = AxGensCache.get(player);

        context.put("axgens.id", axGen.id());
        context.put("axgens.name", axGen.name());
        context.put("axgens.tier", axGen.tier());
        context.put("axgens.speed", axGen.speed());
        context.put("axgens.drops", axGen.drops());
        context.put("axgens.drops.price", axGen.dropPrice());
        context.put("axgens.next.name", axGen.name());
        context.put("axgens.next.price", axGen.nextPrice());
        context.put("axgens.next.level", axGen.nextLevel());
        context.put("axgens.next.tier", axGen.nextTier());
        context.put("axgens.next.speed", axGen.nextSpeed());
        context.put("axgens.next.drops", axGen.nextDrops());
        context.put("axgens.next.drops.price", axGen.nextDropPrice());

    }

    @Override
    public Validity valid(Arguments args) {
        if (!Bukkit.getPluginManager().isPluginEnabled("AxGens")) {
            return Validity.of(false, "AxGens is required for this condition");
        }

        return Validity.TRUE;
    }

    @Override
    public String id() {
        return "lookingataxgen";
    }
}
