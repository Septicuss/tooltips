package fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation;

import fi.septicuss.tooltips.Tooltips;
import org.betonquest.betonquest.api.PlayerConversationStartEvent;
import org.betonquest.betonquest.api.profiles.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

/**
 *
 */
public class TooltipsConversationIO implements ConversationIO, Listener {

    private static final Map<UUID, TooltipsConversationData> CONVERSATION_DATA = new HashMap<>();
    private static final Set<UUID> CONVERSATIONS = new HashSet<>();

    public static TooltipsConversationData getData(Player player) {
        return CONVERSATION_DATA.get(player.getUniqueId());
    }

    public static boolean isInConversation(Player player) {
        return CONVERSATIONS.contains(player.getUniqueId());
    }

    private final Conversation conversation;
    private final Player player;
    private boolean ending = false;

    public TooltipsConversationIO(Conversation conversation, OnlineProfile onlineProfile) {
        this.conversation = conversation;
        this.player = onlineProfile.getPlayer();

        this.getData();
        this.addActiveConversation();

        Bukkit.getPluginManager().registerEvents(this, Tooltips.get());
    }

    @EventHandler
    public void onConversationStart(PlayerConversationStartEvent event) {
        final UUID uuid = event.getProfile().getPlayerUUID();

        // Player already in a Tooltips conversation
        if (uuid.equals(this.player.getUniqueId())) {
            event.setCancelled(true);
        }

    }


    @Override
    public void setNpcResponse(String npcName, String text) {
        final TooltipsConversationData data = getData();
        data.setNPCName(npcName);
        data.setText(text);
    }

    @Override
    public void addPlayerOption(String option) {
        if (option == null) return;

        final TooltipsConversationData data = getData();
        data.setReady(false);
        data.addOption(option);
    }

    @Override
    public void display() {
        final TooltipsConversationData data = getData();
        data.setReady(true);

        Tooltips.get().getTooltipManager().runActions("beton-ready", player);
    }

    @Override
    public void clear() {
        final TooltipsConversationData data = getData();
        data.clearOptions();
    }

    @Override
    public void end() {
        if (this.ending) {
            return;
        }

        this.ending = true;

        Bukkit.getScheduler().runTaskTimer(Tooltips.get(), (task) -> {

            boolean online = this.player.isOnline();
            boolean end = !online || this.getData().shouldEnd();

            if (end) {
                Bukkit.getScheduler().runTaskLater(Tooltips.get(), () -> {
                    HandlerList.unregisterAll(this);

                    CONVERSATION_DATA.remove(player.getUniqueId());
                    this.removeActiveConversation();
                }, 5L);
                task.cancel();
            }


        }, 0L, 1L);

    }

    public void addActiveConversation() {
        CONVERSATIONS.add(player.getUniqueId());
    }

    public void removeActiveConversation() {
        CONVERSATIONS.remove(player.getUniqueId());
    }

    public TooltipsConversationData getData() {
        return CONVERSATION_DATA.computeIfAbsent(this.player.getUniqueId(), uuid -> new TooltipsConversationData(this.conversation));
    }
}
