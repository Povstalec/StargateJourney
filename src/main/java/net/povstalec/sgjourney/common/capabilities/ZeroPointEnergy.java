package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.init.DataComponentInit;

public abstract class ZeroPointEnergy extends SGJourneyEnergy
{
	public static final int MAX_ENTROPY = 1000;
	public static final long ENERGY_PER_ENTROPY_LEVEL = CommonZPMConfig.zpm_energy_per_level_of_entropy.get();
	
	protected int maxEntropy;
	protected int entropy;
	
	public ZeroPointEnergy(int maxEntropy, long capacity, long maxReceive, long maxExtract)
	{
		super(capacity, maxReceive, maxExtract);
		this.maxEntropy = maxEntropy;
		this.energy = capacity;
	}
	
	@Override
    public long receiveLongEnergy(long maxReceive, boolean simulate)
    {
        return 0;
    }
	
	@Override
	public long extractLongEnergy(long maxExtract, boolean simulate)
	{
		if(!canExtract() || entropy >= MAX_ENTROPY)
            return 0;
		
		long energy = this.energy;
		int entropy = this.entropy;
		long energyExtracted = 0;
		
		// Subtract energy from extract until we reach something we can take care of in a single level or run out of energy
		while(maxExtract >= energy && entropy < MAX_ENTROPY)
		{
			maxExtract -= energy;
			energyExtracted += energy;
			energy = ENERGY_PER_ENTROPY_LEVEL;
			entropy++;
		}
		
		// ZPM no longer has energy
		if(entropy >= MAX_ENTROPY)
		{
			if(!simulate)
			{
				this.energy = 0;
				this.entropy = entropy;
			}
			
			if(energyExtracted != 0)
				onEnergyChanged(energyExtracted, simulate);
			
			return energyExtracted;
		}
		
		energy -= maxExtract;
		energyExtracted += maxExtract;
		
		if(!simulate)
		{
			this.energy = energy;
			this.entropy = entropy;
		}
		
		if(energyExtracted != 0)
			onEnergyChanged(energyExtracted, simulate);
		
		return energyExtracted;
	}
	
	@Override
	public int extractEnergy(int maxExtract, boolean simulate)
	{
		return CommonZPMConfig.other_mods_use_zero_point_energy.get() ? regularEnergy(extractLongEnergy(maxExtract, simulate)) : 0;
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
	
	public static String zeroPointEnergyToString(int entropy, long levelEnergy)
	{
		if (entropy >= MAX_ENTROPY - 1)
			return SGJourneyEnergy.energyToString(levelEnergy);
		
		double decimals = (double) levelEnergy / ENERGY_PER_ENTROPY_LEVEL;
		double total = (MAX_ENTROPY - entropy - 1 + decimals) * ENERGY_PER_ENTROPY_LEVEL;
		
		int prefix = -1;
		for (; total >= 1000 && prefix < PREFIXES.length; prefix++)
		{
			total /= 1000;
		}
		
		total *= 100;
		total = Math.round(total);
		total /= 100;
		
		return total + " " + PREFIXES[prefix] + "FE";
	}
	
	
	
	public static class Item extends ZeroPointEnergy
	{
		protected ItemStack stack;
		
		public Item(ItemStack stack, int maxEntropy, long capacity, long maxReceive, long maxExtract)
		{
			super(maxEntropy, capacity, maxReceive, maxExtract);
			
			this.stack = stack;
			
			this.entropy = stack.getOrDefault(DataComponentInit.ENTROPY, MAX_ENTROPY);
			this.energy = stack.getOrDefault(DataComponentInit.ENERGY, 0L); // TODO Fix these
		}
		
		@Override
		public void onEnergyChanged(long difference, boolean simulate)
		{
			stack.set(DataComponentInit.ENTROPY, this.entropy);
			stack.set(DataComponentInit.ENERGY, this.energy);
		}
	}
}
