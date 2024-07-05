package fi.septicuss.tooltips.managers.preset.show;

import org.bukkit.configuration.ConfigurationSection;

public class ShowProperties {

	private boolean active;
	private int limit;
	private int cooldown;

	public ShowProperties(boolean active, int limit, int cooldown) {
		this.active = active;
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

	public static ShowProperties of(ConfigurationSection showSection) {
		if (showSection == null)
			return new ShowProperties(true, 0, 0);

		boolean active;
		int limit;
		int cooldown;

		active = showSection.getBoolean("active", true);
		limit = showSection.getInt("limit", 0);
		cooldown = showSection.getInt("cooldown", 0);
		return new ShowProperties(active, limit, cooldown);
	}

}
