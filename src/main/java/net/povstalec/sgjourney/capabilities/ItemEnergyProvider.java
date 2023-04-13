package net.povstalec.sgjourney.capabilities;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import net.minecraft.core.Direction;
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
	
	private final SGJourneyEnergy ENERGY_STORAGE = new SGJourneyEnergy(this.capacity(), this.maxTransfer(), this.maxTransfer())
	{
		@Override
		public boolean canExtract()
		{
			return true;
		}
		
		@Override
		public boolean canReceive()
		{
			return true;
		}

		@Override
		public void onEnergyChanged(long difference, boolean simulate)
		{
			if(!simulate)
				stack.getOrCreateTag().putInt(ENERGY, this.getEnergyStored());
		}
	};
	
	private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.of(() -> ENERGY_STORAGE);
	
	public abstract int capacity();
	
	public abstract int maxTransfer();
	
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.ENERGY)
			return lazyEnergyHandler.cast();
		return LazyOptional.empty();
	}
}
