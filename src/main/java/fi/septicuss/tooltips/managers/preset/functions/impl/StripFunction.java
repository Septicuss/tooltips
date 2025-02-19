package fi.septicuss.tooltips.managers.preset.functions.impl;

import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.preset.animation.Animations;
import fi.septicuss.tooltips.managers.preset.functions.Function;
import fi.septicuss.tooltips.managers.preset.functions.FunctionContext;
import fi.septicuss.tooltips.managers.preset.functions.Functions;
import org.bukkit.entity.Player;

import java.util.List;

public class StripFunction implements Function {

    @Override
    public String handle(Player player, FunctionContext context, List<Argument> args) {
        if (args.isEmpty()) return "";
        return Animations.stripAnimations(Functions.parseSingleLineWithContext(player, context.preset(), args.get(0).getAsString(), context.context()));
    }

}
