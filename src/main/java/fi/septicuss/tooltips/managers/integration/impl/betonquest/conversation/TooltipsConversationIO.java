package fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.preset.Preset;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.PlayerTooltipData;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.QuestException;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.betonquest.betonquest.conversation.ConversationIO;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.util.concurrent.atomic.AtomicInteger;

public class TooltipsConversationIO implements ConversationIO {

    private static final int DEFAULT_FADEOUT_TICKS = 65;

    private final Tooltips plugin;
    private final Conversation conversation;
    private final OnlineProfile onlineProfile;
    private final TooltipsConversationData data;

    public TooltipsConversationIO(Tooltips plugin, Conversation conversation, OnlineProfile onlineProfile) {
        this.plugin = plugin;
        this.conversation = conversation;
        this.onlineProfile = onlineProfile;
        this.data = new TooltipsConversationData(
                onlineProfile,
                conversation
        );
    }

    @Override
    public void begin() {

        final AtomicInteger attempts = new AtomicInteger(0);
        final int maxAttempts = 5;

        // Attempt to start tracking the conversation with an attempt limit
        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, bukkitTask -> {
            int currentAttempt = attempts.incrementAndGet();
            if (currentAttempt > maxAttempts) {
                bukkitTask.cancel();
                this.conversation.endConversation();
                return;
            }

            // Attempt to track and cancel if successful
            if (TooltipsConversations.track(this.onlineProfile, this.data)) {
                bukkitTask.cancel();
            }

        }, 0L, DEFAULT_FADEOUT_TICKS / maxAttempts);

    }

    @Override
    public void setNpcResponse(Component npcName, Component response) {
        data.setNpcResponse(npcName, response);
    }

    @Override
    public void addPlayerOption(Component option, ConfigurationSection properties) throws QuestException {
        data.addPlayerOption(option);
    }

    @Override
    public void display() {
        data.display();
    }

    @Override
    public void clear() {
        data.reset();
    }

    @Override
    public void end(Runnable callback) {
        if (data.isEnding()) return;

        data.setEnding();
        callback.run();

        int fadeoutTicks = 65;

        // Extract the stay time from preset
        Integer extractedStay = extractPresetStay();
        if (extractedStay != null && extractedStay != 0)
            fadeoutTicks = extractedStay;

        // Stopping after fadeout so that player can read last slide
        Bukkit.getScheduler().runTaskLater(plugin, () -> {
            TooltipsConversations.untrack(onlineProfile.getPlayerUUID());
        }, fadeoutTicks);
    }

    private Integer extractPresetStay() {
        PlayerTooltipData data = plugin.getTooltipManager().getPlayerTooltipData(onlineProfile.getPlayer());
        if (data == null) {
            return null;
        }

        String checkedPresetId = data.getCheckedPreset();
        if (checkedPresetId == null) {
            return null;
        }
        Preset preset = plugin.getPresetManager().getPreset(checkedPresetId);
        if (preset == null) {
            return null;
        }

        return preset.getStay();
    }

    private @Nullable TooltipsConversationData getConversationData() {
        if (!TooltipsConversations.isTracked(onlineProfile.getPlayerUUID())) {
            conversation.endConversation();
            return null;
        }

        return TooltipsConversations.get(onlineProfile.getPlayerUUID());
    }

}
