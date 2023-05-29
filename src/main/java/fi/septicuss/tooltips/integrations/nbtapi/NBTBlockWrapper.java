package fi.septicuss.tooltips.integrations.nbtapi;

import org.bukkit.block.Block;

import de.tr7zw.nbtapi.NBTBlock;
import de.tr7zw.nbtapi.NBTCompound;
import fi.septicuss.tooltips.integrations.IntegratedPlugin;

public class NBTBlockWrapper extends NBTWrapper<Block> {

	private NBTCompound compound;

	public NBTBlockWrapper(Block block) {
		super(block);

		if (IntegratedPlugin.NBTAPI.isEnabled())
			this.compound = new NBTBlock(block).getData();
	}

	public NBTCompound getCompound() {
		return compound;
	}

}
