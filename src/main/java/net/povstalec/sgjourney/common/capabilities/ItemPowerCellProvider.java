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
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandlerItem;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.items.PowerCellItem;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.annotation.Nonnull;

public abstract class ItemPowerCellProvider extends ItemFluidHolderProvider
{
	public static final String ENERGY_BUFFER = "energy";
	
	public ItemPowerCellProvider(ItemStack stack)
	{
		super(stack);
	}
	
	@Override
	public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == ForgeCapabilities.ENERGY)
			return lazyEnergyHandler.cast();
		
		return super.getCapability(cap, side);
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	public abstract long energyCapacity();
	
	public abstract long energyMaxTransfer();
	
	private final ItemEnergy ENERGY_STORAGE = new ItemEnergy(energyCapacity(), energyMaxTransfer(), energyMaxTransfer())
	{
		public void reloadEnergy()
		{
			loadEnergy();
		}
		
		@Override
		public long getTrueEnergyStored()
		{
			FluidStack fluidStack = getFluidInTank(0);
			
			if(fluidStack.getFluid() == FluidInit.LIQUID_NAQUADAH_SOURCE.get())
				return fluidStack.getAmount() * CommonTechConfig.energy_from_liquid_naquadah.get() + this.energy;
			else if(fluidStack.getFluid() == FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get())
				return fluidStack.getAmount() * CommonTechConfig.energy_from_heavy_liquid_naquadah.get() + this.energy;
			else
				return this.energy;
		}
		
		@Override
		public void onEnergyChanged(long difference, boolean simulate)
		{
			saveEnergy();
		}
		
		@Override
		public long receiveLongEnergy(long maxReceive, boolean simulate)
		{
			return 0;
		}
		
		@Override
		public long extractLongEnergy(long maxExtract, boolean simulate)
		{
			long currentEnergy = getTrueEnergyStored();
			if(currentEnergy >= maxExtract)
				return super.extractLongEnergy(maxExtract, simulate);
			
			long convertedEnergy = convertLiquidNaquadahToEnergy(maxExtract, simulate);
			if(convertedEnergy <= 0)
				return super.extractLongEnergy(maxExtract, simulate);
			currentEnergy += convertedEnergy;
			
			long extractedEnergy = Math.min(maxExtract, currentEnergy);
			long energyLeft = currentEnergy - extractedEnergy;
			
			if(!simulate)
				this.setEnergy(Math.min(getTrueMaxEnergyStored(), energyLeft));
			
			return extractedEnergy;
		}
	};
	
	private long convertFluidToEnergy(long maxExtract, long energyFromFluid, boolean simulate)
	{
		long drained = maxExtract / energyFromFluid;
		
		int toDrain = (int) Math.min(((PowerCellItem) stack.getItem()).getFluidAmount(stack), drained);
		
		deplete(toDrain, simulate ? FluidAction.SIMULATE : FluidAction.EXECUTE);
		return toDrain * energyFromFluid;
	}
	
	public long convertLiquidNaquadahToEnergy(long maxExtract, boolean simulate)
	{
		FluidStack fluidStack = getFluidInTank(0);
		if(fluidStack.getAmount() <= 0)
			return 0;
		
		if(fluidStack.getFluid() == FluidInit.LIQUID_NAQUADAH_SOURCE.get())
			return convertFluidToEnergy(maxExtract, CommonTechConfig.energy_from_liquid_naquadah.get(), simulate);
		else if(fluidStack.getFluid() == FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get())
			return convertFluidToEnergy(maxExtract, CommonTechConfig.energy_from_heavy_liquid_naquadah.get(), simulate);
		else
			return 0;
	}
	
	private final LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.of(() -> ENERGY_STORAGE);
	
	public void loadEnergy()
	{
		if(!stack.hasTag())
			return;
		
		CompoundTag tag = stack.getTag();
		if(tag.contains(ENERGY_BUFFER, Tag.TAG_LONG))
			ENERGY_STORAGE.deserializeNBT(tag.get(ENERGY_BUFFER));
	}
	
	public void saveEnergy()
	{
		if(ENERGY_STORAGE.getEnergyWithoutLoading()  > 0)
		{
			CompoundTag tag = stack.getOrCreateTag();
			tag.put(ENERGY_BUFFER, ENERGY_STORAGE.serializeNBT());
			stack.setTag(tag);
		}
		else if(stack.getTag() != null && stack.getTag().contains(ENERGY_BUFFER))
			stack.removeTagKey(ENERGY_BUFFER);
	}
}
