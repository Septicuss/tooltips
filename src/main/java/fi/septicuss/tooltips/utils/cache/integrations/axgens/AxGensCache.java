package fi.septicuss.tooltips.utils.cache.integrations.axgens;

import com.artillexstudios.axgens.generators.Generator;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;
import java.util.HashMap;
import java.util.UUID;

public class AxGensCache {

    private static final HashMap<UUID, Generator> AXGENS_GENERATOR_ID = new HashMap<>();

    public static @Nullable Generator get(Player player) {
        return AXGENS_GENERATOR_ID.get(player.getUniqueId());
    }

    public static void cache(Player player, Generator generator) {
        final UUID uuid = player.getUniqueId();
        AXGENS_GENERATOR_ID.put(uuid, generator);
    }

    public static void clear(Player player) {
        AXGENS_GENERATOR_ID.remove(player.getUniqueId());
    }

    public static void clear() {
        AXGENS_GENERATOR_ID.clear();
    }

}
