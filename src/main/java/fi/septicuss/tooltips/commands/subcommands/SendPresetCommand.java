package fi.septicuss.tooltips.commands.subcommands;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.commands.TooltipsSubCommand;
import fi.septicuss.tooltips.managers.preset.Preset;
import fi.septicuss.tooltips.managers.title.TitleBuilder;
import fi.septicuss.tooltips.managers.tooltip.Tooltip;
import fi.septicuss.tooltips.managers.tooltip.TooltipManager;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.PlayerTooltipData;
import fi.septicuss.tooltips.utils.AdventureUtils;
import fi.septicuss.tooltips.utils.Colors;
import fi.septicuss.tooltips.utils.Text;
import net.citizensnpcs.api.util.Messaging;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SendPresetCommand implements TooltipsSubCommand {

	private Tooltips plugin;

	public SendPresetCommand(Tooltips plugin) {
		this.plugin = plugin;
	}

	@Override
	public void onCommand(CommandSender sender, Command command, String label, String[] args) {

		Player target = null;
		Preset preset = null;
		ArrayList<String> extra = new ArrayList<>();

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

			String text = Text.processText(target, builder.toString().strip());
			extra.addAll(Arrays.asList(text.split("\\\\n")));
		}

		// Sending
		final TooltipManager manager = plugin.getTooltipManager();
		final PlayerTooltipData data = manager.getPlayerTooltipData(target);
		data.setSentPreset(preset.getId());

		final Tooltip tooltip = manager.getTooltip(target, preset, extra);

		TitleBuilder titleBuilder = new TitleBuilder(plugin.getTitleManager());
		titleBuilder.setSubtitle(tooltip.getComponent());
		titleBuilder.setFadeIn(preset.getFadeIn());
		titleBuilder.setStay(preset.getStay());
		titleBuilder.setFadeOut(preset.getFadeOut());

		var optionalTitle = titleBuilder.build();
		if (optionalTitle.isPresent()) {
			optionalTitle.get().send(target);
		}


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
		AdventureUtils.sendMessage(sender, Colors.WARN + "[!] " + message);
	}

}
