package fi.septicuss.tooltips.managers.integration.impl.betonquest.actions;

import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversationData;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversationStage;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversations;
import fi.septicuss.tooltips.managers.preset.actions.command.ActionCommand;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.entity.Player;

public class SelectOptionCommand implements ActionCommand {

    @Override
    public void run(Player player, Arguments arguments) {
        final TooltipsConversationData data = TooltipsConversations.get(player.getUniqueId());
        if (data == null || !data.isReady()) return;

        final TooltipsConversationStage stage = data.getStage();
        if (stage == null) return;

        int answer = 1;

        if (arguments.has("1")) {
            answer = arguments.get("1").getAsInt() + 1;
        }

        // No options to choose from, end conversation
        if (stage.options().isEmpty()) {
            data.getConversation().endConversation();
            return;
        }

        answer = Math.max(answer, stage.options().size());
        data.passPlayerAnswer(answer);
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
