package fi.septicuss.tooltips.managers.integration.impl.nbtapi;

import org.bukkit.block.BlockState;

import de.tr7zw.nbtapi.NBTCompound;
import de.tr7zw.nbtapi.NBTTileEntity;

public class NBTTileEntityWrapper extends NBTWrapper<BlockState> {

	private NBTCompound compound;

	public NBTTileEntityWrapper(BlockState state) {
		super(state);

		if (hasNBTAPI())
			this.compound = new NBTTileEntity(state);
	}

	public NBTCompound getCompound() {
		return compound;
	}

}
