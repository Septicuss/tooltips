package fi.septicuss.tooltips.utils.placeholder.impl;

import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.utils.placeholder.PlaceholderParser;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;
import fi.septicuss.tooltips.utils.variable.Variables;
import org.bukkit.entity.Player;

public class VariablePlaceholder implements PlaceholderParser {

    @Override
    public String parse(Player player, String placeholder) {
        if (!placeholder.startsWith("var_"))
            return null;
        boolean global = placeholder.startsWith("var_global_");
        int cutIndex = (global ? 11 : 4);

        String variableName = placeholder.substring(cutIndex);
        variableName = Placeholders.replacePlaceholders(player, variableName);

        Argument returnArgument = null;

        if (global) {
            returnArgument = Variables.LOCAL.getVar(variableName);
        } else {
            returnArgument = Variables.LOCAL.getVar(player, variableName);
        }

        if (returnArgument == null || returnArgument.getAsString() == null)
            return "0";

        return returnArgument.getAsString();
    }
}
