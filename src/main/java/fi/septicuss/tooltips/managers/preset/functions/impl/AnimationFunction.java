package fi.septicuss.tooltips.managers.preset.functions.impl;

import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.preset.animation.Animations;
import fi.septicuss.tooltips.managers.preset.animation.ParsedAnimation;
import fi.septicuss.tooltips.managers.preset.functions.Function;
import fi.septicuss.tooltips.managers.preset.functions.FunctionContext;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.UUID;

public class AnimationFunction implements Function {

    @Override
    public String handle(Player player, FunctionContext context, List<Argument> args) {
        if (args.isEmpty()) return "";
        final UUID uuid = UUID.fromString(args.get(0).getAsString());
        final ParsedAnimation animation = Animations.get(uuid);

        if (animation == null) return "";
        return animation.text();
    }

}
