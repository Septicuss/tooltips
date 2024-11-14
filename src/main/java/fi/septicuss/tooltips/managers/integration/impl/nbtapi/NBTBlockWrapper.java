package fi.septicuss.tooltips.managers.integration.impl.nbtapi;

import org.bukkit.block.Block;

import de.tr7zw.nbtapi.NBTBlock;
import de.tr7zw.nbtapi.NBTCompound;

public class NBTBlockWrapper extends NBTWrapper<Block> {

	private NBTCompound compound;

	public NBTBlockWrapper(Block block) {
		super(block);

		if (hasNBTAPI())
			this.compound = new NBTBlock(block).getData();
	}

	public NBTCompound getCompound() {
		return compound;
	}

}
