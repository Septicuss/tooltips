package fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation;

import fi.septicuss.tooltips.Tooltips;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.api.quest.QuestException;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.scheduler.BukkitScheduler;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

public class TooltipsConversationIO implements ConversationIO, Listener {

    private static final Map<UUID, TooltipsConversationData> CONVERSATION_DATA = new HashMap<>();
    private static final Set<UUID> CONVERSATIONS = new HashSet<>();

    private static boolean STOP = false;
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

    public static TooltipsConversationData getData(Player player) {
        return CONVERSATION_DATA.get(player.getUniqueId());
    }

    public static boolean isInConversation(Player player) {
        return CONVERSATIONS.contains(player.getUniqueId());
    }

    public static void endConversations() {
        STOP = true;
        CONVERSATIONS.clear();
    }

    @EventHandler
    public void onConversationStart(org.betonquest.betonquest.api.bukkit.event.PlayerConversationStartEvent event) {
        final UUID uuid = event.getProfile().getPlayerUUID();

        // Player already in a Tooltips conversation
        if (uuid.equals(this.player.getUniqueId())) {
            event.setCancelled(true);
        }

    }

    @Override
    public void begin() {

    }

    @Override
    public void setNpcResponse(Component npcName, Component response) {
        final TooltipsConversationData data = getData();
        data.setNPCName(npcName);
        data.setText(response);
    }

    @Override
    public void addPlayerOption(Component option, ConfigurationSection configurationSection) throws QuestException {
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
    public void end(Runnable runnable) {
        // Already in the process of ending
        if (this.ending) {
            return;
        }

        // Set as ending
        this.ending = true;

        BukkitScheduler scheduler = Bukkit.getScheduler();

        // Wait until actual end signal is sent
        scheduler.runTaskTimer(Tooltips.get(), (task) -> {

            // Used to stop tasks after plugin stops
            if (STOP) {
                HandlerList.unregisterAll(this);
                task.cancel();
                runnable.run();
                return;
            }

            final boolean online = player.isOnline();
            final boolean end = (STOP || !online || getData().shouldEnd());

            // Should not end yet
            if (!end) {
                return;
            }

            // Schedule conversation to end
            scheduler.runTaskLater(Tooltips.get(), () -> {
                HandlerList.unregisterAll(this);
                CONVERSATION_DATA.remove(player.getUniqueId());
                this.removeActiveConversation();
                runnable.run();
            }, 5L);

            // Cancel this task
            task.cancel();

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
