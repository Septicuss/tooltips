package fi.septicuss.tooltips.tooltip;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.object.icon.IconManager;
import fi.septicuss.tooltips.object.preset.Preset;
import fi.septicuss.tooltips.object.theme.Theme;
import fi.septicuss.tooltips.tooltip.building.TooltipProperties;
import fi.septicuss.tooltips.tooltip.building.element.BackgroundElement;
import fi.septicuss.tooltips.tooltip.building.element.TextLineElement;
import fi.septicuss.tooltips.tooltip.building.text.LineProperties;
import fi.septicuss.tooltips.tooltip.building.text.TextLine;
import fi.septicuss.tooltips.utils.font.Spaces;
import net.md_5.bungee.api.ChatColor;
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

		// PRE-LOAD
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

			if (lastLine)
				lastLineWidth = elementWidth;

			textLineElements.add(element);
		}

		// BACKGROUND
		var background = new BackgroundElement(tooltipProperties.getTheme(), ChatColor.of(tooltipProperties.getColor()), longestWidth);
		
		ComponentBuilder componentBuilder = new ComponentBuilder();

		if (tooltipProperties.getHorizontalShift() > 0) {
			componentBuilder.append(Spaces.getOffset(tooltipProperties.getHorizontalShift()));
		}
		
		componentBuilder.append(Spaces.getOffset(-1));

		for (var backgroundPart : background.getParts())
			componentBuilder.append(backgroundPart);

		int textPadding = tooltipProperties.getTheme().getPadding();
		int backgroundOffset = -background.getWidth();

		componentBuilder.append(Spaces.getOffset(backgroundOffset + textPadding));

		// TEXT
		int index = 0;

		for (var element : textLineElements) {
			boolean lastLine = (index == textLineElements.size() - 1);
			List<BaseComponent> parts = element.getParts();

			if (parts.isEmpty())
				componentBuilder.append(Spaces.getOffset(1));

			for (var part : parts) {
				componentBuilder.append(part);
			}
			
			if (!lastLine)
				componentBuilder.append(Spaces.getOffset(-element.getWidth() - 1));

			index++;
		}

		// Compensate the the last line
		if (lastLineWidth <= longestWidth) {
			int lastLineOffset = -lastLineWidth;
			int missing = (longestWidth - lastLineWidth);

			int totalLineWidth = lastLineWidth + missing;
			int horizontalShift = tooltipProperties.getHorizontalShift();

			if (horizontalShift > 0) {
				if (missing != 0)
					componentBuilder.append(Spaces.getOffset(-missing));
				componentBuilder.append(Spaces.getOffset(lastLineOffset - 2));
			}

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
