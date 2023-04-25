package net.povstalec.sgjourney.common.capabilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;

public abstract class ZPMEnergyProvider implements ICapabilityProvider
{
	private static final String ENERGY = "Energy";
	private static final String ENTROPY = "Entropy";
	public static final int maxEntropy = 1000;
	
	private ItemStack stack;
	
	
	public ZPMEnergyProvider(ItemStack stack)
	{
		this.stack = stack;
	}
	
	private final ZeroPointEnergy ENERGY_STORAGE = new ZeroPointEnergy(maxEntropy, this.capacity(), this.maxReceive(), this.maxExtract())
	{
	    public long receiveLongEnergy(long maxReceive, boolean simulate)
	    {
	    	loadEnergy();
	        return super.receiveLongEnergy(maxReceive, simulate);
	    }
	    
	    public long extractLongEnergy(long maxExtract, boolean simulate)
	    {
	    	loadEnergy();
	        return super.extractLongEnergy(maxExtract, simulate);
	    }
		
		public long getTrueEnergyStored()
		{
			loadEnergy();
			return this.energy;
			
		}
	    
		@Override
		public boolean canExtract()
		{
			return canExtractEnergy();
		}
		
		@Override
		public boolean canReceive()
		{
			return canReceiveEnergy();
		}

		@Override
		public void onEnergyChanged(long difference, boolean simulate)
		{
			energyChanged(difference, simulate);
		}
	};
	
	private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.of(() -> ENERGY_STORAGE);
	
	public long capacity()
	{
		return getMaxEnergy();
	}
	
	public long maxReceive()
	{
		return 0;
	}
	
	public long maxExtract()
	{
		return getMaxExtract();
	}
	
	public boolean canReceiveEnergy()
	{
		return false;
	}
	
	public boolean canExtractEnergy()
	{
		return true;
	}
	
	public void energyChanged(long difference, boolean simulate)
	{
		saveEnergy();
	}
	
	public long getEnergy()
	{
		return ENERGY_STORAGE.getTrueEnergyStored();
	}
	
	public static long getMaxEnergy()
	{
		return CommonZPMConfig.zpm_energy_per_level_of_entropy.get();
	}
	
	public static long getMaxExtract()
	{
		return CommonZPMConfig.zpm_energy_per_level_of_entropy.get();
	}
	
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.ENERGY)
			return lazyEnergyHandler.cast();
		return LazyOptional.empty();
	}
	
	public void loadEnergy()
	{
		CompoundTag tag = stack.getOrCreateTag();
		if(tag.contains(ENERGY, Tag.TAG_LONG))
			ENERGY_STORAGE.deserializeNBT(tag.get(ENERGY));
		if(tag.contains(ENTROPY, Tag.TAG_INT))
			this.ENERGY_STORAGE.deserializeEntropy(tag.get(ENTROPY));
	}
	
	public void saveEnergy()
	{
		CompoundTag tag = stack.getOrCreateTag();
		tag.put(ENERGY, ENERGY_STORAGE.serializeNBT());
		tag.put(ENTROPY, ENERGY_STORAGE.serializeEntropy());
		stack.setTag(tag);
	}
}
