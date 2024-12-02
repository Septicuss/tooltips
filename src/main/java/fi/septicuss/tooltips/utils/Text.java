package fi.septicuss.tooltips.utils;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.preset.animation.Animations;
import fi.septicuss.tooltips.managers.preset.functions.Functions;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.PlayerTooltipData;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Text {

    private static final List<String> PREPROCESS_FUNCTIONS = List.of("preprocess", "strip");

    public static List<String> preprocessText(final Player player, final List<String> text) {
        final PlayerTooltipData playerTooltipData = Tooltips.get().getTooltipManager().getPlayerTooltipData(player);
        final List<String> placeholdersReplaced = Placeholders.replacePlaceholders(player, text);
        final List<String> functionsParsed = Functions.parse(player, playerTooltipData.hasDisplayedPreset() ? playerTooltipData.getDisplayedPreset() : playerTooltipData.getSentPreset(), placeholdersReplaced, PREPROCESS_FUNCTIONS);
        return Animations.parse(player, functionsParsed);
    }

    public static String processText(final Player player, final String text) {
        final PlayerTooltipData playerTooltipData = Tooltips.get().getTooltipManager().getPlayerTooltipData(player);
        final String placeholdersReplaced = Placeholders.replacePlaceholders(player, text);
        final String functionsParsed = Functions.parse(player, playerTooltipData.getDisplayedPreset(), placeholdersReplaced);
        return Animations.parse(player, functionsParsed);
    }

    public static List<String> processText(final Player player, final List<String> text) {
        final PlayerTooltipData playerTooltipData = Tooltips.get().getTooltipManager().getPlayerTooltipData(player);
        final List<String> placeholdersReplaced = Placeholders.replacePlaceholders(player, text);
        final List<String> functionsParsed = Functions.parse(player, playerTooltipData.hasDisplayedPreset() ? playerTooltipData.getDisplayedPreset() : playerTooltipData.getSentPreset(), placeholdersReplaced);
        final List<String> result = new ArrayList<>();
        for (String line : functionsParsed) {
            if (line.contains("\n")) {
                line = line.replace("\n", "\\n");
            }

            if (line.contains("\\n")) {
                result.addAll(Arrays.asList(line.split("\\\\n")));
            } else {
                result.add(line);
            }
        }

        return Animations.parse(player, result);
    }

}
