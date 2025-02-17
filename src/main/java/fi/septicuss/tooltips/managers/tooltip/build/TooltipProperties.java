package fi.septicuss.tooltips.managers.tooltip.build;

import fi.septicuss.tooltips.managers.preset.Preset;
import fi.septicuss.tooltips.managers.theme.Theme;

public class TooltipProperties {

	private Theme theme;
	private String color;
	private boolean shadow;
	private int horizontalShift;

	protected TooltipProperties() {

	}

	protected TooltipProperties(Theme theme, String color, boolean shadow, int horizontalShift) {
		this.theme = theme;
		this.color = color;
		this.shadow = shadow;
		this.horizontalShift = horizontalShift;
	}

	public Theme getTheme() {
		return theme;
	}

	public String getColor() {
		return color;
	}

	public boolean hasShadow() {
		return shadow;
	}

	public int getHorizontalShift() {
		return horizontalShift;
	}

	public static TooltipProperties from(Preset preset) {
		return new TooltipProperties(preset.getTheme(), preset.getColor(), preset.hasShadow(), preset.getHorizontalShift());
	}

	public static TooltipProperties from(Theme theme) {
		return new TooltipProperties(theme, "#ffffff", true, 0);
	}

}
