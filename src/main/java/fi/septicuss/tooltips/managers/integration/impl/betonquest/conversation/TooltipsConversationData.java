package fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation;

import fi.septicuss.tooltips.utils.AdventureUtils;
import net.kyori.adventure.text.Component;
import org.betonquest.betonquest.api.profile.OnlineProfile;
import org.betonquest.betonquest.conversation.Conversation;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

public class TooltipsConversationData {

    private final OnlineProfile onlineProfile;
    private final Conversation conversation;

    private TooltipsConversationStage stage;
    private String npcName;
    private String npcResponse;
    private final List<String> options = new ArrayList<>();

    private boolean ready = false;
    private boolean ending = false;
    private int selectedOption = 0;

    public TooltipsConversationData(OnlineProfile onlineProfile, Conversation conversation) {
        this.onlineProfile = onlineProfile;
        this.conversation = conversation;
    }

    public void reset() {
        this.ready = false;
        this.npcName = null;
        this.npcResponse = null;
        this.options.clear();
    }

    public void display() {
        this.stage = new TooltipsConversationStage(
                this.npcName,
                this.npcResponse,
                this.options
        );
        this.ready = true;
    }

    public void nextOption() {
        int size = this.options.size();
        if (size > 0) {
            this.selectedOption = (this.selectedOption + 1) % size;
        } else {
            this.selectedOption = 0;
        }
    }

    public void passPlayerAnswer(int answerIndex) {
        conversation.passPlayerAnswer(answerIndex);
    }

    public void setNpcResponse(Component npcName, Component npcResponse) {
        this.npcName = AdventureUtils.MINIMESSAGE.serialize(npcName).replace("\\", "");
        this.npcResponse = AdventureUtils.MINIMESSAGE.serialize(npcResponse).replace("\\", "");
    }

    public void addPlayerOption(Component option) {
        this.options.add(AdventureUtils.MINIMESSAGE.serialize(option).replace("\\", ""));
    }

    public @Nullable TooltipsConversationStage getStage() {
        return stage;
    }

    public boolean isReady() {
        return ready;
    }

    public boolean isEnding() {
        return ending;
    }

    public void setEnding() {
        this.ending = true;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public OnlineProfile getOnlineProfile() {
        return onlineProfile;
    }

    public int getSelectedOption() {
        return selectedOption;
    }
}
