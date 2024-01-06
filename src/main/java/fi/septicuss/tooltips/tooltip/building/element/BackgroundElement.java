package fi.septicuss.tooltips.tooltip.building.element;

import java.util.Collections;
import java.util.List;

import com.google.common.collect.Lists;

import fi.septicuss.tooltips.object.theme.Theme;
import fi.septicuss.tooltips.object.theme.ThemeManager;
import fi.septicuss.tooltips.utils.Colors;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.ComponentBuilder.FormatRetention;
import net.md_5.bungee.api.chat.TextComponent;

public class BackgroundElement implements TooltipElement {

	private static final int SIDE_PARTS = 2;

	private Theme theme;
	private String color;
	private boolean gradient;
	private int requiredWidth;
	private int width;

	private List<BaseComponent> parts;

	public BackgroundElement(Theme theme, String color, int requiredWidth) {
		this.theme = theme;
		this.color = color;
		this.gradient = color.contains("-");
		this.requiredWidth = requiredWidth;
	}

	@Override
	public List<BaseComponent> getParts() {
		if (parts != null)
			return parts;

		final int middleParts = (int) (((double)requiredWidth / (double)theme.getWidth()) + SIDE_PARTS);
		final int totalParts = middleParts + SIDE_PARTS;

		double width = (double)theme.getWidth() * (double)totalParts;
		this.width = (int) (width);

		List<ChatColor> colors = Lists.newArrayList();

		if (gradient)
			colors = Colors.createGradient(totalParts, Colors.parseGradientColors(totalParts, color));
		else
			colors = Collections.singletonList(ChatColor.of(color));

		final boolean singleColor = colors.size() == 1;

		ComponentBuilder builder = singleColor ? constructSingleColorBackground(middleParts, colors.get(0))
				: constructGradientBackground(middleParts, colors);
		
		parts = builder.getParts();
		return parts;
	}

	private ComponentBuilder constructSingleColorBackground(int middleParts, ChatColor color) {
		final ComponentBuilder builder = new ComponentBuilder();
		final StringBuilder backgroundBuilder = new StringBuilder();

		backgroundBuilder.append(ThemeManager.LEFT);

		for (int i = 0; i < middleParts; i++) {
			backgroundBuilder.append(ThemeManager.OFFSET);
			backgroundBuilder.append(ThemeManager.CENTER);
		}
			
		backgroundBuilder.append(ThemeManager.OFFSET);
		backgroundBuilder.append(ThemeManager.RIGHT);
		
		final TextComponent component = new TextComponent(backgroundBuilder.toString());
		component.setFont(theme.getFontName());
		component.setColor(color);
		
		builder.append(component);
		return builder;
	}

	private ComponentBuilder constructGradientBackground(int middleParts, List<ChatColor> colors) {
		final ComponentBuilder builder = new ComponentBuilder();
		final ChatColor firstColor = colors.get(0);

		final String leftPart = String.valueOf(ThemeManager.LEFT);
		final String centerPart = String.valueOf(ThemeManager.OFFSET) + String.valueOf(ThemeManager.CENTER);
		final String rightPart = String.valueOf(ThemeManager.OFFSET) + String.valueOf(ThemeManager.RIGHT);
		
		builder
			.append(leftPart)
			.color(firstColor)
			.font(theme.getFontName());
		
		int gradientIndex = 1;
		
		for (int i = 0; i < middleParts; i++) {
			builder
				.append(centerPart, FormatRetention.FORMATTING)
				.color(colors.get(gradientIndex));
			
			gradientIndex += 1;
		}
		
		builder
			.append(rightPart, FormatRetention.FORMATTING)
			.color(colors.get(gradientIndex));
		
		return builder;
	}

	@Override
	public int getWidth() {
		return width;
	}

}
