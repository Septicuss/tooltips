package fi.septicuss.tooltips.managers.integration.impl.nbtapi;

import de.tr7zw.nbtapi.NBTCompound;
import org.bukkit.Bukkit;

public abstract class NBTWrapper<T> {

	public NBTWrapper(T object) {
	}

	public abstract NBTCompound getCompound();

	public boolean hasNBTAPI() {
		return Bukkit.getPluginManager().isPluginEnabled("NBTAPI");
	}
}
