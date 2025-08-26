package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.nbt.CompoundTag;

public class BloodstreamNaquadah
{
	public static final String BLOODSTREAM_NAQUADAH = "bloodstream_naquadah";
	
	private boolean naquadahInBloodstream = false;
	
	public boolean hasNaquadahInBloodstream()
	{
		return this.naquadahInBloodstream;
	}
	
	public void addNaquadahToBloodstream()
	{
		this.naquadahInBloodstream = true;
	}
	
	public void removeNaquadahFromBloodstream()
	{
		this.naquadahInBloodstream = false;
	}
	
	public void copyFrom(BloodstreamNaquadah source)
	{
		this.naquadahInBloodstream = source.naquadahInBloodstream;
	}
	
	
	public void saveData(CompoundTag tag)
	{
		tag.putBoolean(BLOODSTREAM_NAQUADAH, naquadahInBloodstream);
	}
	
	public void loadData(CompoundTag tag)
	{
		this.naquadahInBloodstream = tag.getBoolean(BLOODSTREAM_NAQUADAH);
	}
}
