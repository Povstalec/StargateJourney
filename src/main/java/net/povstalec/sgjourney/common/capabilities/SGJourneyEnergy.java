package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.EnergyStorage;
import net.minecraftforge.energy.IEnergyStorage;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;

public abstract class SGJourneyEnergy implements IEnergyStorage, INBTSerializable<Tag>
{
	public static final char[] PREFIXES = {'k', 'M', 'G', 'T', 'P', 'E', 'Z', 'Y', 'R', 'Q'};
	
	protected long energy;
    protected long capacity;
    protected long maxReceive;
    protected long maxExtract;
	
	public SGJourneyEnergy(long capacity, long maxReceive, long maxExtract)
	{
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
		
        long energyReceived = Math.min(getTrueMaxEnergyStored() - energy, Math.min(maxReceive(), maxReceive));
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
		return regularEnergy(extractLongEnergy(maxExtract, simulate));
	}
	
	public long extractLongEnergy(long maxExtract, boolean simulate)
	{
		if(!canExtract())
            return 0;
		
		long energyExtracted = Math.min(energy, Math.min(maxExtract(), maxExtract));
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
        return maxExtract() > 0;
    }

    @Override
    public boolean canReceive()
    {
        return maxReceive() > 0 && this.energy < getTrueMaxEnergyStored();
    }
    
    public boolean canReceive(long receivedEnergy)
	{
		return energy + receivedEnergy <= getTrueMaxEnergyStored();
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
	
	public static String energyToString(long energy)
	{
		if(energy < 1000)
			return energy + " FE";
		
		double total = energy;
		int prefix = -1;
		for(; total >= 1000 && prefix < PREFIXES.length; prefix++)
		{
			total /= 1000;
		}
		
		total *= 100;
		total = Math.round(total);
		total /= 100;
		
		return total + " " + PREFIXES[prefix] + "FE";
	}
	
	public static String energyToString(long energy, long capacity)
	{
		return energyToString(energy) + "/" + energyToString(capacity);
	}
}
