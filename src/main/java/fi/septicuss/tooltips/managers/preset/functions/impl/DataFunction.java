package fi.septicuss.tooltips.managers.preset.functions.impl;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.preset.Preset;
import fi.septicuss.tooltips.managers.preset.functions.Function;
import fi.septicuss.tooltips.managers.preset.functions.FunctionContext;
import fi.septicuss.tooltips.utils.Utils;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * $data("path.to.yml.data")
 */
public class DataFunction implements Function {

    private final Tooltips plugin;

    public DataFunction(final Tooltips plugin) {
        this.plugin = plugin;
    }

    @Override
    public String handle(Player player, FunctionContext context, List<Argument> args) {
        final String preset = context.preset();
        if (preset == null) return "";
        if (args.isEmpty()) return "";

        final ConfigurationSection data = getPresetDataSection(preset);
        if (data == null) return "";

        final String path = args.get(0).process(player).getAsString();
        final String queryPath = Utils.stripQueryPath(path);

        final Object object = data.get(queryPath);
        if (object == null) return "";

        final Object result = Utils.queryObject(path, object);
        if (result == null) return "";

        return result.toString();
    }

    private ConfigurationSection getPresetDataSection(final String presetPath) {
        final Preset preset = plugin.getPresetManager().getPreset(presetPath);
        if (preset == null || !preset.isValid()) return null;

        final ConfigurationSection section = preset.getSection().getParent();
        if (section == null) return null;
        if (!section.isSet("data")) return null;

        return section.getConfigurationSection("data");
    }

}
