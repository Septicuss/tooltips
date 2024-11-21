package fi.septicuss.tooltips.utils;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.preset.functions.Functions;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.PlayerTooltipData;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;
import org.bukkit.entity.Player;

import java.util.List;

public class Text {

    public static String processText(final Player player, final String text) {
        final PlayerTooltipData playerTooltipData = Tooltips.get().getTooltipManager().getPlayerTooltipData(player);
        final String placeholdersReplaced = Placeholders.replacePlaceholders(player, text);
        return Functions.parse(player, playerTooltipData.getDisplayedPreset(), placeholdersReplaced);
    }

    public static List<String> processText(final Player player, final List<String> text) {
        final PlayerTooltipData playerTooltipData = Tooltips.get().getTooltipManager().getPlayerTooltipData(player);
        final List<String> placeholdersReplaced = Placeholders.replacePlaceholders(player, text);
        return Functions.parse(player, playerTooltipData.getDisplayedPreset(), placeholdersReplaced);
    }

}
