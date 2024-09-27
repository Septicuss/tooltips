package fi.septicuss.tooltips.managers.tooltip.building.text;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.managers.theme.Theme;
import fi.septicuss.tooltips.pack.impl.IconGenerator;
import fi.septicuss.tooltips.pack.impl.LineGenerator;

public class LineProperties {

	private Theme theme;
	private int lineAscent;
	private int lineIndex;
	private String defaultFont;
	private String offsetFont;
	private String iconFont;

	public LineProperties(Theme theme, int lineIndex) {
		if (theme == null) {
			Tooltips.warn("Theme cannot be null");
			return;
		}
		
		this.theme = theme;
		this.lineIndex = lineIndex;
		this.lineAscent = theme.getTextStartAscent() - (lineIndex * theme.getTextLineSpacing());

		final String regularFontFormat = "tooltips:lines/" + LineGenerator.REGULAR_LINE_FORMAT;
		final String offsetFontFormat = "tooltips:lines/" + LineGenerator.OFFSET_LINE_FORMAT;
		final String iconFontFormat = "tooltips:icons/" + IconGenerator.ICON_FONT_FORMAT;
		
		this.defaultFont = String.format(regularFontFormat, lineAscent);
		this.offsetFont = String.format(offsetFontFormat, lineAscent);
		this.iconFont = String.format(iconFontFormat, lineAscent);
	}

	public Theme getTheme() {
		return theme;
	}
	
	public int getLineAscent() {
		return lineAscent;
	}

	public int getLineIndex() {
		return lineIndex;
	}

	public String getRegularFont() {
		return defaultFont;
	}

	public String getOffsetFont() {
		return offsetFont;
	}
	
	public String getIconFont() {
		return iconFont;
	}

	public int getMaxLines() {
		return theme.getLines();
	}

}