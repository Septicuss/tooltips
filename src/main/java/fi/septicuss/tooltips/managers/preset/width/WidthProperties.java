package fi.septicuss.tooltips.managers.preset.width;

import org.bukkit.configuration.ConfigurationSection;

public class WidthProperties {

    private final int minWidth;
    private final int maxWidth;

    public WidthProperties(int minWidth, int maxWidth) {
        this.minWidth = minWidth;
        this.maxWidth = maxWidth;
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

    public static WidthProperties of(ConfigurationSection widthSection) {
        if (widthSection == null)
            return new WidthProperties(0, 0);

        int min = widthSection.getInt("min", 0);
        int max = widthSection.getInt("max", 0);

        return new WidthProperties(min, max);
    }
}
