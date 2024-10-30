package fi.septicuss.tooltips.integrations.axgens;

import com.artillexstudios.axgens.generators.Generator;
import com.artillexstudios.axgens.generators.GeneratorArea;
import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.cache.integrations.axgens.AxGensCache;
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
    public Validity valid(Arguments args) {
        if (!Bukkit.getPluginManager().isPluginEnabled("AxGens")) {
            return Validity.of(false, "AxGens is required for this condition");
        }

        return Validity.TRUE;
    }

}
