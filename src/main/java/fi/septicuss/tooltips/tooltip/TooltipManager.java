package fi.septicuss.tooltips.tooltip;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.icon.IconManager;
import fi.septicuss.tooltips.managers.preset.Preset;
import fi.septicuss.tooltips.managers.theme.Theme;
import fi.septicuss.tooltips.tooltip.building.TooltipProperties;
import fi.septicuss.tooltips.tooltip.building.element.BackgroundElement;
import fi.septicuss.tooltips.tooltip.building.element.TextLineElement;
import fi.septicuss.tooltips.tooltip.building.text.LineProperties;
import fi.septicuss.tooltips.tooltip.building.text.TextLine;
import fi.septicuss.tooltips.utils.font.Spaces;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;

public class TooltipManager {

	private IconManager iconManager;

	public TooltipManager(Tooltips plugin) {
		this.iconManager = plugin.getIconManager();
	}

	public Tooltip getTooltip(Player player, TooltipProperties tooltipProperties, List<String> unprocessedText) {
		List<TextLineElement> textLineElements = new ArrayList<>();

		int longestWidth = 0;
		int lastLineWidth = 0;

		// --- PRE-LOAD ---
		for (int i = 0; i < unprocessedText.size(); i++) {
			String text = unprocessedText.get(i);

			var properties = new LineProperties(tooltipProperties.getTheme(), i);
			var textLine = new TextLine(player, iconManager, text);
			var element = new TextLineElement(properties, textLine);

			// Generate parts
			element.getParts();

			// Cache widths
			boolean lastLine = (i == unprocessedText.size() - 1);
			int elementWidth = element.getWidth();

			if (elementWidth > longestWidth)
				longestWidth = elementWidth;

			if (lastLine) {
				lastLineWidth = elementWidth;
			}

			textLineElements.add(element);
		}

		// --- BACKGROUND ---
		int leftPadding = tooltipProperties.getTheme().getLeftPadding();
		int rightPadding = tooltipProperties.getTheme().getRightPadding();

		var background = new BackgroundElement(tooltipProperties.getTheme(), tooltipProperties.getColor(),
				longestWidth + rightPadding);

		ComponentBuilder componentBuilder = new ComponentBuilder();

		if (tooltipProperties.getHorizontalShift() > 0) {
			componentBuilder.append(Spaces.getOffset(tooltipProperties.getHorizontalShift()));
		}

		componentBuilder.append(Spaces.getOffset(-1));

		for (var backgroundPart : background.getParts())
			componentBuilder.append(backgroundPart);

		int backgroundOffset = -background.getWidth();

		componentBuilder.append(Spaces.getOffset(backgroundOffset + leftPadding));

		// --- TEXT ---
		int index = 0;

		for (var element : textLineElements) {
			boolean lastLine = (index == textLineElements.size() - 1);
			List<BaseComponent> parts = element.getParts();

			if (parts.isEmpty())
				componentBuilder.append(Spaces.getOffset(1));

			boolean centered = false;

			if (element.isCentered()) {
				if (element.getWidth() != longestWidth) {
					centered = true;
					componentBuilder.append(Spaces.getOffset(((longestWidth - element.getWidth()) / 2)));
				}
			}
			
			for (var part : parts) {
				componentBuilder.append(part);
			}

			if (centered) {
				componentBuilder.append(Spaces.getOffset(-((longestWidth - element.getWidth()) / 2)));
			}

			if (!lastLine)
				componentBuilder.append(Spaces.getOffset(-element.getWidth() - 1));

			index++;
		}

		/**
		 * Compensating the last line.
		 * 
		 * Each line except for the last one is "neutered" (it's width is fully offset,
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
					componentBuilder.append(Spaces.getOffset(-missing));
				componentBuilder.append(Spaces.getOffset(lastLineOffset - 2));
			}

			// Move to the left
			if (horizontalShift < 0) {
				componentBuilder.append(Spaces.getOffset(-horizontalShift));
				componentBuilder.append(Spaces.getOffset(totalLineWidth));
			}

			if (missing != 0)
				componentBuilder.append(Spaces.getOffset(missing));
		}

		return new Tooltip(componentBuilder.create());
	}

	public Tooltip getTooltip(Player target, Preset preset, List<String> unprocessedText) {
		if (unprocessedText == null || unprocessedText.isEmpty())
			unprocessedText = preset.getText();

		return getTooltip(target, TooltipProperties.from(preset), unprocessedText);
	}

	public Tooltip getTooltip(Player target, Theme theme, List<String> unprocessedText) {
		return getTooltip(target, TooltipProperties.from(theme), unprocessedText);
	}

}
