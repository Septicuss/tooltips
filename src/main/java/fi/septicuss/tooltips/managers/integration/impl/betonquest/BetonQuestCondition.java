package fi.septicuss.tooltips.managers.integration.impl.betonquest;

import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversationData;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversationStage;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversations;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.entity.Player;

public class BetonQuestCondition implements Condition {

    @Override
    public boolean check(Player player, Arguments args) {
        final TooltipsConversationData data = TooltipsConversations.get(player.getUniqueId());
        return data != null && data.isReady();
    }

    @Override
    public void writeContext(Player player, Arguments args, Context context) {
        if (!TooltipsConversations.isTracked(player.getUniqueId())) return;

        TooltipsConversationData conversationData = TooltipsConversations.get(player.getUniqueId());
        if (conversationData == null) return;

        TooltipsConversationStage stage = conversationData.getStage();
        if (stage == null) return;

        context.put("betonquest.npc", stage.npcName());
        context.put("betonquest.text", stage.response());
        context.put("betonquest.options", stage.options());
        context.put("betonquest.selected", conversationData.getSelectedOption());

    }

    @Override
    public Validity valid(Arguments args) {
        return Validity.TRUE;
    }

    @Override
    public String id() {
        return "betonquest";
    }
}
