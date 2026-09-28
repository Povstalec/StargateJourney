package net.povstalec.sgjourney.common.capabilities;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.capabilities.ICapabilityProvider;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.povstalec.sgjourney.common.config.SyncedConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class ZPMEnergyProvider implements ICapabilityProvider
{
	private static final String ENERGY = "Energy";
	private static final String ENTROPY = "Entropy";
	
	private final ItemStack zpmStack;
	public final ZeroPointEnergy zeroPointEnergy;
	private final LazyOptional<IEnergyStorage> lazyEnergyHandler;
	
	public ZPMEnergyProvider(ItemStack zpmStack)
	{
		this.zpmStack = zpmStack;
		this.zeroPointEnergy = createZeroPointEnergy(zpmStack);
		this.lazyEnergyHandler = LazyOptional.of(() -> this.zeroPointEnergy);
	}
	
	public ZeroPointEnergy createZeroPointEnergy(ItemStack zpmStack)
	{
		return new ZeroPointEnergy(zpmStack)
		{
			@Override
			public long receiveLongEnergy(long maxReceive, boolean simulate)
			{
				loadEnergy();
				return super.receiveLongEnergy(maxReceive, simulate);
			}
			
			@Override
			public long depleteEnergy(long maxExtract, boolean simulate)
			{
				loadEnergy();
				return super.depleteEnergy(maxExtract, simulate);
			}
			
			@Override
			public long getTrueEnergyStored()
			{
				loadEnergy();
				return this.energy;
				
			}
			
			@Override
			public void onEnergyChanged(long difference, boolean simulate)
			{
				energyChanged(difference, simulate);
			}
		};
	}
	
	public void energyChanged(long difference, boolean simulate)
	{
		saveEnergy();
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
		CompoundTag tag = zpmStack.getOrCreateTag();
		if(tag.contains(ENERGY, Tag.TAG_LONG))
			zeroPointEnergy.deserializeNBT(tag.get(ENERGY));
		if(tag.contains(ENTROPY, Tag.TAG_INT))
			this.zeroPointEnergy.deserializeEntropy(tag.get(ENTROPY));
	}
	
	public void saveEnergy()
	{
		CompoundTag tag = zpmStack.getOrCreateTag();
		tag.put(ENERGY, zeroPointEnergy.serializeNBT());
		tag.put(ENTROPY, zeroPointEnergy.serializeEntropy());
		zpmStack.setTag(tag);
	}
}
