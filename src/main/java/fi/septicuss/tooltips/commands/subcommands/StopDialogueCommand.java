package fi.septicuss.tooltips.commands.subcommands;

import fi.septicuss.tooltips.commands.TooltipsSubCommand;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversationData;
import fi.septicuss.tooltips.managers.integration.impl.betonquest.conversation.TooltipsConversationIO;
import fi.septicuss.tooltips.utils.AdventureUtils;
import fi.septicuss.tooltips.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;

public class StopDialogueCommand implements TooltipsSubCommand {

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 2) {
            AdventureUtils.sendMessage(sender, Colors.WARN + "[!] Missing player to stop dialogue for.");
            return;
        }

        String targetName = args[1];
        Player targetPlayer = Bukkit.getPlayerExact(targetName);

        if (targetPlayer == null || !targetPlayer.isOnline()) {
            AdventureUtils.sendMessage(sender, Colors.WARN + "[!] Target player is offline.");
            return;
        }

        if (!TooltipsConversationIO.isInConversation(targetPlayer)) {
            AdventureUtils.sendMessage(sender, Colors.WARN + "[!] Player " + targetPlayer.getName() + " is not in a dialogue.");
            return;
        }

        TooltipsConversationData data = TooltipsConversationIO.getData(targetPlayer);
        data.getConversation().endConversation();
        data.end();

        AdventureUtils.sendMessage(sender, Colors.PLUGIN + "Stopping dialogue for player " + targetPlayer.getName() + ".");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] relativeArgs) {
        if (relativeArgs.length == 1) {
            return Bukkit.getOnlinePlayers()
                    .stream()
                    .map(Player::getName)
                    .filter(name -> name.startsWith(relativeArgs[0]))
                    .toList();
        }

        return List.of();
    }

    @Override
    public String getPermission() {
        return "tooltips.command.stopdialogue";
    }

}
