package fi.septicuss.tooltips.managers.integration.impl.betonquest.actions;

import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversationData;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversationIO;
import fi.septicuss.tooltips.managers.preset.actions.command.ActionCommand;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.feature.ConversationApi;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.bukkit.entity.Player;

public class EndConversationCommand  implements ActionCommand {

    @Override
    public void run(Player player, Arguments arguments) {

        OnlineProfile profile = BetonQuest.getInstance().getProfileProvider().getProfile(player);

        if (profile == null)
            return;

        Conversation conversation = BetonQuest.getInstance().getFeatureApi().conversationApi().getActive(profile);

        if (conversation != null) {
            conversation.endConversation();
        }

        if (!TooltipsConversationIO.isInConversation(player)) {
            return;
        }
        final TooltipsConversationData conversationData = TooltipsConversationIO.getData(player);
        if (conversationData == null) return;

        conversationData.end();
        conversationData.getConversation().endConversation();

    }

    @Override
    public Validity validity(Arguments arguments) {
        return Validity.TRUE;
    }

}
