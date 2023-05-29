package fi.septicuss.tooltips.object.preset.condition.impl;

import org.bukkit.entity.Player;

import fi.septicuss.tooltips.object.preset.condition.Condition;
import fi.septicuss.tooltips.object.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.object.preset.condition.type.MultiString;
import fi.septicuss.tooltips.object.validation.Validity;
import fi.septicuss.tooltips.utils.cache.area.CurrentAreaCache;

public class Region implements Condition {

	private static final String[] REGION = { "r", "reg", "region", "name", "id" };

	@Override
	public boolean check(Player player, Arguments args) {
		MultiString region = null;

		if (args.has(REGION))
			region = MultiString.of(args.get(REGION).getAsString());

		if (region == null) {
			return CurrentAreaCache.has(player);
		}

		if (!CurrentAreaCache.has(player))
			return false;

		for (var applicableRegion : CurrentAreaCache.get(player))
			if (region.contains(applicableRegion))
				return true;

		return false;
	}

	@Override
	public Validity valid(Arguments args) {
		return Validity.TRUE;
	}

}
