package fi.septicuss.tooltips.managers.preset.functions;


import fi.septicuss.tooltips.managers.condition.argument.Argument;
import org.bukkit.entity.Player;

import java.util.List;

public interface Function {

    String handle(Player player, FunctionContext context, List<Argument> args);

    default boolean isAcceptRawInput() {
        return false;
    }

}
