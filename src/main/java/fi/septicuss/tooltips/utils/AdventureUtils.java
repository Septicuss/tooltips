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
        AdventureUtils.sendMessage(sender, MiniMessage.miniMessage().deserialize(AdventureUtils.legacyToMiniMessage(message)));
    }

    public static void sendMessage(CommandSender sender, Component message) {
        Tooltips.get().getAdventure().sender(sender).sendMessage(message);
    }

    // Credit: Github Xiao-MoMi/Custom-Nameplates
    public static String legacyToMiniMessage(String message) {
        final StringBuilder builder = new StringBuilder();
        final char[] chars = message.toCharArray();

        for (int i = 0; i < chars.length; i++) {
            final char c = chars[i];

            if (!isLegacyColorCode(c)) {
                builder.append(c);
                continue;
            }

            if (i + 1 >= chars.length) {
                builder.append(c);
                continue;
            }

            switch (chars[i+1]) {
                case '0' -> builder.append("<black>");
                case '1' -> builder.append("<dark_blue>");
                case '2' -> builder.append("<dark_green>");
                case '3' -> builder.append("<dark_aqua>");
                case '4' -> builder.append("<dark_red>");
                case '5' -> builder.append("<dark_purple>");
                case '6' -> builder.append("<gold>");
                case '7' -> builder.append("<gray>");
                case '8' -> builder.append("<dark_gray>");
                case '9' -> builder.append("<blue>");
                case 'a' -> builder.append("<green>");
                case 'b' -> builder.append("<aqua>");
                case 'c' -> builder.append("<red>");
                case 'd' -> builder.append("<light_purple>");
                case 'e' -> builder.append("<yellow>");
                case 'f' -> builder.append("<white>");
                case 'r' -> builder.append("<reset>");
                case 'l' -> builder.append("<b>");
            }

        }

        return builder.toString();
    }

    public static boolean isLegacyColorCode(char c) {
        return c == '§' || c == '&';
    }


}
