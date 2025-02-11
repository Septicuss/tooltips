package fi.septicuss.tooltips.managers.preset.functions.impl.variable;

import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.preset.functions.Function;
import fi.septicuss.tooltips.managers.preset.functions.FunctionContext;
import fi.septicuss.tooltips.managers.preset.functions.Functions;
import fi.septicuss.tooltips.utils.variable.Variables;
import org.bukkit.entity.Player;

import java.util.List;

public class HasPVarFunction implements Function {

    @Override
    public String handle(Player player, FunctionContext context, List<Argument> args) {
        if (args.isEmpty()) return "false";

        final String first = args.get(0).getAsString();
        boolean global = first.equalsIgnoreCase("global");

        String variableName = (args.size() == 1 ? args.get(0).getAsString() : args.get(1).getAsString());
        variableName = Functions.parse(player, context.preset(), variableName);

        Argument returnArgument = null;

        if (global) {
            returnArgument = Variables.PERSISTENT.getVar(variableName);
        } else {
            returnArgument = Variables.PERSISTENT.getVar(player, variableName);
        }

        if (returnArgument == null || returnArgument.getAsString() == null) {
            return "false";
        }

        return "true";
    }

}