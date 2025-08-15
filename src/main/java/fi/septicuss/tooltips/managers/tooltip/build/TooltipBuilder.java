package fi.septicuss.tooltips.managers.tooltip.build;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.icon.IconManager;
import fi.septicuss.tooltips.managers.preset.Preset;
import fi.septicuss.tooltips.managers.theme.Theme;
import fi.septicuss.tooltips.managers.tooltip.Tooltip;
import fi.septicuss.tooltips.managers.tooltip.build.element.BackgroundElement;
import fi.septicuss.tooltips.managers.tooltip.build.element.TextLineElement;
import fi.septicuss.tooltips.managers.tooltip.build.text.TextLine;
import fi.septicuss.tooltips.utils.font.Spaces;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.ShadowColor;
import net.kyori.adventure.text.format.Style;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.serializer.json.JSONComponentSerializer;
import net.kyori.adventure.util.ARGBLike;

import java.util.ArrayList;
import java.util.List;

public class TooltipBuilder {

    private final IconManager iconManager;

    public TooltipBuilder(IconManager iconManager) {
        this.iconManager = iconManager;
    }

    public Tooltip build(TooltipProperties tooltipProperties, List<String> unprocessedText) {
        final List<TextLineElement> textLineElements = new ArrayList<>();
        final List<TextLine> textLines = new ArrayList<>();

        int longestWidth = 0;
        int lastLineWidth = 0;

        // --- PRE-LOAD ---
        for (int i = 0; i < unprocessedText.size(); i++) {
            final String text = unprocessedText.get(i);

            var textLine = new TextLine(tooltipProperties.getTheme(), i, this.iconManager, text);
            var textLineElement = new TextLineElement(this.iconManager, textLine);

            // Generate
            textLineElement.getComponent();

            // Cache widths
            boolean lastLine = (i == unprocessedText.size() - 1);
            int elementWidth = textLineElement.getWidth();

            if (elementWidth > longestWidth)
                longestWidth = elementWidth;

            if (lastLine) {
                lastLineWidth = elementWidth;
            }

            textLineElements.add(textLineElement);
            textLines.add(textLine);
        }

        final TextComponent.Builder builder = Component.text();

        // --- BACKGROUND ---
        int leftPadding = tooltipProperties.getTheme().getLeftPadding();
        int rightPadding = tooltipProperties.getTheme().getRightPadding();

        var background = new BackgroundElement(
                tooltipProperties.getTheme(),
                tooltipProperties.getColor(),
                longestWidth + rightPadding);

        if (tooltipProperties.getHorizontalShift() > 0) {
            builder.append(Spaces.getOffset(tooltipProperties.getHorizontalShift()));
        }

        builder.append(Spaces.getOffset(-1));
        builder.append(background.getComponent());

        int backgroundOffset = -background.getWidth();
        builder.append(Spaces.getOffset(backgroundOffset + leftPadding));

        // --- TEXT ---

        for (int i = 0; i < textLines.size(); i++) {
            final TextLineElement element = textLineElements.get(i);
            final TextLine line = textLines.get(i);

            final boolean lastLine = (i == textLineElements.size() - 1);
            final Component component = element.getComponent();

            // Empty line
            if (component == null)
                builder.append(Spaces.getOffset(1));

            boolean centered = false;

            if (line.isCentered()) {
                if (element.getWidth() != longestWidth) {
                    centered = true;
                    builder.append(Spaces.getOffset(((longestWidth - element.getWidth()) / 2)));
                }
            }

            if (component != null)
                builder.append(component);

            if (centered) {
                builder.append(Spaces.getOffset(-((longestWidth - element.getWidth()) / 2)));
            }

            if (!lastLine) {
                builder.append(Spaces.getOffset(-element.getWidth() - 1));
            }
        }

        /**
         * Compensating the last line.
         *
         * Each line except for the last one is neutralized (it's width is fully offset,
         * think of this like a typewriter making a new line and the cursor being set to
         * the start).
         *
         * Titles center text by default. We want the tooltip to also be centered. For
         * this to happen, the last line has to have the same width as its longest line.
         *
         * In summary, we set the tooltips center to that of the longest (pixel-wise)
         * line.
         *
         * After that, we can easily apply the horizontalShift (user-defined, allows the
         * tooltip to be shown off center).
         */
        if (lastLineWidth <= longestWidth) {
            int lastLineOffset = -lastLineWidth;
            int missing = (longestWidth - lastLineWidth);

            int totalLineWidth = lastLineWidth + missing;
            int horizontalShift = tooltipProperties.getHorizontalShift();

            // Move to the right
            if (horizontalShift > 0) {
                if (missing != 0)
                    builder.append(Spaces.getOffset(-missing));
                builder.append(Spaces.getOffset(lastLineOffset - 2));
            }

            // Move to the left
            if (horizontalShift < 0) {
                builder.append(Spaces.getOffset(-horizontalShift));
                builder.append(Spaces.getOffset(totalLineWidth));
            }

            if (missing != 0)
                builder.append(Spaces.getOffset(missing));
        }

        // Disable shadows
        if (!Tooltips.get().isUseShadows()) {
            builder.style(Style.style().shadowColor(ShadowColor.none()).build());
        }

        final Component component = builder.build();
        return new Tooltip(component);
    }

    public Tooltip build(Preset preset, List<String> text) {
        return build(TooltipProperties.from(preset), text);
    }

    public Tooltip build(Theme theme, List<String> text) {
        return build(TooltipProperties.from(theme), text);
    }

}
