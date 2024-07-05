package fi.septicuss.tooltips.managers.preset.condition.impl;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import fi.septicuss.tooltips.managers.preset.condition.Condition;
import fi.septicuss.tooltips.managers.preset.condition.argument.Argument;
import fi.septicuss.tooltips.managers.preset.condition.argument.Arguments;
import fi.septicuss.tooltips.managers.preset.condition.type.EnumOptions;
import fi.septicuss.tooltips.utils.validation.Validity;

public class Equipped implements Condition {

	private static final String[] SLOT = { "slot", "s" };
	private static final String[] MATERIAL = { "type", "m", "mat", "material" };

	@Override
	public boolean check(Player player, Arguments args) {
		EnumOptions<Material> materials = args.get(MATERIAL).getAsEnumOptions(Material.class);
		EquipmentSlot slot = EquipmentSlot.HAND;
		
		if (args.has(SLOT)) {
			slot = EquipmentSlot.valueOf(args.get(SLOT).getAsString().strip().toUpperCase());
		}

		ItemStack item = player.getInventory().getItem(slot);

		if (item == null || item.getType().isAir()) {
			return materials.contains(Material.AIR);
		}

		return (materials.contains(item.getType()));
	}

	@Override
	public Validity valid(Arguments args) {

		if (!args.has(MATERIAL)) {
			return Validity.of(false, "Type argument is required");
		}

		if (args.has(MATERIAL)) {
			Argument materialArg = args.get(MATERIAL);
			Validity optionValidity = EnumOptions.validity(Material.class, materialArg.getAsString());

			if (!optionValidity.isValid()) {
				return optionValidity;
			}
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
