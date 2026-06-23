package fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation;

import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public final class TooltipsConversations {

    private static final Map<UUID, TooltipsConversationData> CONVERSATIONS = new HashMap<>();

    private TooltipsConversations() { }

    public static void track(OnlineProfile profile, Conversation conversation) {
        final UUID playerId = profile.getPlayerUUID();

        // Check if player is already being tracked
        if (isTracked(playerId)) {
            untrack(playerId);
        }

        CONVERSATIONS.put(playerId, new TooltipsConversationData(
                profile,
                conversation
        ));
    }

    public static boolean isTracked(UUID playerId) {
        return CONVERSATIONS.containsKey(playerId);
    }

    public static @Nullable TooltipsConversationData get(UUID playerId) {
        return CONVERSATIONS.get(playerId);
    }

    public static Collection<TooltipsConversationData> getAll() {
        return Collections.unmodifiableCollection(CONVERSATIONS.values());
    }

    public static void untrack(UUID playerId) {
        CONVERSATIONS.remove(playerId);
    }

}
