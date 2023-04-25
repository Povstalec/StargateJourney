package net.povstalec.sgjourney.common.misc;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;

public class InventoryHelper
{
	public static CompoundTag addItem(int slot, String id, int count, @Nullable CompoundTag tag)
	{
		CompoundTag itemTag = new CompoundTag();

		itemTag.putInt("Slot", slot);
		itemTag.putString("id", id);
		itemTag.putByte("Count", (byte) count);
		
		if(tag != null)
			itemTag.put("tag", tag);
		
		return itemTag;
	}
}
