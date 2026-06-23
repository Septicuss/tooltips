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

public class TooltipsConversationIO implements ConversationIO {

    private final Tooltips plugin;
    private final Conversation conversation;
    private final OnlineProfile onlineProfile;

    public TooltipsConversationIO(Tooltips plugin, Conversation conversation, OnlineProfile onlineProfile) {
        this.plugin = plugin;
        this.conversation = conversation;
        this.onlineProfile = onlineProfile;
    }

    @Override
    public void begin() {
        TooltipsConversations.track(this.onlineProfile, this.conversation);
    }

    @Override
    public void setNpcResponse(Component npcName, Component response) {
        TooltipsConversationData conversationData = getConversationData();
        if (conversationData == null) return;

        conversationData.setNpcResponse(npcName, response);
    }

    @Override
    public void addPlayerOption(Component option, ConfigurationSection properties) throws QuestException {
        TooltipsConversationData conversationData = getConversationData();
        if (conversationData == null) return;

        conversationData.addPlayerOption(option);
    }

    @Override
    public void display() {
        TooltipsConversationData conversationData = getConversationData();
        if (conversationData == null) return;

        conversationData.display();
    }

    @Override
    public void clear() {
        TooltipsConversationData conversationData = getConversationData();
        if (conversationData == null) return;

        conversationData.reset();
    }

    @Override
    public void end(Runnable callback) {
        TooltipsConversationData data = getConversationData();
        if (data == null) return;
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
