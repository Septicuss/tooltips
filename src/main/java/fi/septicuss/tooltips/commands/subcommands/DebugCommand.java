package fi.septicuss.tooltips.commands.subcommands;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.commands.TooltipsSubCommand;
import fi.septicuss.tooltips.managers.condition.Statement;
import fi.septicuss.tooltips.managers.condition.StatementHolder;
import fi.septicuss.tooltips.managers.preset.Preset;
import fi.septicuss.tooltips.utils.AdventureUtils;
import fi.septicuss.tooltips.utils.Colors;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

// /tt debug [preset] [player]
public class DebugCommand implements TooltipsSubCommand {

    private Tooltips plugin;

    public DebugCommand(Tooltips plugin) { this.plugin = plugin; }

    @Override
    public void onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length < 2) {
            AdventureUtils.sendMessage(sender, Colors.WARN + "[!] Missing preset ID");
            return;
        }

        String presetId = args[1];
        Preset preset = plugin.getPresetManager().getPreset(presetId);

        Player target = null;

        if (args.length >= 3) {
            final String targetName = args[2];
            target = Bukkit.getPlayer(targetName);
        }

        if (target == null) {
            if (!(sender instanceof Player player)) {
                AdventureUtils.sendMessage(sender, Colors.WARN + "[!] Invalid or missing target player");
                return;
            }

            target = player;
        }

        debugPreset(sender, target, preset);

    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] relativeArgs) {
        return switch (relativeArgs.length) {
            case 1 -> new ArrayList<>(plugin.getPresetManager().getPresets().keySet());
            case 2 -> null;
            default -> List.of();
        };
    }

    @Override
    public String getPermission() {
        return "tooltips.command.debug";
    }

    public void debugPreset(CommandSender sender, Player player, Preset preset) {
        ConfigurationSection section = preset.getSection();

        List<String> conditionLines = null;

        if (section.contains("conditions.conditions"))
            conditionLines = section.getStringList("conditions.conditions");
        if (section.contains("conditions") && !section.isConfigurationSection("conditions"))
            conditionLines = section.getStringList("conditions");;

        if (conditionLines == null || conditionLines.isEmpty()) {
            AdventureUtils.sendMessage(sender, Colors.WARN + "[!] This preset does not have any conditions");
            return;
        }

        StatementHolder holder = new StatementHolder();

        for (String line : conditionLines) {
            Statement statement = plugin.getConditionManager().getStatementParser().parse(preset.getId(), line);
            holder.addStatement(statement);
        }

        AdventureUtils.sendMessage(sender, " ");
        AdventureUtils.sendMessage(sender, Colors.PLUGIN + "Debugging preset " + Colors.INFO + preset.getId() + Colors.PLUGIN + " for player " + Colors.INFO + player.getName());
        AdventureUtils.sendMessage(sender, " ");

        int index = -1;

        for (var statement : holder.getStatements()) {
            index += 1;
            int readableIndex = index + 1;
            if (statement == null || statement.getCompositeCondition() == null || statement.getOutcome() == Statement.Outcome.SKIP) {
                AdventureUtils.sendMessage(sender, Colors.PLUGIN + readableIndex + " (Skipped)" + Colors.INFO + " " + conditionLines.get(index));
                continue;
            }

            boolean conditionResult = statement.getCompositeCondition().check(player);

            AdventureUtils.sendMessage(sender, Colors.PLUGIN + readableIndex + Colors.INFO + " " + conditionLines.get(index));
            AdventureUtils.sendMessage(sender, Colors.INFO + "   -> " + (conditionResult ? ChatColor.GREEN + "true" : ChatColor.RED + "false"));

            if (statement.hasOutcome()) {
                boolean outcome = statement.getOutcome().asBoolean();

                if (outcome) {
                    // REQUIRED
                    if (!conditionResult) {
                        AdventureUtils.sendMessage(sender, ChatColor.RED + "> Stopping at " + readableIndex + Colors.INFO + ", because outcome is REQUIRED and condition was " + ChatColor.RED + "false.");
                        return;
                    }
                } else {
                    // CANCEL
                    if (conditionResult) {
                        AdventureUtils.sendMessage(sender, ChatColor.RED + "> Stopping at " + readableIndex + Colors.INFO + ", because outcome is CANCEL and result was " + ChatColor.GREEN + "true.");
                        return;
                    }
                    continue;
                }

            }

            if (!conditionResult) {
                AdventureUtils.sendMessage(sender, ChatColor.RED + "> Stopping at " + readableIndex + Colors.INFO + ", because condition was " + ChatColor.RED + "false.");
                return;
            }

        }

        AdventureUtils.sendMessage(sender, Colors.INFO + "> A tooltip " + ChatColor.GREEN + "would show!");

    }
}
