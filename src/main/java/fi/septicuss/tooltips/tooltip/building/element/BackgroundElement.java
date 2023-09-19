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

		final int middleParts = (requiredWidth / theme.getWidth()) + SIDE_PARTS;
		final int totalParts = middleParts + SIDE_PARTS;

		this.width = theme.getWidth() * totalParts;

		List<ChatColor> colors = Lists.newArrayList();
		
		if (gradient) {
			colors = Colors.createGradient(totalParts, Colors.parseGradientColors(totalParts, color));
		} else {
			colors = Collections.singletonList(ChatColor.of(color));
		}
		
		// Construct the component
		final String font = theme.getFontName();
		final String backgroundString = buildBackground(middleParts, colors);
		final TextComponent component = new TextComponent(backgroundString);

		component.setFont(font);
		
		if (!gradient) {
			component.setColor(ChatColor.of(color));
		}

		parts = new ComponentBuilder().append(component).getParts();
		return parts;
	}
	
	private String buildBackground(int middleParts, List<ChatColor> colors) {
		
		final ChatColor firstColor = colors.get(0);
		final StringBuilder backgroundBuilder = new StringBuilder();
		
		backgroundBuilder.append(String.valueOf(firstColor) + ThemeManager.LEFT);
		int gradientIndex = 1;

		for (int i = 0; i < middleParts; i++) {
			String color = String.valueOf((gradient ? colors.get(gradientIndex) : ""));
			String coloredPart = color + ThemeManager.CENTER;
			
			backgroundBuilder.append(ThemeManager.OFFSET);
			backgroundBuilder.append(coloredPart);
			
			gradientIndex += 1;
		}

		String color = String.valueOf((gradient ? colors.get(gradientIndex) : ""));
		String coloredPart = color + ThemeManager.RIGHT;

		backgroundBuilder.append(ThemeManager.OFFSET);
		backgroundBuilder.append(coloredPart);

		final String backgroundString = backgroundBuilder.toString();
		
		return backgroundString;
	}
	
	
	@Override
	public int getWidth() {
		return width;
	}

}
