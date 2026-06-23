package fi.septicuss.tooltips.managers.integration.impl.betonquest;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.actions.EndConversationCommand;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.actions.NextOptionCommand;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.actions.SelectOptionCommand;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversationIOFactory;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversations;
import fi.septicuss.tooltips.managers.preset.actions.command.ActionCommands;
import org.betonquest.betonquest.BetonQuest;
import org.betonquest.betonquest.api.quest.FeatureRegistry;
import org.betonquest.betonquest.conversation.ConversationIOFactory;
import org.betonquest.betonquest.kernel.registry.feature.ConversationIORegistry;
import org.bukkit.Bukkit;
import org.bukkit.plugin.Plugin;

public class BetonQuestIntegration {
    
    private static boolean INITIALIZED = false;

    public static void initialize(Tooltips plugin) {
        Plugin betonQuestPlugin = Bukkit.getPluginManager().getPlugin("BetonQuest");

        if (betonQuestPlugin == null || !betonQuestPlugin.isEnabled())
            return;

        // Register tooltips conversation factory
        if (betonQuestPlugin instanceof BetonQuest betonQuest) {
            final FeatureRegistry<ConversationIOFactory> conversationIORegistry = betonQuest.getComponentLoader().get(ConversationIORegistry.class);
            final TooltipsConversationIOFactory tooltipsConversationFactory = new TooltipsConversationIOFactory(plugin);
            conversationIORegistry.register("tooltips", tooltipsConversationFactory);
        } else {
            Tooltips.warn("Failed to register BetonQuest integration, because the conversationIO could not be registered");
            return;
        }

        plugin.getConditionManager().register(new BetonQuestCondition());
        ActionCommands.addCommand("selectoption", new SelectOptionCommand());
        ActionCommands.addCommand("endconversation", new EndConversationCommand());
        ActionCommands.addCommand("nextoption", new NextOptionCommand());

        INITIALIZED = true;

    }
    
    public static void disable() {
        if (!INITIALIZED) {
            return;
        }

        // End all conversations
        TooltipsConversations.getAll().forEach(data -> data.getConversation().endConversation());
    }

}
