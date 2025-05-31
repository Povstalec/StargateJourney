package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.items.ZeroPointModule;

public abstract class ZeroPointEnergy extends SGJourneyEnergy
{
	protected int maxEntropy;
	protected int entropy;
	
	public ZeroPointEnergy(int maxEntropy, long capacity, long maxReceive, long maxExtract)
	{
		super(capacity, maxReceive, maxExtract);
		this.maxEntropy = maxEntropy;
		this.energy = capacity;
	}
    
    public long receiveLongEnergy(long maxReceive, boolean simulate)
    {
        return 0;
    }
	
	public long extractLongEnergy(long maxExtract, boolean simulate)
	{
		if(!canExtract())
            return 0;
		
		long energyExtracted = !isNearMaxEntropy() ? Math.min(this.maxExtract, maxExtract) : Math.min(this.energy, Math.min(this.maxExtract, maxExtract));

		if(!isNearMaxEntropy() && energyExtracted > this.energy)
		{
			long leftover = energyExtracted - this.energy;

			if(!simulate)
			{
				this.entropy++;
				this.energy = this.capacity - leftover;
			}
	        
	        if(energyExtracted != 0)
				onEnergyChanged(energyExtracted, simulate);
			
			return energyExtracted;
		}
		else
		{
			if(!simulate)
			{
	        	this.energy -= energyExtracted;
	        	if(this.energy <= 0 && this.entropy < 1000)
	        		this.entropy++;
			}
	        
	        if(energyExtracted != 0)
				onEnergyChanged(energyExtracted, simulate);
	        
	        return energyExtracted;
		}
	}
	
	@Override
    public int getEnergyStored()
    {
        return getRegularEnergy(getTrueEnergyStored());
    }
	
	public long getTrueEnergyStored()
	{
		return this.energy;
	}
	
	public int getEntropy()
	{
		return this.entropy;
	}
	
	public int setEntropy(int entropy)
	{
		this.entropy = entropy;
		
		return this.entropy;
	}
	
	public boolean isNearMaxEntropy()
	{
		return this.entropy >= this.maxEntropy - 1;
	}
    
    public Tag serializeEntropy()
    {
        return IntTag.valueOf(this.entropy);
    }
    
    public void deserializeEntropy(Tag nbt)
    {
    	if(!(nbt instanceof IntTag intTag))
    		throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
    	
    	this.setEntropy(intTag.getAsInt());
    }
	
	public static class Item extends ZeroPointEnergy
	{
		protected ItemStack stack;
		
		public Item(ItemStack stack, int maxEntropy, long capacity, long maxReceive, long maxExtract)
		{
			super(maxEntropy, capacity, maxReceive, maxExtract);
			
			this.stack = stack;
			
			this.entropy = stack.getOrDefault(DataComponentInit.ENTROPY, 0);
			this.energy = stack.getOrDefault(DataComponentInit.ENERGY, capacity);
		}
		
		@Override
		public void onEnergyChanged(long difference, boolean simulate)
		{
			stack.set(DataComponentInit.ENTROPY, this.entropy);
			stack.set(DataComponentInit.ENERGY, this.energy);
		}
	}
}
