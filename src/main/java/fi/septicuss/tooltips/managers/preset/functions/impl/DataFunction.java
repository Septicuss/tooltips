package fi.septicuss.tooltips.managers.preset.functions.impl;

import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.preset.Preset;
import fi.septicuss.tooltips.managers.preset.PresetManager;
import fi.septicuss.tooltips.managers.preset.functions.Function;
import fi.septicuss.tooltips.managers.preset.functions.FunctionContext;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.List;

/**
 * $data("path.to.yml.data")
 */
public class DataFunction implements Function {

    private final PresetManager presetManager;

    public DataFunction(final PresetManager presetManager) {
        this.presetManager = presetManager;
    }

    @Override
    public String handle(Player player, FunctionContext context, List<Argument> args) {
        final String preset = context.preset();
        if (preset == null) return "";
        if (args.isEmpty()) return "";

        final ConfigurationSection data = getPresetDataSection(preset);
        if (data == null) return "";

        String path = args.get(0).process(player).getAsString();
        int index = -1;

        // ...[x] used for indexing lists
        if (path.length() > 3 && path.endsWith("]") && path.charAt(path.length() - 3) == '[') {
            index = Character.digit(path.charAt(path.length() - 2), 10);
            path = path.substring(0, path.length() - 3);
        }

        final Object result = data.get(path);
        if (result == null) return "";

        if (result instanceof List) {
            final List<?> list = data.getList(path);
            final boolean hasIndex = index != -1;
            final boolean indexInBounds = list.size() > index;

            if (hasIndex && indexInBounds) {
                return list.get(index).toString();
            }


            final List<String> stringList = list.stream().map(Object::toString).toList();
            return String.join(", ", stringList);
        }

        return result.toString();
    }

    private ConfigurationSection getPresetDataSection(final String presetPath) {
        final Preset preset = presetManager.getPreset(presetPath);
        if (preset == null || !preset.isValid()) return null;

        final ConfigurationSection section = preset.getSection().getParent();
        if (section == null) return null;
        if (!section.isSet("data")) return null;

        return section.getConfigurationSection("data");
    }

}
