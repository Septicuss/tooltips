package fi.septicuss.tooltips.managers.integration.impl.nbtapi;

import org.bukkit.entity.Entity;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTEntity;

public class NBTEntityWrapper extends NBTWrapper<Entity> {

	private NBTCompound compound;

	public NBTEntityWrapper(Entity entity) {
		super(entity);

		if (hasNBTAPI())
			this.compound = new NBTEntity(entity);
	}

	@Override
	public NBTCompound getCompound() {
		return compound;
	}

}
