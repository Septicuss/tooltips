package fi.septicuss.tooltips.managers.preset.functions.impl;

import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.preset.functions.Function;
import fi.septicuss.tooltips.managers.preset.functions.FunctionContext;
import fi.septicuss.tooltips.managers.preset.functions.Functions;
import fi.septicuss.tooltips.utils.Utils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class StaticFunction implements Function {

    @Override
    public String handle(Player player, FunctionContext context, List<Argument> args) {
        if (args.isEmpty()) return "";
        final List<String> list = new ArrayList<>();
        for (Argument arg : args) {
            list.add(arg.getAsString());
        }

        String joined = String.join(",", list);

        if (Utils.isSurroundedByQuotes(joined)) {
            joined = Utils.removeQuotes(joined);
        }

        return Functions.parse(player, context.preset(), joined);
    }

    @Override
    public boolean isAcceptRawInput() {
        return true;
    }
}
