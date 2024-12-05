package fi.septicuss.tooltips.managers.integration.impl.axgens;

import com.artillexstudios.axgens.api.AxGensAPI;
import com.artillexstudios.axgens.generators.Generator;
import fi.septicuss.tooltips.utils.Utils;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class AxGensCache {

    public static record AxGen(int id, String name, String tier, String speed, List<String> drops, List<String> dropPrice, String nextName, String nextTier, String nextSpeed, List<String> nextDrops, String nextPrice, String nextLevel, List<String> nextDropPrice) { }

    private static final HashMap<UUID, AxGen> CACHED_GENERATORS = new HashMap<>();

    public static AxGen get(Player player) {
        return CACHED_GENERATORS.get(player.getUniqueId());
    }

    public static void cache(Player player, Generator generator) {
        final UUID uuid = player.getUniqueId();

        // Already cached
        if (CACHED_GENERATORS.containsKey(uuid) && CACHED_GENERATORS.get(uuid).id() == generator.getId()) {
            return;
        }

        final AxGen axGen = getAxGen(player, generator);

        CACHED_GENERATORS.put(uuid, axGen);
    }

    public static void clear(Player player) {
        CACHED_GENERATORS.remove(player.getUniqueId());
    }

    public static void clear() {
        CACHED_GENERATORS.clear();
    }

    private static AxGen getAxGen(Player player, Generator generator) {

        // CURRENT

        final int id = generator.getId();

        String name = null;

        ItemStack item = generator.getcTier().getGenItem();

        if (item.getItemMeta() != null && item.getItemMeta().hasDisplayName()) {
            name = item.getItemMeta().getDisplayName();
        }

        String tier = String.valueOf(generator.getcTier());
        String speed = String.valueOf(generator.getcTier().getSpeed());

        List<String> drops = new ArrayList<>();

        for (Map.Entry<ItemStack, Double> entry : generator.getcTier().getDropItems().entrySet()) {
            final ItemStack drop = entry.getKey();
            final double chance = entry.getValue();

            drops.add(String.format("%s (%s%%)", drop.getItemMeta().getDisplayName(), chance));
        }

        List<String> dropPrices = new ArrayList<>();
        for (Map.Entry<ItemStack, Double> entry : generator.getcTier().getDropItems().entrySet()) {
            final ItemStack drop = entry.getKey();

            dropPrices.add(String.format("%s (%s)", drop.getItemMeta().getDisplayName(), AxGensAPI.getPrice(player, drop)));
        }

        String nextName = null;

        item = generator.getnTier().getGenItem();

        if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
            nextName = item.getItemMeta().getDisplayName();
        }

        String nextTier = String.valueOf(generator.getnTier().getTier());
        String nextSpeed = String.valueOf(generator.getnTier().getSpeed());

        List<String> nextDrops = new ArrayList<>();
        for(Map.Entry<ItemStack, Double> entry : generator.getnTier().getDropItems().entrySet()) {
            final ItemStack drop = entry.getKey();
            final double chance = entry.getValue();

            nextDrops.add(String.format("%s (%s%%)", drop.getItemMeta().getDisplayName(), chance));
        }

        String nextPrice = Utils.withSuffix((long) generator.getnTier().getPrice());
        String nextLevel = String.valueOf(generator.getnTier().getLevelNeeded());

        List<String> nextDropPrices = new ArrayList<>();
        for (Map.Entry<ItemStack, Double> entry : generator.getnTier().getDropItems().entrySet()) {
            final ItemStack drop = entry.getKey();

            nextDropPrices.add(String.format("%s (%s)", drop.getItemMeta().getDisplayName(), AxGensAPI.getPrice(player, drop)));
        }

        return new AxGen(id, name, tier, speed, drops, dropPrices, nextName, nextTier, nextSpeed, nextDrops, nextPrice, nextLevel, nextDropPrices);
    }

}
