package fi.septicuss.tooltips.managers.integration.impl.axgens;

import com.artillexstudios.axgens.api.AxGensAPI;
import com.artillexstudios.axgens.generators.Generator;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.Placeholders;
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


    }

}
