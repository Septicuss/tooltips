package fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation;

import org.betonquest.betonquest.conversation.Conversation;

import java.util.ArrayList;
import java.util.List;

public class TooltipsConversationData {

    private final Conversation conversation;

    // Conversation data
    private String npcName;
    private String text;
    private final List<String> options = new ArrayList<>();
    private int selectedOption;

    // Conversation state
    /** Whether all necessary data has been received from BQ. */
    private boolean ready;
    /** Controls whether to end this conversation */
    private boolean end;

    public TooltipsConversationData(Conversation conversation) {
        this.conversation = conversation;
    }

    public Conversation getConversation() {
        return conversation;
    }

    public String getNPCName() {
        return npcName;
    }

    public void setNPCName(String npcName) {
        this.npcName = npcName;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }

    public void end() {
        this.end = true;
    }

    public boolean shouldEnd() {
        return this.end;
    }

    public List<String> getOptions() {
        return options;
    }

    public void addOption(String option) {
        this.options.add(option);
    }

    public void clearOptions() {
        this.options.clear();
        this.selectedOption = 0;
    }

    public void nextOption() {
        int size = this.options.size();
        if (size > 0) {
            this.selectedOption = (this.selectedOption + 1) % size;
        } else {
            this.selectedOption = 0;
        }
    }

    public int getSelectedOption() {
        return this.selectedOption;
    }

    public boolean isLast() {
        return this.conversation.isEnded();
    }

    public void reset() {
        this.npcName = null;
        this.text = null;
        this.options.clear();
    }
}
