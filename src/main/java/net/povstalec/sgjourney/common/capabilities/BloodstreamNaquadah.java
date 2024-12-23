package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.povstalec.sgjourney.StargateJourney;

public class BloodstreamNaquadah
{
	public static final String HAS_NAQUADAH = "has_naquadah";
	public static final String BLOODSTREAM_NAQUADAH = "bloodstream_naquadah";
	
	public static final EntityCapability<BloodstreamNaquadah, Void> BLOODSTREAM_NAQUADAH_CAPABILITY = EntityCapability.createVoid(
			StargateJourney.sgjourneyLocation(BLOODSTREAM_NAQUADAH), BloodstreamNaquadah.class);
	
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
		tag.putBoolean(HAS_NAQUADAH, hasNaquadah);
	}
	
	public void loadData(CompoundTag tag)
	{
		this.hasNaquadah = tag.getBoolean(HAS_NAQUADAH);
	}
}
