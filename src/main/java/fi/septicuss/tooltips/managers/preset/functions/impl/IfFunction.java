package fi.septicuss.tooltips.managers.preset.functions.impl;

import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.preset.functions.Function;
import fi.septicuss.tooltips.managers.preset.functions.FunctionContext;
import org.bukkit.entity.Player;

import java.util.List;

public class IfFunction implements Function {

    @Override
    public String handle(Player player, FunctionContext context, List<Argument> args) {
        if (args.isEmpty() || args.size() < 3) return "";

        final Argument conditionArgument = args.get(0).process(player);

        if (Boolean.parseBoolean(conditionArgument.getAsString())) {
            return args.get(1).process(player).getAsString();
        }

        return args.get(2).process(player).getAsString();
    }

}
