package fi.septicuss.tooltips.managers.preset.actions.command.impl;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.preset.actions.command.ActionCommand;
import fi.septicuss.tooltips.managers.preset.animation.Animations;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.PlayerTooltipData;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.entity.Player;

public class RedisplayCommand implements ActionCommand {

    @Override
    public void run(Player player, Arguments arguments) {

            PlayerTooltipData tooltipData = Tooltips.get().getTooltipManager().getPlayerTooltipData(player);
            tooltipData.clearAnimations();
            tooltipData.setTextChanged(true);
            tooltipData.setSourceText(Animations.parse(player, Tooltips.get().getPresetManager().getPreset(tooltipData.getDisplayedPreset()).getText()));

    }

    @Override
    public Validity validity(Arguments arguments) {
        return Validity.TRUE;
    }

}
