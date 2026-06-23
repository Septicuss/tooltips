package fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation;

import fi.septicuss.tooltips.Tooltips;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.betonquest.betonquest.conversation.ConversationIOFactory;

public class TooltipsConversationIOFactory implements ConversationIOFactory {

    private final Tooltips plugin;

    public TooltipsConversationIOFactory(Tooltips plugin) {
        this.plugin = plugin;
    }

    @Override
    public ConversationIO parse(Conversation conversation, OnlineProfile onlineProfile) {
        return new TooltipsConversationIO(plugin, conversation, onlineProfile);
    }

}
