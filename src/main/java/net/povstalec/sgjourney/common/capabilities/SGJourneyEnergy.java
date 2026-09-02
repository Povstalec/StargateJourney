package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.LongTag;
import net.minecraft.nbt.Tag;
import net.minecraftforge.common.util.INBTSerializable;
import net.minecraftforge.energy.IEnergyStorage;

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
		
		return depleteEnergy(Math.min(maxExtract(), maxExtract), simulate);
	}
	
	/**
	 * Alternative to {@link #extractLongEnergy(long maxExtract, boolean simulate)} meant to be used in places where it is unacceptable for pull-based energy systems to extract energy.
	 * Unlike the aforementioned method, this one ignores {@link #canExtract()} and {@link #maxExtract()}, allowing it to act for systems that normally have maxExtract set to 0
	 * @param maxExtract Maximum amount of energy to be extracted
	 * @param simulate Whether to simulate the extraction or not
	 * @return Energy that was actually extracted
	 */
	public long depleteEnergy(long maxExtract, boolean simulate)
	{
		long energyExtracted = Math.min(energy, maxExtract);
		if(!simulate)
			energy -= energyExtracted;
		
		if(energyExtracted != 0)
			onEnergyChanged(energyExtracted, simulate);
		
		return energyExtracted;
	}
	
	/**
	 * @return Energy currently stored by this Energy Storage, but limited by max int value
	 */
	@Override
    public int getEnergyStored()
    {
        return regularEnergy(getTrueEnergyStored());
    }
	
	/**
	 * @return Real energy currently stored by this Energy Storage, not limited by max int value
	 */
	public long getTrueEnergyStored()
	{
		return this.energy;
	}
	
	/**
	 * @return Capacity of this Energy Storage, but limited by max int value
	 */
    @Override
    public int getMaxEnergyStored()
    {
        return regularEnergy(getTrueMaxEnergyStored());
    }
	
	/**
	 * @return Real capacity of this Energy Storage, not limited by max int value
	 */
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
        return maxReceive() > 0;
    }
    
    public boolean canReceive(long receivedEnergy)
	{
		return energy + receivedEnergy <= getTrueMaxEnergyStored();
	}
	
	public boolean hasEnergy(long energy)
	{
		return getTrueEnergyStored() >= energy;
	}
	
	public boolean hasEnergy()
	{
		return this.energy > 0;
	}
    
    
	
	public long setEnergy(long energy)
	{
		this.energy = energy;
		
		return energy;
	}
	
	public abstract void onEnergyChanged(long difference, boolean simulate);
	
	/**
	 * @return The maximum amount of energy this Energy Storage can receive in a single tick from other Energy Storages
	 */
	public long maxReceive()
	{
		return this.maxReceive;
	}
	
	/**
	 * @return The maximum amount of energy that can be extracted from this Energy Storage in a single tick by other Energy Storages
	 */
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
		if(nbt instanceof LongTag longTag)
			this.setEnergy(longTag.getAsLong());
		else if(nbt instanceof IntTag intTag)
			this.setEnergy(intTag.getAsInt());
		else
			throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
	}
	
	
	
	public void drainSimpleEnergyStorage(IEnergyStorage otherEnergyStorage, int amount)
	{
		int simulatedOutputAmount = otherEnergyStorage.extractEnergy(amount, true);
		int simulatedReceiveAmount = this.receiveEnergy(simulatedOutputAmount, true);
		
		otherEnergyStorage.extractEnergy(simulatedReceiveAmount, false);
		this.receiveEnergy(simulatedReceiveAmount, false);
	}
	
	public void drainOtherEnergyStorage(IEnergyStorage otherEnergyStorage, long amount)
	{
		if(otherEnergyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
			sgjourneyEnergy.fillSGJourneyEnergyStorage(this, amount);
		else
			this.drainSimpleEnergyStorage(otherEnergyStorage, SGJourneyEnergy.regularEnergy(amount));
	}
	
	public void fillSGJourneyEnergyStorage(SGJourneyEnergy sgjourneyEnergy, long amount)
	{
		long simulatedOutputAmount = this.extractLongEnergy(amount, true);
		long simulatedReceiveAmount = sgjourneyEnergy.receiveLongEnergy(simulatedOutputAmount, true);
		
		this.extractLongEnergy(simulatedReceiveAmount, false);
		sgjourneyEnergy.receiveLongEnergy(simulatedReceiveAmount, false);
	}
	
	public void fillSimpleEnergyStorage(IEnergyStorage otherEnergyStorage, int amount)
	{
		int simulatedOutputAmount = this.extractEnergy(amount, true);
		int simulatedReceiveAmount = otherEnergyStorage.receiveEnergy(simulatedOutputAmount, true);
		
		this.extractEnergy(simulatedReceiveAmount, false);
		otherEnergyStorage.receiveEnergy(simulatedReceiveAmount, false);
	}
	
	public void fillOtherEnergyStorage(IEnergyStorage otherEnergyStorage, long amount)
	{
		if(otherEnergyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
			this.fillSGJourneyEnergyStorage(sgjourneyEnergy, amount);
		else
			this.fillSimpleEnergyStorage(otherEnergyStorage, SGJourneyEnergy.regularEnergy(amount));
	}
    
 
	
	public static int regularEnergy(long energy)
    {
    	return (int) Math.min(Integer.MAX_VALUE, energy);
    }
	
	public static String energyToString(long energy)
	{
		if(energy < 0)
			return "NaN";
		
		if(energy < 1000)
			return energy + " FE";
		
		double total = energy;
		int prefix = -1;
		for(; total >= 1000 && prefix < PREFIXES.length; prefix++)
		{
			total /= 1000;
		}
		
		total *= 100;
		total = Math.floor(total);
		total /= 100;
		
		return total + " " + PREFIXES[prefix] + "FE";
	}
	
	public static String energyToString(long energy, long capacity)
	{
		return energyToString(energy) + "/" + energyToString(capacity);
	}
	
	public static long energyToTarget(long energyTarget, long energyStored, long maxExtract)
	{
		long needed = energyTarget - energyStored;
		
		if(needed < 0)
			return 0;
		
		return Math.min(needed, maxExtract);
	}
}
