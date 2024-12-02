package fi.septicuss.tooltips.managers.preset.functions.impl;

import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.preset.functions.Function;
import fi.septicuss.tooltips.managers.preset.functions.FunctionContext;
import fi.septicuss.tooltips.managers.preset.functions.Functions;
import org.bukkit.entity.Player;

import java.util.List;

public class PreprocessFunction implements Function {

    @Override
    public String handle(Player player, FunctionContext context, List<Argument> args) {
        if (args.isEmpty()) return "";

        final Argument argument = args.get(0);
        return Functions.parse(player, context.preset(), argument.getAsString());
    }

}
