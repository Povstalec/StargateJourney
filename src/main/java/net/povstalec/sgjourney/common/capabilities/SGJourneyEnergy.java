package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.energy.EnergyStorage;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;

public abstract class SGJourneyEnergy extends EnergyStorage
{
	protected long energy;
    protected long capacity;
    protected long maxReceive;
    protected long maxExtract;
	
	public SGJourneyEnergy(long capacity, long maxReceive, long maxExtract)
	{
		super(regularEnergy(capacity), regularEnergy(maxReceive), regularEnergy(maxExtract));

		this.energy = 0;
		this.capacity = capacity;
		this.maxReceive = maxReceive;
		this.maxExtract = maxExtract;
	}

    @Override
	public int receiveEnergy(int maxReceive, boolean simulate)
	{
    	return regularEnergy(receiveLongEnergy(maxReceive, simulate));
	}
    
    public long receiveLongEnergy(long maxReceive, boolean simulate)
    {
        if(!canReceive())
            return 0;
        long energyReceived = Math.min(capacity - energy, Math.min(this.maxReceive, maxReceive));
        if(!simulate)
        	energy += energyReceived;

        if(energyReceived != 0)
			onEnergyChanged(energyReceived, simulate);
        return energyReceived;
    }
	
	public long receiveZeroPointEnergy(long maxReceive, boolean simulate)
	{
		return receiveLongEnergy(maxReceive, simulate);
	}
	
	@Override
    public int extractEnergy(int maxExtract, boolean simulate)
    {
		return CommonZPMConfig.other_mods_use_zero_point_energy.get() ? regularEnergy(extractLongEnergy(maxExtract, simulate)) : 0;
    }
	
	public long extractLongEnergy(long maxExtract, boolean simulate)
	{
		if(!canExtract())
            return 0;
		
		long energyExtracted = Math.min(energy, Math.min(this.maxExtract, maxExtract));
        if(!simulate)
        	energy -= energyExtracted;
        
        if(energyExtracted != 0)
			onEnergyChanged(energyExtracted, simulate);
        
        return energyExtracted;
	}
	
	@Override
    public int getEnergyStored()
    {
        return regularEnergy(getTrueEnergyStored());
    }
	
	public long getTrueEnergyStored()
	{
		return this.energy;
	}

    @Override
    public int getMaxEnergyStored()
    {
        return regularEnergy(getTrueMaxEnergyStored());
    }
    
    public long getTrueMaxEnergyStored()
    {
        return capacity;
    }

    @Override
    public boolean canExtract()
    {
        return this.maxExtract > 0;
    }

    @Override
    public boolean canReceive()
    {
        return this.maxReceive > 0 && this.energy < this.capacity;
    }
    
    public boolean canReceive(long receivedEnergy)
	{
		return energy + receivedEnergy <= capacity;
	}
    
    
	
	public long setEnergy(long energy)
	{
		this.energy = energy;
		
		return energy;
	}
	
	public abstract void onEnergyChanged(long difference, boolean simulate);
	
	public long maxReceive()
	{
		return this.maxReceive;
	}
	
	public long maxExtract()
	{
		return this.maxExtract;
	}

    @Override
    public Tag serializeNBT()
    {
        return LongTag.valueOf(this.energy);
    }

    @Override
    public void deserializeNBT(Tag nbt)
    {
    	if(!(nbt instanceof LongTag longTag))
    		throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
    	
    	this.setEnergy(longTag.getAsLong());
    }
    
    public static int regularEnergy(long energy)
    {
    	return (int) Math.min(Integer.MAX_VALUE, energy);
    }
}
