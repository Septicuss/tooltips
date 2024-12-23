package fi.septicuss.tooltips.managers.preset.functions;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.condition.Context;
import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.utils.Utils;
import org.bukkit.entity.Player;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Class used for adding and parsing textual functions.
 * Functions are in format {@code $function(args)} and when parsed, return a string response.
 */
public class Functions {

    private static final Map<String, Function> FUNCTIONS = new ConcurrentHashMap<>();

    public static Function get(String name) {
        return FUNCTIONS.get(name);
    }

    public static void add(String name, Function function) {
        FUNCTIONS.put(name.toLowerCase(), function);
    }

    public static void remove(String name) {
        FUNCTIONS.remove(name.toLowerCase());
    }

    public static String parseSingleLineWithContext(Player player, String preset, String text, Context context) {
        return parse(player, preset, text, null, context);
    }

    public static String parse(Player player, String preset, String text) {
        return parse(player, preset, text, null, null);
    }

    public static List<String> parse(Player player, String preset, List<String> text) {
        return parse(player, preset,  text, null);
    }

    /**
     * Process the given text for any functions ($function()) and replace them with the output of function.
     *
     * @param preset The preset for which this function is being parsed
     * @param text Text to parse functions from
     * @return Result string list, with all valid functions parsed and executed
     */
    public static @Nonnull List<String> parse(Player player, String preset, List<String> text, List<String> whitelist) {
        if (preset == null) return text;
        final List<String> result = new ArrayList<>();

        for (String line : text) {
            result.add(parse(player, preset, line, whitelist, null));
        }

        return result;
    }

    /**
     * Process the given text for any functions ($function()) and replace them with the output of function.
     *
     * @param preset The preset for which this function is being parsed
     * @param text Text to parse functions from
     * @return Result string, with all valid functions parsed and executed
     */
    public static @Nonnull String parse(Player player, @Nonnull String preset, @Nonnull String text, List<String> whitelist, Context context) {
        Objects.requireNonNull(preset);
        final StringBuilder builder = new StringBuilder();

        if (text.indexOf('$') == -1) {
            return text;
        }

        for (int index = 0; index < text.length(); index++) {

            // $meow()
            // ^
            final int functionSign = text.indexOf('$', index);
            if (functionSign == -1) {
                builder.append(text.substring(index));
                break;
            }

            builder.append(text, index, functionSign);
            index = functionSign;

            // $meow()
            //      ^
            final int openingBracket = text.indexOf('(', functionSign);
            if (openingBracket == -1 || text.indexOf(')') == -1) {
                builder.append(text.substring(index));
                break;
            }

            // $meow()
            //  ^^^^
            final String name = text.substring(functionSign + 1, openingBracket);

            // Do not allow spaces like $meow ()
            //                               ^
            if (name.contains(" ")) {
                builder.append(text, index, openingBracket + 1);
                index = openingBracket;
                continue;
            }

            int closingBracket = -1;
            int level = 1;

            // i = $meow(...
            //           ^
            for (int j = openingBracket + 1; j < text.length(); j++) {
                if (text.charAt(j) == '(') {
                    level++;
                } else if (text.charAt(j) == ')') {
                    level--;

                    if (level == 0) {
                        // $meow(...)
                        //          ^
                        closingBracket = j;
                        break;
                    }
                }
            }

            // ) Not found
            if (closingBracket == -1) {
                builder.append(text, index, openingBracket + 1);
                index = openingBracket;
                continue;
            }

            if (whitelist != null && !whitelist.isEmpty()) {
                if (!whitelist.contains(name.toLowerCase())) {
                    builder.append(text, index, openingBracket + 1);
                    index = openingBracket;
                    continue;
                }

            }

            final Function function = FUNCTIONS.get(name);

            if (function == null) {
                builder.append(text, index, closingBracket + 1);
                index = closingBracket;
                continue;
            }

            final String parameterString = text.substring(openingBracket + 1, closingBracket);

            final String[] argumentStrings = Utils.splitStringQuotations(parameterString, ',');
            final List<Argument> arguments = new ArrayList<>();

            for (String argumentString : argumentStrings) {
                argumentString = argumentString.strip();

                if (function.isAcceptRawInput()) {
                    arguments.add(new Argument(argumentString));
                    continue;
                }

                if (Utils.isSurroundedByQuotes(argumentString)) {
                    final int length = argumentString.length();
                    argumentString = argumentString.substring(1, length - 1);
                }

                arguments.add(new Argument(argumentString.strip()));
            }

            if (context == null) {
                context = Tooltips.getPlayerTooltipData(player).getActiveContext();
            }

            builder.append(function.handle(player, new FunctionContext(preset, context), arguments));
            index = closingBracket;

        }

        return builder.toString();
    }

}
