package fi.septicuss.tooltips.managers.preset.show;

import org.bukkit.configuration.ConfigurationSection;

public class ShowProperties {

	private final boolean active;
	private final boolean reshowOnChange;
	private final int limit;
	private final int cooldown;

	public ShowProperties(boolean active, boolean reshowOnChange, int limit, int cooldown) {
		this.active = active;
		this.reshowOnChange = reshowOnChange;
		this.limit = limit;
		this.cooldown = cooldown;
	}

	public boolean isActive() {
		return active;
	}

	public int getLimit() {
		return limit;
	}

	public boolean hasLimit() {
		return (limit != 0);
	}

	public int getCooldown() {
		return cooldown;
	}

	public boolean hasCooldown() {
		return (cooldown != 0);
	}

	public boolean shouldRefreshOnChange() {
		return this.reshowOnChange;
	}

	public static ShowProperties of(ConfigurationSection showSection) {
		if (showSection == null)
			return new ShowProperties(true, false, 0, 0);

		boolean active;
		boolean refreshOnChange;
		int limit;
		int cooldown;

		active = showSection.getBoolean("active", true);
		refreshOnChange = showSection.getBoolean("reshow-on-change", showSection.getBoolean("reshow", false));
		limit = showSection.getInt("limit", 0);
		cooldown = showSection.getInt("cooldown", 0);
		return new ShowProperties(active, refreshOnChange, limit, cooldown);
	}

}
