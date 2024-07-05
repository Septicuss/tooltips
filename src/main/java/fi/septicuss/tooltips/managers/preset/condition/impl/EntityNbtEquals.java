package fi.septicuss.tooltips.managers.preset.condition.impl;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import fi.septicuss.tooltips.integrations.IntegratedPlugin;
import fi.septicuss.tooltips.integrations.nbtapi.NBTEntityWrapper;
import fi.septicuss.tooltips.managers.preset.condition.Condition;
import fi.septicuss.tooltips.managers.preset.condition.argument.Argument;
import fi.septicuss.tooltips.managers.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.Utils;
import fi.septicuss.tooltips.utils.validation.Validity;

public class EntityNbtEquals implements Condition {

	private static final String[] DISTANCE = { "d", "dist", "distance" };
	private static final String[] KEY = { "key", "k" };
	private static final String[] VALUE = { "nbtval", "value", "nbtvalue", "val", "v" };

	@Override
	public boolean check(Player player, Arguments args) {
		int distance = 3;

		if (args.has(DISTANCE))
			distance = args.get(DISTANCE).getAsInt();

		var rayTrace = Utils.getRayTraceResult(player, distance);
		
		if (rayTrace == null || rayTrace.getHitEntity() == null)
			return false;
		
		Entity entity = rayTrace.getHitEntity();

		String key = args.get(KEY).getAsString();
		var compound = new NBTEntityWrapper(entity).getCompound();

		Argument valueArg = args.get(VALUE);

		if (key.contains(".")) {
			String[] split = key.split("\\.");
			for (int i = 0; i < split.length; i++) {
				if (i == split.length - 1) {
					key = split[i];
					continue;
				}
				compound = compound.getCompound(split[i]);
				if (compound == null)
					return false;
			}
		}

		if (valueArg.isNumber()) {
			int num = valueArg.getAsInt();
			return compound.getInteger(key) == num;
		}

		if (valueArg.isBoolean()) {
			boolean bool = valueArg.getAsBool();
			return compound.getBoolean(key) == bool;
		}

		String str = valueArg.getAsString();
		return compound.getString(key).equals(str);
	}

	@Override
	public Validity valid(Arguments args) {
		if (!IntegratedPlugin.NBTAPI.isEnabled()) {
			return Validity.of(false, "Cannot use entitynbtequals because NBTAPI is not installed");
		}

		if (!args.has(KEY)) {
			return Validity.of(false, "Key argument is missing");
		}

		if (!args.has(VALUE)) {
			return Validity.of(false, "Value argument is missing");
		}

		return Validity.TRUE;
	}

}
