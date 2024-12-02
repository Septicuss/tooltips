package fi.septicuss.tooltips.managers.integration.impl.betonquest.actions;

import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversationData;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversationIO;
import fi.septicuss.tooltips.managers.preset.actions.command.ActionCommand;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.entity.Player;

public class SelectOptionCommand implements ActionCommand {

    @Override
    public void run(Player player, Arguments arguments) {
        if (!TooltipsConversationIO.isInConversation(player)) {
            return;
        }

        final TooltipsConversationData conversationData = TooltipsConversationIO.getData(player);

        if (!conversationData.isReady()) {
            return;
        }

        int answer = 1;

        if (arguments.has("1")) {
            answer = arguments.get("1").getAsInt() + 1;
        }

        if (conversationData.getOptions().isEmpty()) {
            conversationData.end();
            conversationData.getConversation().endConversation();
            return;
        }

        if (answer > conversationData.getOptions().size() - 1) {
            answer = Math.max(answer, conversationData.getOptions().size());
        }

        conversationData.getConversation().passPlayerAnswer(answer);

    }

    @Override
    public Validity validity(Arguments arguments) {
        if (arguments.has("1")) {
            if (!arguments.get("1").isNumber()) {
                return Validity.of(false, "Selected option must be a number");
            }
        }


        return Validity.TRUE;
    }

}
