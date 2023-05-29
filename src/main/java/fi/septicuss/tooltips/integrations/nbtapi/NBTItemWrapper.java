package fi.septicuss.tooltips.integrations.nbtapi;

import org.bukkit.inventory.ItemStack;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTItem;
import fi.septicuss.tooltips.integrations.IntegratedPlugin;

public class NBTItemWrapper extends NBTWrapper<ItemStack> {

	private NBTCompound compound;

	public NBTItemWrapper(ItemStack item) {
		super(item);

		if (IntegratedPlugin.NBTAPI.isEnabled())
			this.compound = new NBTItem(item);
	}

	public NBTCompound getCompound() {
		return compound;
	}

}
