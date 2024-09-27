package fi.septicuss.tooltips.managers.condition.type;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.validation.Validity;

public class LocationArgument {

	private Location location;
	private String world;
	private double x;
	private double y;
	private double z;

	public LocationArgument(Location location) {
		this.location = location;
		this.world = location.getWorld().getName();
		this.x = location.getX();
		this.y = location.getY();
		this.z = location.getZ();
	}

	public String getWorld() {
		return world;
	}

	public double getX() {
		return x;
	}

	public double getY() {
		return y;
	}

	public double getZ() {
		return z;
	}

	public Location getLocation() {
		return location;
	}

	/**
	 * 
	 * @param player
	 * @param string In format [x, y, z]
	 * @return
	 */
	public static LocationArgument of(Player player, String string) {
		if (string.startsWith("[") && string.endsWith("]")) {
			string = string.substring(1, string.length() - 1);
		}

		double x = 0;
		double y = 0;
		double z = 0;

		String[] split = string.split(",");

		for (int i = 0; i < split.length; i++) {
			var str = split[i].strip();

			double value = Double.parseDouble(str);

			if (i == 0)
				x = value;
			if (i == 1)
				y = value;
			if (i == 2)
				z = value;
		}

		Location location = new Location(player.getWorld(), x, y, z);
		return new LocationArgument(location);
	}

	/**
	 * Checks the validity of a location argument string
	 * 
	 * @param string In format [x, y, z]
	 * @return
	 */
	public static Validity validityOf(String string) {

		if (!string.startsWith("[") || !string.endsWith("]")) {
			return Validity.of(false, "Location argument must be within square brackets [x, y, z]");
		}

		string = string.substring(1, string.length() - 1);
		String[] split = string.split(",");

		if (split.length != 3) {
			return Validity.of(false, "Invalid location format. Must be [x, y, z]");
		}

		for (var str : split) {
			try {
				Double.parseDouble(str);
			} catch (IllegalArgumentException e) {
				return Validity.of(false, Utils.quote(str) + " is not a number");
			}
		}

		return Validity.TRUE;
	}

}
