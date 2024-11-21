package fi.septicuss.tooltips.managers.tooltip.build.text;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.icon.IconManager;
import fi.septicuss.tooltips.managers.theme.Theme;
import fi.septicuss.tooltips.pack.impl.IconGenerator;
import fi.septicuss.tooltips.pack.impl.LineGenerator;
import net.kyori.adventure.key.Key;

import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TextLine {

    public static final Pattern ICON_PLACEHOLDER_PATTERN = Pattern.compile("\\{([^}]+)\\}");
    public static final Pattern OFFSET_PATTERN = Pattern.compile("\\{([+-])(\\d+)\\}");

    private Theme theme;
    private int lineAscent;
    private int lineIndex;
    private Key regularFont;
    private Key offsetFont;
    private Key iconFont;
    private boolean centered = false;
    private String text = "";

    private IconManager iconManager;

    public TextLine(Theme theme, int lineIndex, IconManager iconManager, String unprocessedText) {
        if (theme == null) {
            Tooltips.warn("Theme cannot be null");
            return;
        }

        this.theme = theme;
        this.iconManager = iconManager;
        this.lineIndex = lineIndex;
        this.lineAscent = theme.getTextStartAscent() - (lineIndex * theme.getTextLineSpacing());
        this.text = unprocessedText;

        if (this.text.startsWith("||") && this.text.endsWith("||")) {
            this.centered = true;
            this.text = unprocessedText.substring(2, unprocessedText.length() - 2);
        }

        final String regularFontFormat = "lines/" + LineGenerator.REGULAR_LINE_FORMAT;
        final String offsetFontFormat = "lines/" + LineGenerator.OFFSET_LINE_FORMAT;
        final String iconFontFormat = "icons/" + IconGenerator.ICON_FONT_FORMAT;

        this.regularFont = Key.key("tooltips",  String.format(regularFontFormat, lineAscent));
        this.offsetFont = Key.key("tooltips", String.format(offsetFontFormat, lineAscent));
        this.iconFont = Key.key("tooltips", String.format(iconFontFormat, lineAscent));

        processText();
    }

    public void processText() {
        if (this.text.contains("{") && this.text.contains("}")) {
            final Set<String> iconPaths = this.iconManager.getIconPaths();

            // Replace icon placeholders {icon} with <icon:icon>
            this.text = replaceIconPlaceholders(this.text, iconPaths);

            // Replace offset placeholders {+x} or {-x} with <offset:x> or <offset:-x>
            this.text = replaceOffsets(this.text);
        }
    }

    private String replaceIconPlaceholders(String text, Set<String> validValues) {
        Matcher matcher = ICON_PLACEHOLDER_PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String insideBraces = matcher.group(1);
            if (validValues.contains(insideBraces)) {
                matcher.appendReplacement(result, "<icon:" + insideBraces + ">");
            }
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private String replaceOffsets(String text) {
        Matcher matcher = OFFSET_PATTERN.matcher(text);
        StringBuilder result = new StringBuilder();

        while (matcher.find()) {
            String sign = matcher.group(1);  // Either + or -
            String number = matcher.group(2);  // The number

            String replacement = sign.equals("+") ? "<offset:" + number + ">" : "<offset:" + sign + number + ">";
            matcher.appendReplacement(result, replacement);
        }
        matcher.appendTail(result);
        return result.toString();
    }


    public Theme getTheme() {
        return theme;
    }

    public int getLineAscent() {
        return lineAscent;
    }

    public int getLineIndex() {
        return lineIndex;
    }

    public Key getRegularFont() {
        return regularFont;
    }

    public Key getOffsetFont() {
        return offsetFont;
    }

    public Key getIconFont() {
        return iconFont;
    }

    public boolean isCentered() {
        return centered;
    }

    public String getText() {
        return text;
    }
}
