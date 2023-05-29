package fi.septicuss.tooltips.utils;

import org.bukkit.command.CommandSender;

public class Messaging {

	public static void send(CommandSender sender, String message) {
		sender.sendMessage(Utils.color(message));
	}

}
