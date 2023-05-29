package fi.septicuss.tooltips.tooltip.building.text;

import fi.septicuss.tooltips.Tooltips;
import fi.septicuss.tooltips.object.theme.Theme;
import fi.septicuss.tooltips.pack.impl.ThemeGenerator;

public class LineProperties {

	private Theme theme;
	private int iconAscent;
	private int lineIndex;
	private String defaultFont;
	private String offsetFont;

	public LineProperties(Theme theme, int lineIndex) {
		if (theme == null) {
			Tooltips.warn("Theme cannot be null");
			return;
		}
		
		this.theme = theme;
		this.lineIndex = lineIndex;
		this.iconAscent = theme.getTextStartAscent() - (lineIndex * theme.getTextLineSpacing());

		final var readableLineIndex = lineIndex + 1;
		final var themeName = theme.getId();

		final String defaultFontFormat = "tooltips:%s/" + ThemeGenerator.DEFAULT_LINE_FORMAT;
		final String offsetFontFormat = "tooltips:%s/" + ThemeGenerator.OFFSET_LINE_FORMAT;

		this.defaultFont = String.format(defaultFontFormat, themeName, readableLineIndex);
		this.offsetFont = String.format(offsetFontFormat, themeName, readableLineIndex);
	}

	public Theme getTheme() {
		return theme;
	}

	public int getIconAscent() {
		return iconAscent;
	}

	public int getLineIndex() {
		return lineIndex;
	}

	public String getDefaultFont() {
		return defaultFont;
	}

	public String getOffsetFont() {
		return offsetFont;
	}

	public int getMaxLines() {
		return theme.getLines();
	}

}