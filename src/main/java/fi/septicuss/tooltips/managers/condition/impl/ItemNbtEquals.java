package fi.septicuss.tooltips.managers.condition.impl;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import fi.septicuss.tooltips.managers.integration.impl.nbtapi.NBTItemWrapper;
import fi.septicuss.tooltips.managers.condition.Condition;
import fi.septicuss.tooltips.managers.condition.argument.Argument;
import fi.septicuss.tooltips.managers.condition.argument.Arguments;
import fi.septicuss.tooltips.utils.validation.Validity;

public class ItemNbtEquals implements Condition {

	private static final String[] KEY = { "key", "k" };
	private static final String[] VALUE = { "nbtval", "value", "nbtvalue", "val", "v" };
	private static final String[] SLOT = { "slot", "s" };

	public boolean check(Player player, Arguments args) {
		EquipmentSlot slot = EquipmentSlot.HAND;
		if (args.has(SLOT))
			slot = EquipmentSlot.valueOf(args.get(SLOT).getAsString().strip().toUpperCase());

		ItemStack item = player.getInventory().getItem(slot);

		if (item == null || item.getType().isAir())
			return false;

		String key = args.get(KEY).getAsString();
		var nbti = new NBTItemWrapper(item).getCompound();

		if (key.contains(".")) {
			String[] split = key.split("\\.");
			for (int i = 0; i < split.length; i++) {
				if (i == split.length - 1) {
					key = split[i];
				} else {
					nbti = nbti.getCompound(split[i]);
					if (nbti == null)
						return false;
				}
			}
		}

		Argument valueArg = args.get(VALUE);

		if (valueArg.isNumber()) {
			int i = valueArg.getAsInt();
			return (nbti.getInteger(key).intValue() == i);
		}
		if (valueArg.isBoolean()) {
			boolean bool = valueArg.getAsBool();
			return (nbti.getBoolean(key).booleanValue() == bool);
		}

		String value = valueArg.getAsString();
		return nbti.getString(key).equals(value);
	}

	public Validity valid(Arguments args) {
		if (!Bukkit.getPluginManager().isPluginEnabled("NBTAPI")) {
			return Validity.of(false, "Cannot use itemnbtequals because NBTAPI is not installed");
		}

		if (!args.has(KEY)) {
			return Validity.of(false, "Key argument is missing");
		}

		if (!args.has(VALUE)) {
			return Validity.of(false, "Value argument is missing");
		}

		if (args.has(SLOT)) {
			String slot = args.get(SLOT).getAsString();
			try {
				return Validity.of(false, "Unknown slot " + quote(slot));
			} catch (IllegalArgumentException e) {
				return Validity.of(false, null);
			}
		}

		return Validity.TRUE;
	}

}
