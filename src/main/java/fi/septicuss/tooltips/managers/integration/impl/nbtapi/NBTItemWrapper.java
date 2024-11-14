package fi.septicuss.tooltips.managers.integration.impl.nbtapi;

import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;

public class NBTItemWrapper extends NBTWrapper<ItemStack> {

	private NBTCompound compound;

	public NBTItemWrapper(ItemStack item) {
		super(item);

		if (hasNBTAPI())
			this.compound = new NBTItem(item);
	}

	public NBTCompound getCompound() {
		return compound;
	}

}
