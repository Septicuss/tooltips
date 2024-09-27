package fi.septicuss.tooltips.commands.subcommands;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.google.common.collect.Lists;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.commands.TooltipsSubCommand;
import fi.septicuss.tooltips.integrations.Title;
import fi.septicuss.tooltips.managers.preset.Preset;
import fi.septicuss.tooltips.managers.title.TitleBuilder;
import fi.septicuss.tooltips.managers.tooltip.Tooltip;
import fi.septicuss.tooltips.utils.Colors;
import fi.septicuss.tooltips.utils.Messaging;
import fi.septicuss.tooltips.utils.placeholder.Placeholders;

public class SendPresetCommand implements TooltipsSubCommand {

	private Tooltips plugin;

	public SendPresetCommand(Tooltips plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onCommand(CommandSender sender, Command command, String label, String[] args) {

		Player target = null;
		Preset preset = null;
		List<String> extra = Lists.newArrayList();

		if (args.length < 2) {
			warn(sender, "Missing target");
			return;
		}

		target = Bukkit.getPlayerExact(args[1]);

		if (target == null) {
			warn(sender, "Unknown target");
			return;
		}

		if (args.length < 3) {
			warn(sender, "Missing preset id");
			return;
		}

		if (!plugin.getPresetManager().doesPresetExist(args[2])) {
			warn(sender, "Preset does not exist");
			return;
		}

		preset = plugin.getPresetManager().getPreset(args[2]);

		if (args.length > 3) {
			StringBuilder builder = new StringBuilder();

			for (int i = 3; i < args.length; i++) {
				builder.append(args[i] + " ");
			}
			
			String text = Placeholders.replacePlaceholders(target, builder.toString().trim());
			extra.addAll(Arrays.asList(text.split("\\\\n")));
		}

		// Sending

		Tooltip tooltip = plugin.getTooltipManager().getTooltip(target, preset, extra);

		TitleBuilder titleBuilder = new TitleBuilder(plugin.getTitleManager());
		titleBuilder.setSubtitle(tooltip.getComponents());
		titleBuilder.setFadeIn(preset.getFadeIn());
		titleBuilder.setStay(preset.getStay());
		titleBuilder.setFadeOut(preset.getFadeOut());

		Title title = titleBuilder.build();
		title.send(target);

	}

	@Override
	public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] relativeArgs) {
		switch (relativeArgs.length) {

		case 0:
			return null;
		case 1:
			return null;
		case 2:
			return new ArrayList<>(plugin.getPresetManager().getPresets().keySet());

		}

		return null;
	}

	@Override
	public String getPermission() {
		return "tooltips.command.sendpreset";
	}

	private void warn(CommandSender sender, String message) {
		Messaging.send(sender, Colors.WARN + "[!] " + message);
	}

}
