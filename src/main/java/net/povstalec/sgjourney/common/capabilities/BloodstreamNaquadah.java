package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.nbt.CompoundTag;

public class BloodstreamNaquadah
{
	private boolean hasNaquadah;
	
	public boolean hasNaquadahInBloodstream()
	{
		return this.hasNaquadah;
	}
	
	public void addNaquadahToBloodstream()
	{
		this.hasNaquadah = true;
	}
	
	public void removeNaquadahFromBloodstream()
	{
		this.hasNaquadah = false;
	}
	
	public void copyFrom(BloodstreamNaquadah source)
	{
		this.hasNaquadah = source.hasNaquadah;
	}
	
	public void saveData(CompoundTag tag)
	{
		tag.putBoolean("HasNaquadah", hasNaquadah);
	}
	
	public void loadData(CompoundTag tag)
	{
		this.hasNaquadah = tag.getBoolean("HasNaquadah");
	}
}
