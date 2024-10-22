package fi.septicuss.tooltips.managers.tooltip.build;

import fi.septicuss.tooltips.managers.preset.Preset;
import fi.septicuss.tooltips.managers.preset.width.WidthProperties;
import fi.septicuss.tooltips.managers.theme.Theme;

public class TooltipProperties {

	private Theme theme;
	private String color;
	private int horizontalShift;
	private int maxWidth;
	private int minWidth;

	protected TooltipProperties() {

	}

	protected TooltipProperties(Theme theme, String color, int horizontalShift, int maxWidth, int minWidth) {
		this.theme = theme;
		this.color = color;
		this.horizontalShift = horizontalShift;
		this.maxWidth = maxWidth;
		this.minWidth = minWidth;
	}

	public Theme getTheme() {
		return theme;
	}

	public String getColor() {
		return color;
	}

	public int getHorizontalShift() {
		return horizontalShift;
	}

	public int getMinWidth() {
		return minWidth;
	}

	public boolean hasMinWidth() {
		return minWidth != 0;
	}

	public int getMaxWidth() {
		return maxWidth;
	}

	public boolean hasMaxWidth() {
		return maxWidth != 0;
	}

	public static TooltipProperties from(Preset preset) {
		final WidthProperties widthProperties = preset.getWidthProperties();
		return new TooltipProperties(preset.getTheme(), preset.getColor(), preset.getHorizontalShift(), widthProperties.getMaxWidth(), widthProperties.getMinWidth());
	}

	public static TooltipProperties from(Theme theme) {
		return new TooltipProperties(theme, "#ffffff", 0, 0, 0);
	}

}
