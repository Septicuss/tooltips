package fi.septicuss.tooltips.tooltip.building.element;

import java.util.List;
import java.util.Map;

import fi.septicuss.tooltips.object.icon.IconManager;
import fi.septicuss.tooltips.tooltip.building.text.LineProperties;
import fi.septicuss.tooltips.tooltip.building.text.TextLine;
import fi.septicuss.tooltips.utils.font.Spaces;
import fi.septicuss.tooltips.utils.font.Widths;
import fi.septicuss.tooltips.utils.font.Widths.SizedChar;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.TextComponent;

public class TextLineElement implements TooltipElement {

	static FormatRetention RETENTION = FormatRetention.NONE;
	static Map<String, TextComponent> replaceables;

	private LineProperties lineProperties;
	private TextLine textLine;
	private double totalWidth = 0;

	private List<BaseComponent> parts;

	public TextLineElement(LineProperties lineProperties, TextLine textLine) {
		this.lineProperties = lineProperties;
		this.textLine = textLine;
	}

	@Override
	public List<BaseComponent> getParts() {
		if (parts != null)
			return parts;

		final ComponentBuilder componentBuilder = new ComponentBuilder();
		final StringBuilder builder = new StringBuilder();

		final List<BaseComponent> lineComponents = textLine.getLineComponents().getParts();

		boolean offset = false;
		boolean firstChar = true;
		boolean previousIsIcon = false;

		for (var component : lineComponents) {

			final boolean hasFont = (component.getFont() != null);
			final boolean isIcon = (hasFont && component.getFont().equals(IconManager.ICON_FONT_PLACEHOLDER));
			final boolean isSpace = (hasFont && component.getFont().equals(Spaces.OFFSET_FONT_NAME));
			// ICON
			if (isIcon) {
				final TextComponent textComponent = (TextComponent) component;
				TextComponent original = new TextComponent(component.toPlainText());

				final char unicode = textComponent.getText().charAt(0);
				final SizedChar sizedChar = Widths.getSizedChar(unicode);

				final int negativeSpace = sizedChar.getNegativeSpace();
				final double addedWidth = getIconWidth(unicode, sizedChar, firstChar);

				this.totalWidth += addedWidth;

				if (firstChar) {
					firstChar = false;
				} else {
					final boolean shouldOffset = negativeSpace >= 1;
					final String modifiedIconText = (shouldOffset ? Spaces.NEGATIVE_ONE : "") + textComponent.getText();

					original = new TextComponent(modifiedIconText);
				}

				original.copyFormatting(textComponent, true);
				componentBuilder.append(original).font(lineProperties.getIconFont());
				previousIsIcon = true;
				continue;
			}

			// SPACE
			if (isSpace) {
				int offsetAmount = 0;

				for (char character : component.toPlainText().toCharArray()) {
					for (var entry : Spaces.getOffsetMapEntries()) {
						if (character != entry.getValue())
							continue;
						offsetAmount += entry.getKey();
					}
				}

				componentBuilder.append(component, RETENTION);
				this.totalWidth += offsetAmount;
				continue;
			}

			// TEXT
			final ChatColor color = component.getColor();
			final String plain = component.toPlainText();

			for (char character : plain.toCharArray()) {
				final SizedChar sizedChar = Widths.getSizedChar(character);
				final double addedWidth = getCharWidth(character, sizedChar, firstChar, previousIsIcon);

				this.totalWidth += addedWidth;

				if (firstChar) {
					firstChar = false;
				} else {
					if (!previousIsIcon) {
						builder.append(Spaces.NEGATIVE_ONE);
					} else {
						previousIsIcon = false;
					}
				}
				
				builder.append(character);
				
				if (character == ' ') {
					builder.append(character);
					builder.append(character);
				}
				
				if (sizedChar.getRealWidth() % 1 == 0 && character != ' ') {
					final String result = builder.toString();

					TextComponent textComponent = new TextComponent(result);
					textComponent.setFont((offset ? lineProperties.getOffsetFont() : lineProperties.getRegularFont()));
					textComponent.setColor(color);

					componentBuilder.append(textComponent);

					offset = !offset;
					builder.setLength(0);

					continue;
				}

			}

			if (!builder.toString().isEmpty()) {
				TextComponent textComponent = new TextComponent(builder.toString());
				textComponent.setFont((offset ? lineProperties.getOffsetFont() : lineProperties.getRegularFont()));
				textComponent.setColor(color);

				componentBuilder.append(textComponent);
			}

			builder.setLength(0);
		}

		this.parts = componentBuilder.getParts();
		return this.parts;
	}

	@Override
	public int getWidth() {
		return (int) totalWidth;
	}

	private double getCharWidth(char character, SizedChar sizedChar, boolean firstChar, boolean previousIsIcon) {
		double width = (int) (sizedChar.getAbsoluteWidth() * (double) sizedChar.getHeightRatio()) + 1;

		if (previousIsIcon)
			width += Math.ceil(sizedChar.getHeightRatio());

		if (sizedChar.getRealWidth() % 1 == 0 && character != ' ')
			width -= sizedChar.getHeightRatio();

		return width;
	}

	private double getIconWidth(char chracter, SizedChar sizedChar, boolean firstChar) {
		final int negativeSpace = sizedChar.getNegativeSpace();
		double width = (int) (sizedChar.getAbsoluteWidth() * (double) sizedChar.getHeightRatio()) + 1;

		final double ratio = sizedChar.getHeightRatio();

		double amount;
		if (ratio == 0.5)
			amount = 1;
		else if (ratio > 0.5)
			amount = (int) ratio;
		else if (ratio == 0.25)
			amount = 0;
		else
			amount = 1;

		if (negativeSpace >= 1)
			width -= amount;

		if (firstChar)
			if (negativeSpace < 1)
				width -= amount;

		return width;
	}

}
