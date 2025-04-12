package fi.septicuss.tooltips.managers.preset.functions.impl;

import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.preset.functions.Function;
import fi.septicuss.tooltips.managers.preset.functions.FunctionContext;
import fi.septicuss.tooltips.utils.Placeholders;
import fi.septicuss.tooltips.utils.Text;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.List;

public class ParseFunction implements Function {

    @Override
    public String handle(Player player, FunctionContext context, List<Argument> args) {
        if (args.isEmpty()) return "";
        if (args.size() < 2) return "";

        final String targetName = args.get(0).process(player, context.context()).getAsString();
        final String string = args.get(1).getAsString();

        final Player target = Bukkit.getPlayerExact(targetName);
        if (target == null) return "";

        return Placeholders.replacePlaceholders(player, string);
    }


}
