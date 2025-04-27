package fi.septicuss.tooltips.managers.integration.impl.betonquest;

import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversationData;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversationIO;
import fi.septicuss.tooltips.utils.AdventureUtils;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class BetonQuestCondition implements Condition {

    @Override
    public boolean check(Player player, Arguments args) {
        return TooltipsConversationIO.isInConversation(player);
    }

    @Override
    public void writeContext(Player player, Arguments args, Context context) {
        if (!TooltipsConversationIO.isInConversation(player)) return;
        final TooltipsConversationData data = TooltipsConversationIO.getData(player);
        if (data == null) return;

        context.put("betonquest.npc", data.getNPCName());
        context.put("betonquest.text", data.getText());
        context.put("betonquest.options", data.getOptions());
        context.put("betonquest.selected", data.getSelectedOption());
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
