package fi.septicuss.tooltips.managers.integration.impl.nbtapi;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import org.bukkit.inventory.ItemStack;

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
