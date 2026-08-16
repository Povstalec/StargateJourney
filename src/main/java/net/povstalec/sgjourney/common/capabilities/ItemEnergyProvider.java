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

public abstract class ItemEnergyProvider implements ICapabilityProvider
{
	private static final String ENERGY = "Energy";
	
	private ItemStack stack;
	
	
	public ItemEnergyProvider(ItemStack stack)
	{
		this.stack = stack;
	}
	
	private final ItemEnergy ENERGY_STORAGE = new ItemEnergy(this.capacity(), this.maxReceive(), this.maxExtract())
	{
		public void reloadEnergy()
		{
			loadEnergy();
		}
		
		@Override
		public boolean canReceive()
		{
			return super.canReceive() && canReceiveEnergy();
		}

		@Override
		public void onEnergyChanged(long difference, boolean simulate)
		{
			energyChanged(difference, simulate);
		}
	};
	
	private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.of(() -> ENERGY_STORAGE);
	
	public abstract long capacity();
	
	public abstract long maxReceive();
	
	public abstract long maxExtract();
	
	public boolean canReceiveEnergy()
	{
		return stack.getCount() == 1;
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
		if(!stack.hasTag())
			return;
		
		CompoundTag tag = stack.getTag();
		if(tag.contains(ENERGY, Tag.TAG_LONG))
			ENERGY_STORAGE.deserializeNBT(tag.get(ENERGY));
	}
	
	public void saveEnergy()
	{
		if(ENERGY_STORAGE.getEnergyWithoutLoading()  > 0)
		{
			CompoundTag tag = stack.getOrCreateTag();
			tag.put(ENERGY, ENERGY_STORAGE.serializeNBT());
			stack.setTag(tag);
		}
		else if(stack.getTag() != null && stack.getTag().contains(ENERGY))
			stack.removeTagKey(ENERGY);
	}
}
