package fi.septicuss.tooltips.managers.preset.functions.impl;


import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.preset.functions.Function;
import fi.septicuss.tooltips.managers.preset.functions.FunctionContext;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.PlayerTooltipData;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Objects;

/**
 * $context(key)
 */
public class ContextFunction implements Function {

    private final Tooltips plugin;

    public ContextFunction(Tooltips plugin) {
        this.plugin = plugin;
    }

    @Override
    public String handle(Player player, FunctionContext context, List<Argument> args) {
        if (args.isEmpty()) return "";
        final String key = args.get(0).process(player).getAsString();
        final PlayerTooltipData tooltipData = plugin.getTooltipManager().getPlayerTooltipData(player);

        final Context tooltipContext = tooltipData.getContext();
        if (!tooltipContext.has(key)) return "";

        return Objects.requireNonNull(tooltipContext.get(key)).toString();
    }

}
