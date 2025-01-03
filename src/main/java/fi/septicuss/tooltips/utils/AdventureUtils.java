package fi.septicuss.tooltips.utils;

import fi.septicuss.tooltips.Tooltips;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;
import net.kyori.adventure.text.minimessage.tag.standard.StandardTags;
import net.kyori.adventure.text.serializer.gson.GsonComponentSerializer;
import net.kyori.adventure.text.serializer.plain.PlainTextComponentSerializer;
import org.bukkit.command.CommandSender;

import java.util.HashMap;
import java.util.Map;

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

    private static final Map<Character, String> COLOR_CHAR_MAP = new HashMap<>();

    static {
        COLOR_CHAR_MAP.put('0', "<black>");
        COLOR_CHAR_MAP.put('1', "<dark_blue>");
        COLOR_CHAR_MAP.put('2', "<dark_green>");
        COLOR_CHAR_MAP.put('3', "<dark_aqua>");
        COLOR_CHAR_MAP.put('4', "<dark_red>");
        COLOR_CHAR_MAP.put('5', "<dark_purple>");
        COLOR_CHAR_MAP.put('6', "<gold>");
        COLOR_CHAR_MAP.put('7', "<gray>");
        COLOR_CHAR_MAP.put('8', "<dark_gray>");
        COLOR_CHAR_MAP.put('9', "<blue>");
        COLOR_CHAR_MAP.put('a', "<green>");
        COLOR_CHAR_MAP.put('b', "<aqua>");
        COLOR_CHAR_MAP.put('c', "<red>");
        COLOR_CHAR_MAP.put('d', "<light_purple>");
        COLOR_CHAR_MAP.put('e', "<yellow>");
        COLOR_CHAR_MAP.put('f', "<white>");
        COLOR_CHAR_MAP.put('r', "<reset>");
        COLOR_CHAR_MAP.put('l', "<b>");
    }

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

            final char nextChar = chars[i+1];
            if (COLOR_CHAR_MAP.containsKey(nextChar)) {
                builder.append(COLOR_CHAR_MAP.get(nextChar));
                i++;
            }

        }

        return builder.toString();
    }

    public static boolean isLegacyColorCode(char c) {
        return c == '§' || c == '&';
    }


}
