package fi.septicuss.tooltips.managers.preset.functions.impl;


import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.preset.functions.Function;
import fi.septicuss.tooltips.managers.preset.functions.FunctionContext;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.PlayerTooltipData;
import fi.septicuss.tooltips.utils.Utils;
import org.bukkit.entity.Player;

import java.util.List;

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

        final String path = args.get(0).process(player, context.context()).getAsString();
        final String queryPath = Utils.stripQueryPath(path);

        final Context tooltipContext = context.context();
        if (!tooltipContext.has(queryPath)) return "";

        final Object value = tooltipContext.get(queryPath);
        if (value == null) return "";

        final Object result = Utils.queryObject(path, value);

        return result.toString();
    }

}
