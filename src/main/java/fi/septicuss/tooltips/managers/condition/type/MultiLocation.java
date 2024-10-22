package fi.septicuss.tooltips.managers.condition.type;

import com.google.common.collect.Lists;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.validation.Validity;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class MultiLocation {

	private List<Location> locations;
	
	public MultiLocation(List<Location> locations) {
		this.locations = locations;
	}
	
	public boolean contains(Location location) {
		for (var loc : locations) {
			if (loc.equals(location)) {
				return true;
			}
		}
		
		return false;
	}
	
	public List<Location> getLocations() {
		return locations;
	}
	
	public static MultiLocation of(Player player, String line) {
		List<Location> locations = Lists.newArrayList();
		List<String> locationArgs = parseLocations(line);
		
		for (var locationArg : locationArgs) {
			locations.add(LocationArgument.of(player, locationArg).getLocation());
		}
		
		return new MultiLocation(locations);
	}
	
	/**
	 * Checks the validity of a location array argument string
	 * 
	 * @param line In format [x, y, z], [x, y, z], ...
	 * @return
	 */
	public static Validity validityOf(String line) {
		if (!line.contains("[") || !line.contains("]")) {
			return Validity.of(false, "Location argument must be within square brackets [x, y, z]");
		}

		List<String> locationArgStrings = parseLocations(line);

		if (locationArgStrings == null) {
			return Validity.of(false, "Unclosed bracket!");
		}
		
		for (var locationArgString : locationArgStrings) {
			
			if (locationArgString.equals("ERROR")) {
				return Validity.of(false, "Failed to parse " + Utils.quote(locationArgString));
			}
			
			Validity validity = LocationArgument.validityOf(locationArgString);
			
			if (validity.isValid()) {
				continue;
			}
			
			return Validity.of(false, validity.getReason() + " (was " + Utils.quote(locationArgString) + " )");
		}
		
		return Validity.TRUE;
	}
	
	private static ArrayList<String> parseLocations(String line) {
		ArrayList<String> locations = new ArrayList<>();
		
		boolean insideBracket = false;
		StringBuilder builder = new StringBuilder();
		
		for (char c : line.toCharArray()) {
			if (c == '[') {
				insideBracket = true;
			}
			
			if (insideBracket) {
				builder.append(c);
			}
			
			if (c == ']') {
				insideBracket = false;
				
				String locationArgString = builder.toString().strip();
				
				if (locationArgString.isBlank()) {
					locations.add("ERROR");
					return locations;
				}
				
				locations.add(locationArgString);
				builder.setLength(0);
			}
		}
		
		if (insideBracket) {
			return null;
		}
		
		return locations;
	}
	
	public static void main(String[] args) {
		
		String test = "[]";

		Validity validity = validityOf(test);
		
		if (validity.isValid()) {
			System.out.println("Valid");
		} else {
			System.out.println(validity.getReason());
			
		}
		
	}
	
}
