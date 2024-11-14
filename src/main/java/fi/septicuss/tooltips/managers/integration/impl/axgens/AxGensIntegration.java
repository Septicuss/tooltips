package fi.septicuss.tooltips.managers.integration.impl.axgens;

import com.artillexstudios.axgens.api.AxGensAPI;
import com.artillexstudios.axgens.generators.Generator;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.cache.integrations.axgens.AxGensCache;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;
import org.bukkit.Bukkit;
import org.bukkit.inventory.ItemStack;

import java.util.Map;
import java.util.StringJoiner;

public class AxGensIntegration {

    public static void registerIntegration() {

        if (!Bukkit.getPluginManager().isPluginEnabled("AxGens")) {
            return;
        }

        // Current generator

        Placeholders.addLocal("axgens_name", (player, placeholder) -> {
            final Generator generator = AxGensCache.get(player);

            if (generator == null) {
                return null;
            }

            final ItemStack item = generator.getcTier().getGenItem();

            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                return item.getItemMeta().getDisplayName();
            }

            return null;
        });

        Placeholders.addLocal("axgens_tier", ((player, placeholder) -> {
            final Generator generator = AxGensCache.get(player);

            if (generator == null) {
                return null;
            }

            return String.valueOf(generator.getTier());
        }));

        Placeholders.addLocal("axgens_speed", ((player, placeholder) -> {
            final Generator generator = AxGensCache.get(player);

            if (generator == null) {
                return null;
            }

            return String.valueOf(generator.getcTier().getSpeed());
        }));

        Placeholders.addLocal("axgens_drops", ((player, placeholder) -> {
            final Generator generator = AxGensCache.get(player);

            if (generator == null) {
                return null;
            }

            final StringJoiner joiner = new StringJoiner(", ");

            for (Map.Entry<ItemStack, Double> entry : generator.getcTier().getDropItems().entrySet()) {
                final ItemStack drop = entry.getKey();
                final double chance = entry.getValue();

                joiner.add(String.format("%s (%s%%)", drop.getItemMeta().getDisplayName(), chance));
            }

            return joiner.toString();
        }));

        Placeholders.addLocal("axgens_drop_price", ((player, placeholder) -> {
            final Generator generator = AxGensCache.get(player);

            if (generator == null) {
                return null;
            }

            final StringJoiner joiner = new StringJoiner(", ");

            for (Map.Entry<ItemStack, Double> entry : generator.getcTier().getDropItems().entrySet()) {
                final ItemStack drop = entry.getKey();

                joiner.add(String.format("%s (%s)", drop.getItemMeta().getDisplayName(), AxGensAPI.getPrice(player, drop)));
            }

            return joiner.toString();
        }));

        // Next generator
        Placeholders.addLocal("axgens_next_name", (player, placeholder) -> {
            final Generator generator = AxGensCache.get(player);

            if (generator == null) {
                return null;
            }

            final ItemStack item = generator.getnTier().getGenItem();

            if (item.hasItemMeta() && item.getItemMeta().hasDisplayName()) {
                return item.getItemMeta().getDisplayName();
            }

            return null;
        });

        Placeholders.addLocal("axgens_next_tier", ((player, placeholder) -> {
            final Generator generator = AxGensCache.get(player);

            if (generator == null) {
                return null;
            }

            return String.valueOf(generator.getnTier().getTier());
        }));

        Placeholders.addLocal("axgens_next_speed", ((player, placeholder) -> {
            final Generator generator = AxGensCache.get(player);

            if (generator == null) {
                return null;
            }

            return String.valueOf(generator.getnTier().getSpeed());
        }));

        Placeholders.addLocal("axgens_next_drops", ((player, placeholder) -> {
            final Generator generator = AxGensCache.get(player);

            if (generator == null) {
                return null;
            }

            final StringJoiner joiner = new StringJoiner(", ");

            for (Map.Entry<ItemStack, Double> entry : generator.getnTier().getDropItems().entrySet()) {
                final ItemStack drop = entry.getKey();
                final double chance = entry.getValue();

                joiner.add(String.format("%s (%s%%)", drop.getItemMeta().getDisplayName(), chance));
            }

            return joiner.toString();
        }));
        Placeholders.addLocal("axgens_next_price", ((player, placeholder) -> {
            final Generator generator = AxGensCache.get(player);

            if (generator == null) {
                return null;
            }

            return Utils.withSuffix((long) generator.getnTier().getPrice());
        }));

        Placeholders.addLocal("axgens_next_level", ((player, placeholder) -> {
            final Generator generator = AxGensCache.get(player);

            if (generator == null) {
                return null;
            }

            return String.valueOf(generator.getnTier().getLevelNeeded());
        }));

        Placeholders.addLocal("axgens_next_drop_price", ((player, placeholder) -> {
            final Generator generator = AxGensCache.get(player);

            if (generator == null) {
                return null;
            }

            final StringJoiner joiner = new StringJoiner(", ");

            for (Map.Entry<ItemStack, Double> entry : generator.getnTier().getDropItems().entrySet()) {
                final ItemStack drop = entry.getKey();

                joiner.add(String.format("%s (%s)", drop.getItemMeta().getDisplayName(), AxGensAPI.getPrice(player, drop)));
            }

            return joiner.toString();
        }));

    }

}
