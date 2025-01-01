package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.entity.LivingEntity;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.AttachmentTypeInit;

public class BloodstreamNaquadah
{
	public static final String HAS_NAQUADAH = "has_naquadah";
	public static final String BLOODSTREAM_NAQUADAH = "bloodstream_naquadah";
	
	public static final EntityCapability<BloodstreamNaquadah, Void> BLOODSTREAM_NAQUADAH_CAPABILITY = EntityCapability.createVoid(
			StargateJourney.sgjourneyLocation(BLOODSTREAM_NAQUADAH), BloodstreamNaquadah.class);
	
	private LivingEntity entity;
	private boolean hasNaquadah;
	public BloodstreamNaquadah(LivingEntity entity)
	{
		this.entity = entity;
		this.hasNaquadah = this.entity.getData(AttachmentTypeInit.BLOODSTREAM_NAQUADAH);
	}
	
	public boolean hasNaquadahInBloodstream()
	{
		return this.hasNaquadah;
	}
	
	public void setBloodstreamNaquadah(boolean hasNaquadah)
	{
		this.hasNaquadah = hasNaquadah;
		this.entity.setData(AttachmentTypeInit.BLOODSTREAM_NAQUADAH, this.hasNaquadah);
	}
	
	public void addNaquadahToBloodstream()
	{
		setBloodstreamNaquadah(true);
	}
	
	public void removeNaquadahFromBloodstream()
	{
		setBloodstreamNaquadah(false);
	}
	
	public void copyFrom(BloodstreamNaquadah source)
	{
		setBloodstreamNaquadah(source.hasNaquadah);
	}
}
