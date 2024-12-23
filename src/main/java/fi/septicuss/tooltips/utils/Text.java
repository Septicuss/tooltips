package fi.septicuss.tooltips.utils;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.preset.animation.Animations;
import fi.septicuss.tooltips.managers.preset.functions.Functions;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.PlayerTooltipData;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Text {

    private static final List<String> PREPROCESS_FUNCTIONS = List.of("preprocess", "strip", "static");

    public static List<String> preprocessAnimatedText(final Player player, final List<String> text) {
        final List<String> placeholdersReplaced = Placeholders.replacePlaceholders(player, text);
        final List<String> functionsParsed = Functions.parse(player, Text.getPreset(player), placeholdersReplaced, PREPROCESS_FUNCTIONS);
        return Animations.parse(player, functionsParsed);
    }

    public static String processText(final Player player, final String text) {
        final String placeholdersReplaced = Placeholders.replacePlaceholders(player, text);
        return Functions.parse(player, Text.getPreset(player), placeholdersReplaced);
    }

    public static String processTextWithContext(final Player player, final String text, Context context) {
        final String placeholdersReplaced = Placeholders.replacePlaceholders(player, text);
        return Functions.parse(player, Text.getPreset(player), placeholdersReplaced, null, context);
    }

    public static List<String> processText(final Player player, final List<String> text) {
        final List<String> placeholdersReplaced = Placeholders.replacePlaceholders(player, text);
        final List<String> functionsParsed = Functions.parse(player, Text.getPreset(player), placeholdersReplaced);
        return Text.splitText(functionsParsed);
    }

    public static List<String> splitText(List<String> text) {
        final List<String> result = new ArrayList<>();
        for (String line : text) {
            if (line.contains("\n")) {
                line = line.replace("\n", "\\n");
            }

            if (line.contains("\\n")) {
                result.addAll(Arrays.asList(line.split("\\\\n")));
            } else {
                result.add(line);
            }
        }
        return result;
    }

    private static String getPreset(Player player) {
        final PlayerTooltipData playerTooltipData = Tooltips.get().getTooltipManager().getPlayerTooltipData(player);
        final String preset = playerTooltipData.hasDisplayedPreset() ? playerTooltipData.getDisplayedPreset() : playerTooltipData.getSentPreset();
        return preset == null ? playerTooltipData.getCheckedPreset() : preset;
    }

}
