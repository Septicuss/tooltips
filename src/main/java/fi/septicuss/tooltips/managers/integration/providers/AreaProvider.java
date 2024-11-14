package fi.septicuss.tooltips.managers.integration.providers;

import org.bukkit.Location;

import java.util.List;

public interface AreaProvider extends Provider {

    /**
     * Given a location, return a list which contains identifiers of all areas
     * applicable to the location.
     *
     * @param location Location to check
     * @return A list of area identifiers
     */
    List<String> getApplicableAreas(Location location);


}
