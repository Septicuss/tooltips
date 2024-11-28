package fi.septicuss.tooltips.managers.preset.actions.command.impl;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.preset.actions.command.ActionCommand;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.PlayerTooltipData;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.entity.Player;

public class SkipCommand implements ActionCommand {

    @Override
    public void run(Player player, Arguments arguments) {

        boolean skipAll = true;

        if (arguments.has("1")) {
            final String firstArg = arguments.get("1").getAsString();
            if (firstArg.equalsIgnoreCase("current") || firstArg.equalsIgnoreCase("curr")) {
                skipAll = false;
            }

        }

        final PlayerTooltipData data = Tooltips.get().getTooltipManager().getPlayerTooltipData(player);

        if (skipAll) {
            data.skipAllAnimations();
            return;
        }

        data.skipCurrentAnimation();

    }

    @Override
    public Validity validity(Arguments arguments) {
        return Validity.TRUE;
    }

}
