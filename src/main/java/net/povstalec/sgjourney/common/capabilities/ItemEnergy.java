package net.povstalec.sgjourney.common.capabilities;

public abstract class ItemEnergy extends SGJourneyEnergy
{
	public ItemEnergy(long capacity, long maxReceive, long maxExtract)
	{
		super(capacity, maxReceive, maxExtract);
	}
	
	public abstract void reloadEnergy();
	
	public long getEnergyWithoutLoading()
	{
		return energy;
	}
	
	@Override
	public long getTrueEnergyStored()
	{
		reloadEnergy();
		return this.energy;
	}
	
	@Override
	public long receiveLongEnergy(long maxReceive, boolean simulate)
	{
		reloadEnergy();
		return super.receiveLongEnergy(maxReceive, simulate);
	}
	
	@Override
	public long extractLongEnergy(long maxExtract, boolean simulate)
	{
		reloadEnergy();
		return super.extractLongEnergy(maxExtract, simulate);
	}
}
