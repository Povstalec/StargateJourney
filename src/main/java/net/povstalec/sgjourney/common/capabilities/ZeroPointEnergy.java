package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.nbt.IntTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.energy.IEnergyStorage;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.config.SyncedConfig;
import net.povstalec.sgjourney.common.items.ZeroPointModule;

public abstract class ZeroPointEnergy extends SGJourneyEnergy
{
	protected int entropy = 0;
	
	public ZeroPointEnergy(ItemStack zpmStack, long maxExtract)
	{
		super(SyncedConfig.zpm_energy_per_entropy_level.get(), 0, maxExtract);
		updateFromZPMItem(zpmStack);
	}
	
	public ZeroPointEnergy(ItemStack zpmStack)
	{
		this(zpmStack, SyncedConfig.zpm_energy_per_entropy_level.get());
	}
	
	public void updateFromZPMItem(ItemStack zpmStack)
	{
		this.entropy = ZeroPointModule.getEntropy(zpmStack);
		this.energy = ZeroPointModule.getEnergy(zpmStack);
	}
	
	public void updateZPMItem(ItemStack zpmStack)
	{
		ZeroPointModule.setEntropy(zpmStack, entropy);
		ZeroPointModule.setEnergy(zpmStack, energy);
	}
	
	@Override
    public long receiveLongEnergy(long maxReceive, boolean simulate)
    {
        return 0;
    }
	
	@Override
	public long depleteEnergy(long maxExtract, boolean simulate)
	{
		if(!canExtract() || entropy >= SyncedConfig.zpm_max_entropy.get())
            return 0;
		
		long energy = this.energy;
		int entropy = this.entropy;
		long energyExtracted = 0;
		
		// Subtract energy from extract until we reach something we can take care of in a single level or run out of energy
		while(maxExtract >= energy && entropy < SyncedConfig.zpm_max_entropy.get())
		{
			maxExtract -= energy;
			energyExtracted += energy;
			energy = SyncedConfig.zpm_energy_per_entropy_level.get();
			entropy++;
		}
		
		// ZPM no longer has energy
		if(entropy >= SyncedConfig.zpm_max_entropy.get())
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
		return this.entropy >= SyncedConfig.zpm_max_entropy.get() - 1;
	}
    
    public Tag serializeEntropy()
    {
        return IntTag.valueOf(this.entropy);
    }
    
    public void deserializeEntropy(Tag nbt)
    {
    	if(nbt instanceof IntTag intTag)
			this.setEntropy(intTag.getAsInt());
		else
			throw new IllegalArgumentException("Can not deserialize to an instance that isn't the default implementation");
    }
	
	@Override
	public void fillSGJourneyEnergyStorage(SGJourneyEnergy sgjourneyEnergy, long amount)
	{
		long simulatedOutputAmount = this.extractLongEnergy(amount, true);
		long simulatedReceiveAmount = sgjourneyEnergy.receiveZeroPointEnergy(simulatedOutputAmount, true);
		
		this.extractLongEnergy(simulatedReceiveAmount, false);
		sgjourneyEnergy.receiveZeroPointEnergy(simulatedReceiveAmount, false);
	}
	
	@Override
	public void fillOtherEnergyStorage(IEnergyStorage otherEnergyStorage, long amount)
	{
		if(!otherEnergyStorage.canReceive())
			return;
		
		if(otherEnergyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
			fillSGJourneyEnergyStorage(sgjourneyEnergy, amount);
		else
		{
			int simulatedOutputAmount = this.extractEnergy(SGJourneyEnergy.regularEnergy(amount), true);
			int simulatedReceiveAmount = otherEnergyStorage.receiveEnergy(simulatedOutputAmount, true);
			
			this.extractEnergy(simulatedReceiveAmount, false);
			otherEnergyStorage.receiveEnergy(simulatedReceiveAmount, false);
		}
	}
	
	
	
	public static String zeroPointEnergyToString(int entropy, long levelEnergy)
	{
		if(entropy >= SyncedConfig.zpm_max_entropy.get() - 1)
			return SGJourneyEnergy.energyToString(levelEnergy);
		
		double decimals = (double) levelEnergy / SyncedConfig.zpm_energy_per_entropy_level.get();
		double total = (SyncedConfig.zpm_max_entropy.get() - entropy - 1  + decimals) * SyncedConfig.zpm_energy_per_entropy_level.get();
		
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
}
