package fi.septicuss.tooltips.integrations.worldguard;

import java.util.List;

import org.bukkit.Location;

import com.google.common.collect.Lists;
import com.sk89q.worldedit.bukkit.BukkitAdapter;
import com.sk89q.worldedit.math.BlockVector3;
import com.sk89q.worldedit.world.World;
import com.sk89q.worldguard.WorldGuard;
import com.sk89q.worldguard.protection.ApplicableRegionSet;
import com.sk89q.worldguard.protection.managers.RegionManager;
import com.sk89q.worldguard.protection.regions.RegionContainer;

import fi.septicuss.tooltips.integrations.AreaProvider;

public class WorldGuardAreaProvider implements AreaProvider {

	private RegionContainer container;

	public WorldGuardAreaProvider() {
		container = WorldGuard.getInstance().getPlatform().getRegionContainer();
	}

	@Override
	public List<String> getApplicableAreas(Location location) {
		if (location == null) return null;
		if (location.getWorld() == null) return null;
		
		final World world = BukkitAdapter.adapt(location.getWorld());
		final RegionManager regions = container.get(world);

		if (regions == null) return null;

		final BlockVector3 vector = BukkitAdapter.asBlockVector(location);

		if (vector == null) return null;

        ApplicableRegionSet applicable = regions.getApplicableRegions(vector);

		if (applicable.size() == 0) {
			return null;
		}

		List<String> regionIds = Lists.newArrayList();
		applicable.forEach(region -> regionIds.add(region.getId()));

		return regionIds;
	}

}
