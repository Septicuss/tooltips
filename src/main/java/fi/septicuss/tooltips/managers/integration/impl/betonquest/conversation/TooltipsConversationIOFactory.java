package fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;

public class TooltipsConversationIOFactory implements ConversationIOFactory {

    @Override
    public ConversationIO parse(Conversation conversation, OnlineProfile onlineProfile) {
        return new TooltipsConversationIO(conversation, onlineProfile);
    }

}
