package fi.septicuss.tooltips.utils;

import fi.septicuss.tooltips.Tooltips;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;

public class AdventureUtils {

    public static PlainTextComponentSerializer PLAINTEXT = PlainTextComponentSerializer.plainText();
    public static MiniMessage MINIMESSAGE = MiniMessage.builder()
            .tags(TagResolver.builder()
                    .resolver(StandardTags.color())
                    .resolver(StandardTags.reset())
                    .resolver(StandardTags.rainbow())
                    .resolver(StandardTags.gradient())
                    .resolver(StandardTags.transition())
                    .build()
            ).build();
    public static GsonComponentSerializer GSONSERIALIZER = GsonComponentSerializer.gson();

    public static void sendMessage(CommandSender sender, String message) {
        AdventureUtils.sendMessage(sender, MiniMessage.miniMessage().deserialize(AdventureUtils.convertLegacyString(message)));
    }

    public static void sendMessage(CommandSender sender, Component message) {
        Tooltips.get().getAdventure().sender(sender).sendMessage(message);
    }

    private static String convertLegacyString(String message) {
        return message
                .replace("§a", "<green>")
                .replace("§c", "<red>")
                .replace("§f", "<white>")
                .replace("&f", "<white>")
                .replace("§l", "<b>");
    }


}
