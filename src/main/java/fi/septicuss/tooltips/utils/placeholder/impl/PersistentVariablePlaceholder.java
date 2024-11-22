package fi.septicuss.tooltips.utils.placeholder.impl;

import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.utils.placeholder.PlaceholderParser;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;
import fi.septicuss.tooltips.utils.variable.Variables;
import org.bukkit.entity.Player;

public class PersistentVariablePlaceholder implements PlaceholderParser {

    @Override
    public String parse(Player player, String placeholder) {
        if (!placeholder.startsWith("persistentvar_"))
            return null;

        boolean global = placeholder.startsWith("persistentvar_global_");
        int cutIndex = (global ? 21 : 14);

        String variableName = placeholder.substring(cutIndex);
        variableName = Placeholders.replacePlaceholders(player, variableName);

        Argument returnArgument = null;

        if (global) {
            returnArgument = Variables.PERSISTENT.getVar(variableName);
        } else {
            returnArgument = Variables.PERSISTENT.getVar(player, variableName);
        }

        if (returnArgument == null || returnArgument.getAsString() == null)
            return "0";

        return returnArgument.getAsString();
    }
}
