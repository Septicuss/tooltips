package fi.septicuss.tooltips.integrations.nbtapi;

import de.tr7zw.nbtapi.NBTCompound;

public abstract class NBTWrapper<T> {

	public NBTWrapper(T object) {
	}

	public abstract NBTCompound getCompound();
}
