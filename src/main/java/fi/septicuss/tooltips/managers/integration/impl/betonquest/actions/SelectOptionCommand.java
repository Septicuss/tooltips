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
        if (data == null || !data.isReady() || data.isEnding()) return;

        final TooltipsConversationStage stage = data.getStage();
        if (stage == null) return;

        int answer = 0;

        if (arguments.has("1")) {
            answer = arguments.get("1").getAsInt();
        }

        // No options to choose from, end conversation
        if (stage.options().isEmpty()) {
            data.getConversation().endConversation();
            return;
        }

        // Cap the answer to the total amount of answers
        answer = Math.min(answer, stage.options().size() - 1);

        // Pass along
        data.passPlayerAnswer(answer + 1);
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
