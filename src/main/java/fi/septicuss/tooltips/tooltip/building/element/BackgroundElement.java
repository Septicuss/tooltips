package fi.septicuss.tooltips.tooltip.building.element;

import java.util.List;

import fi.septicuss.tooltips.object.theme.Theme;
import fi.septicuss.tooltips.object.theme.ThemeManager;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.ComponentBuilder;
import net.md_5.bungee.api.chat.TextComponent;

public class BackgroundElement implements TooltipElement {

	private static final int SIDE_PARTS = 2;

	private Theme theme;
	private ChatColor color;
	private int requiredWidth;
	private int width;

	private List<BaseComponent> parts;

	public BackgroundElement(Theme theme, ChatColor color, int requiredWidth) {
		this.theme = theme;
		this.color = color;
		this.requiredWidth = requiredWidth;
	}

	@Override
	public List<BaseComponent> getParts() {
		if (parts != null)
			return parts;

		final int middleParts = (requiredWidth / theme.getWidth()) + SIDE_PARTS;
		final int totalParts = middleParts + SIDE_PARTS;

		this.width = theme.getWidth() * totalParts;

		// Construct the component
		final String font = theme.getFontName();
		final StringBuilder backgroundBuilder = new StringBuilder();

		backgroundBuilder.append(ThemeManager.LEFT);

		for (int i = 0; i < middleParts; i++) {
			backgroundBuilder.append(ThemeManager.OFFSET);
			backgroundBuilder.append(ThemeManager.CENTER);
		}

		backgroundBuilder.append(ThemeManager.OFFSET);
		backgroundBuilder.append(ThemeManager.RIGHT);

		final String backgroundString = backgroundBuilder.toString();
		final TextComponent component = new TextComponent(backgroundString);

		component.setFont(font);
		component.setColor(color);

		parts = new ComponentBuilder().append(component).getParts();
		return parts;
	}

	@Override
	public int getWidth() {
		return width;
	}

}
