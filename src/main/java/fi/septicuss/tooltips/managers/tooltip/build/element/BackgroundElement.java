package fi.septicuss.tooltips.managers.tooltip.build.element;

import fi.septicuss.tooltips.managers.theme.Theme;
import fi.septicuss.tooltips.managers.theme.ThemeManager;
import fi.septicuss.tooltips.utils.AdventureUtils;
import net.kyori.adventure.text.Component;

public class BackgroundElement implements TooltipElement {

	private static final int SIDE_PARTS = 2;

	private final Theme theme;
	private final String color;
	private final boolean gradient;
	private final int requiredWidth;
	private int width;

	private Component component;

	public BackgroundElement(Theme theme, String color, int requiredWidth) {
		this.theme = theme;
		this.color = color.replace('-', ':');
		this.gradient = color.contains(":");
		this.requiredWidth = requiredWidth;
	}

	@Override
	public Component getComponent() {
		if (this.component != null)
			return this.component;

		final int middleParts = (int) (((double)requiredWidth / (double)theme.getWidth()) + SIDE_PARTS);
		final int totalParts = middleParts + SIDE_PARTS;

		double width = theme.getWidth() * (double)totalParts;
		this.width = (int) (width);

		final String backgroundString = constructBackgroundString(middleParts);
		String toFormat;

		if (gradient) {
			toFormat = String.format("<gradient:%s>%s</gradient>", color, backgroundString);
		} else {
			toFormat = String.format("<color:%s>%s</color>", color, backgroundString);
		}

		this.component = AdventureUtils.MINIMESSAGE.deserialize(toFormat).font(theme.getFontKey());
		return this.component;
	}

	private String constructBackgroundString(int middleParts) {
		final StringBuilder backgroundBuilder = new StringBuilder();

		backgroundBuilder.append(ThemeManager.LEFT);

		for (int i = 0; i < middleParts; i++) {
			backgroundBuilder.append(ThemeManager.OFFSET);
			backgroundBuilder.append(ThemeManager.CENTER);
		}

		backgroundBuilder.append(ThemeManager.OFFSET);
		backgroundBuilder.append(ThemeManager.RIGHT);
		return backgroundBuilder.toString();
	}

	@Override
	public int getWidth() {
		return width;
	}

}
