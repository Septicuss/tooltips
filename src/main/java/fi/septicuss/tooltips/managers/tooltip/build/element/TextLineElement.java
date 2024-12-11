package fi.septicuss.tooltips.managers.tooltip.build.element;

import fi.septicuss.tooltips.managers.icon.IconManager;
import fi.septicuss.tooltips.managers.tooltip.build.text.TextLine;
import fi.septicuss.tooltips.utils.AdventureUtils;
import fi.septicuss.tooltips.utils.font.Spaces;
import fi.septicuss.tooltips.utils.font.Widths;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.ComponentIteratorType;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.minimessage.tag.Tag;
import net.kyori.adventure.text.minimessage.tag.resolver.TagResolver;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.OptionalInt;

public class TextLineElement implements TooltipElement{

    private static final double HALF = 0.5;
    private static final double QUARTER = 0.25;
    private static final int PIXEL = 1;

    private final List<TagResolver> tagResolvers;
    private final TextLine textLine;

    private Component component;
    private double totalWidth;

    public TextLineElement(IconManager iconManager, TextLine textLine) {
        this.textLine = textLine;
        this.tagResolvers = new ArrayList<>();
        this.tagResolvers.add(TagResolver.resolver("icon", (args, context) -> {
            final String iconPath = args.pop().value();
            final char iconUnicode = iconManager.getUnicodeFor(iconPath);
            return Tag.selfClosingInserting(Component.text(iconUnicode).font(textLine.getIconFont()));
        }));
        this.tagResolvers.add(TagResolver.resolver("offset", (args, context) -> {
            final OptionalInt optionalAmount = args.pop().asInt();
            if (optionalAmount.isPresent()) {
                return Tag.selfClosingInserting(Spaces.getOffset(optionalAmount.getAsInt()).asComponent());
            }
            return null;
        }));
    }

    @Override
    public Component getComponent() {
        if (this.component != null) {
            return this.component;
        }

        if (this.textLine.getText().isEmpty()) {
            return null;
        }

        final TagResolver[] iconTagResolverArray = this.tagResolvers.toArray(new TagResolver[0]);

        Component component = AdventureUtils.MINIMESSAGE.deserialize(textLine.getText().replace('§', ' '), iconTagResolverArray);

        final Iterator<Component> iterator = component.iterator(ComponentIteratorType.DEPTH_FIRST);

        final TextComponent.Builder builder = Component.text();
        final StringBuilder stringBuilder = new StringBuilder();

        boolean firstChar = true;
        boolean offset = false;

        while (iterator.hasNext()) {
            final Component child = iterator.next();

            if (child instanceof TextComponent textComponent) {
                final String content = textComponent.content();

                final boolean hasFont = (textComponent.font() != null);
                final boolean isIcon = (hasFont && textComponent.font().equals(textLine.getIconFont()));
                final boolean isSpace = (hasFont && textComponent.font().equals(Spaces.OFFSET_FONT_KEY));

                // ICON
                if (isIcon) {
                    final StringBuilder iconTextBuilder = new StringBuilder();
                    double addedWidth = 0;

                    for (char unicode : content.toCharArray()) {
                        final Widths.SizedChar sizedChar = Widths.getIconSizedChar(unicode);
                        final int negativeSpace = sizedChar.getNegativeSpace();

                        addedWidth += this.getIconWidth(sizedChar, firstChar);

                        if (firstChar) {
                            firstChar = false;
                        } else {
                            final boolean shouldOffset = negativeSpace >= 1;

                            iconTextBuilder.append(shouldOffset ? Spaces.NEGATIVE_ONE : "");
                        }

                        iconTextBuilder.append(unicode);
                    }

                    this.totalWidth += addedWidth;
                    textComponent = textComponent.content(iconTextBuilder.toString());

                    builder.append(textComponent);
                    continue;
                }

                // SPACE
                if (isSpace) {
                    int offsetAmount = 0;

                    for (char character : content.toCharArray()) {
                        for (var entry : Spaces.getOffsetMapEntries()) {
                            if (character != entry.getValue()) {
                                continue;
                            }
                            offsetAmount += entry.getKey();
                        }
                    }

                    builder.append(textComponent);
                    this.totalWidth += offsetAmount;
                    continue;
                }

                // TEXT
                for (char character : content.toCharArray()) {
                    final Widths.SizedChar sizedChar = Widths.getSizedChar(character);

                    // Handle special language characters, which offset on top of other characters
                    if (sizedChar.hasOverridingWidth() && sizedChar.getOverridingWidth() < 0) {

                        var textFont = (offset ? textLine.getOffsetFont() : textLine.getRegularFont());
                        var text = Component.text(stringBuilder.toString())
                                .font(textFont)
                                .color(child.color());
                        builder.append(text);
                        builder.append(Spaces.getOffset((int) sizedChar.getOverridingWidth()));

                        stringBuilder.setLength(0);
                        stringBuilder.append(character);
                        continue;
                    }

                    final double addedWidth = getCharWidth(character, sizedChar);

                    this.totalWidth += addedWidth;

                    if (firstChar) {
                        firstChar = false;
                    } else {
                        stringBuilder.append(Spaces.NEGATIVE_ONE);
                    }

                    stringBuilder.append(character);

                    if (sizedChar.getRealWidth() % PIXEL == 0 && character != ' ') {
                        final String result = stringBuilder.toString();

                        var textFont = (offset ? textLine.getOffsetFont() : textLine.getRegularFont());
                        var text = Component.text(result)
                                .font(textFont)
                                .color(child.color());

                        builder.append(text);

                        offset = !offset;
                        stringBuilder.setLength(0);
                    }
                }

            }

            if (!stringBuilder.toString().isEmpty()) {
                var textFont = (offset ? textLine.getOffsetFont() : textLine.getRegularFont());
                var text = Component.text(stringBuilder.toString())
                        .font(textFont)
                        .color(child.color());

                builder.append(text);
            }

            stringBuilder.setLength(0);

        }

        this.component = builder.build().asComponent();
        return this.component;

    }

    @Override
    public int getWidth() {
        return (int) this.totalWidth;
    }

    private double getCharWidth(char character, Widths.SizedChar sizedChar) {
        if (sizedChar.hasOverridingWidth()) {
            return sizedChar.getOverridingWidth();
        }

        if (character == ' ') {
            return PIXEL * 2;
        }

        int negativeSpace = sizedChar.getNegativeSpace();

        double width = 0;

        width = (negativeSpace + sizedChar.getExactWidth());
        width *= sizedChar.getHeightRatio();

        /**
         * Adds space between characters, which normally is 1, but since we're sizing
         * everything down 2x, that width is 0.5
         */
        if (negativeSpace <= 1) {
            width += HALF;
        }

        return width;
    }

    private double getIconWidth(Widths.SizedChar sizedChar, boolean firstCharacter) {
        final int negativeSpace = sizedChar.getNegativeSpace();
        final double heightRatio = sizedChar.getHeightRatio();

        double width = sizedChar.getRealWidth();
        double subtracted = PIXEL;

        // Mostly experimental, magic values.
        // Subtracting from width, based on the height ratio of the character.
        if (heightRatio > HALF) {
            subtracted = (int) heightRatio;
        } else if (heightRatio == QUARTER) {
            subtracted = 0;
        }

        final boolean hasNegativeSpace = (negativeSpace >= PIXEL);

        if (hasNegativeSpace) {
            width -= subtracted;
        }

        if (firstCharacter)
            if (negativeSpace < 1) {
                width -= subtracted;
            }

        return width;
    }

}
