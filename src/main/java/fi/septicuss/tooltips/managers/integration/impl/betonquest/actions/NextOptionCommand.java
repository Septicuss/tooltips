package fi.septicuss.tooltips.managers.integration.impl.betonquest.actions;

import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversationData;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversationIO;
import fi.septicuss.tooltips.managers.preset.actions.command.ActionCommand;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.entity.Player;

public class NextOptionCommand implements ActionCommand {

    @Override
    public void run(Player player, Arguments arguments) {
        if (!TooltipsConversationIO.isInConversation(player)) {
            return;
        }

        final TooltipsConversationData data = TooltipsConversationIO.getData(player);

        if (!data.isReady()) {
            return;
        }

        data.nextOption();

    }

    @Override
    public Validity validity(Arguments arguments) {
        return Validity.TRUE;
    }

}
