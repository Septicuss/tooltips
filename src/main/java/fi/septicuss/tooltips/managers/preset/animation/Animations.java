package fi.septicuss.tooltips.managers.preset.animation;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.preset.functions.Functions;
import fi.septicuss.tooltips.managers.tooltip.tasks.data.PlayerTooltipData;
import fi.septicuss.tooltips.utils.Utils;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Animations {

    private static final HashMap<String, AnimationProvider> ANIMATION_PROVIDERS = new HashMap<>();
    private static final ConcurrentHashMap<UUID, ParsedAnimation> ANIMATIONS = new ConcurrentHashMap<>();

    public static void addProvider(String name, AnimationProvider animation) {
        ANIMATION_PROVIDERS.put(name, animation);
    }

    public static void removeProvider(String name) {
        ANIMATION_PROVIDERS.remove(name);
    }

    public static boolean doesProviderExist(String name) {
        return ANIMATION_PROVIDERS.containsKey(name);
    }

    public static AnimationProvider getProvider(String name) {
        return ANIMATION_PROVIDERS.get(name);
    }

    public static ParsedAnimation get(UUID uuid) {
        return ANIMATIONS.get(uuid);
    }

    public static UUID newAnimation(String name, String text, Arguments arguments) {
        if (!doesProviderExist(name)) {
            return null;
        }
        final UUID uuid = UUID.randomUUID();
        final Animation animation = ANIMATION_PROVIDERS.get(name).create(text, arguments);
        final ParsedAnimation parsedAnimation = new ParsedAnimation(animation, text, arguments);

        ANIMATIONS.put(uuid, parsedAnimation);

        return uuid;
    }

    public static void stopAnimation(UUID uuid) {
        ANIMATIONS.remove(uuid);
    }

    public static List<String> parse(Player player, List<String> text) {
        final List<String> result = new ArrayList<>();
        for (String line : text) result.add(parse(player, line));
        return result;
    }

    public static String stripAnimations(String text) {
        final StringBuilder builder = new StringBuilder();

        if (text.indexOf('<') == -1 || text.indexOf('>') == -1) {
            return text;
        }

        for (int index = 0; index < text.length(); index++) {

            // <...>
            // ^
            final int openingSign = text.indexOf('<', index);
            if (openingSign == -1) {
                builder.append(text.substring(index));
                break;
            }

            // <...>
            //     ^
            int closingSign = -1;
            int level = 1;

            // j = <...
            //     ^
            for (int j = openingSign + 1; j < text.length(); j++) {
                if (text.charAt(j) == '<') {
                    level++;
                } else if (text.charAt(j) == '>') {
                    level--;

                    if (level == 0) {
                        // <...>
                        //     ^
                        closingSign = j;
                        break;
                    }
                }
            }

            // > Not found
            if (closingSign == -1) {
                builder.append(text, index, openingSign + 1);
                index = openingSign;
                continue;
            }

            builder.append(text, index, openingSign);
            index = openingSign;

            // <...>
            //  ^^^
            final String content = text.substring(openingSign + 1, closingSign);
            final List<String> tokens = tokenizeTagContents(content);

            // <meow ...>
            //  ^^^^
            final String name = tokens.get(0);

            if (!doesProviderExist(name)) {
                builder.append(text, index, closingSign + 1);
                index = closingSign;
                continue;
            }

            final Arguments arguments = new Arguments();

            for (int i = 1; i < tokens.size(); i++) {
                String token = tokens.get(i);

                if (token.contains("=")) {
                    String[] parts = token.split("=", 2);
                    String key = parts[0].strip();
                    String value = parts[1].strip();

                    if (Utils.isSurroundedByQuotes(value)) {
                        value = Utils.removeQuotes(value);
                    }

                    arguments.add(key, new Argument(value));
                } else {
                    arguments.add(token.strip(), new Argument("true"));
                }
            }

            if (!arguments.has("text", "t")) {
                builder.append(text, index, closingSign + 1);
                index = closingSign;
                continue;
            }

            builder.append(arguments.get("text", "t").getAsString());
            index = closingSign;
        }

        return builder.toString();
    }

    public static String parse(Player player, String text) {

        final PlayerTooltipData data = Tooltips.get().getTooltipManager().getPlayerTooltipData(player);
        final StringBuilder builder = new StringBuilder();

        if (text.indexOf('<') == -1 || text.indexOf('>') == -1) {
            return text;
        }

        for (int index = 0; index < text.length(); index++) {

            // <...>
            // ^
            final int openingSign = text.indexOf('<', index);
            if (openingSign == -1) {
                builder.append(text.substring(index));
                break;
            }

            // <...>
            //     ^
            int closingSign = -1;
            int level = 1;

            // j = <...
            //     ^
            for (int j = openingSign + 1; j < text.length(); j++) {
                if (text.charAt(j) == '<') {
                    level++;
                } else if (text.charAt(j) == '>') {
                    level--;

                    if (level == 0) {
                        // <...>
                        //     ^
                        closingSign = j;
                        break;
                    }
                }
            }


            // > Not found
            if (closingSign == -1) {
                builder.append(text, index, openingSign + 1);
                index = openingSign;
                continue;
            }

            builder.append(text, index, openingSign);
            index = openingSign;

            // <...>
            //  ^^^
            final String content = text.substring(openingSign + 1, closingSign);
            final List<String> tokens = tokenizeTagContents(content);


            // <meow ...>
            //  ^^^^
            final String name = tokens.get(0);

            if (!doesProviderExist(name)) {
                builder.append(text, index, closingSign + 1);
                index = closingSign;
                continue;
            }

            final Arguments arguments = new Arguments();

            for (int i = 1; i < tokens.size(); i++) {
                String token = tokens.get(i);

                if (token.contains("=")) {
                    String[] parts = token.split("=", 2);
                    String key = parts[0].strip();
                    String value = parts[1].strip();

                    if (Utils.isSurroundedByQuotes(value)) {
                        value = Utils.removeQuotes(value);
                    }

                    arguments.add(key, new Argument(Functions.parse(player, data.getCurrentPreset(), value)));
                } else {
                    arguments.add(token.strip(), new Argument("true"));
                }
            }

            final String textArgument = arguments.has("text", "t") ?  arguments.get("text", "t").getAsString() : null;
            final UUID animationId = newAnimation(name, textArgument, arguments);
            data.addAnimation(animationId);

            builder.append("$tta(");
            builder.append(animationId);
            builder.append(")");

            index = closingSign;
        }


        return builder.toString();
    }

    private static List<String> tokenizeTagContents(String content) {
        final List<String> tokens = new ArrayList<>();
        final StringBuilder currentToken = new StringBuilder();
        boolean insideQuotes = false;

        for (int i = 0; i < content.length(); i++) {
            char c = content.charAt(i);

            if (c == '"') {
                insideQuotes = !insideQuotes;
                currentToken.append(c);
            } else if (c == ' ' && !insideQuotes) {
                if (!currentToken.isEmpty()) {
                    tokens.add(currentToken.toString().strip());
                    currentToken.setLength(0);
                }
            } else {
                currentToken.append(c);
            }
        }

        // Add the last token if it exists
        if (!currentToken.isEmpty()) {
            tokens.add(currentToken.toString().strip());
        }

        return tokens;
    }

}
