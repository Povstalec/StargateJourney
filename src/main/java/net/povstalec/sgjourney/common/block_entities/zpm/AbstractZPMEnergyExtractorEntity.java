package net.povstalec.sgjourney.common.block_entities.zpm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.capabilities.ZeroPointEnergy;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.init.ItemInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class AbstractZPMEnergyExtractorEntity extends AbstractZPMHolderEntity
{
	public final ZeroPointEnergy zpmEnergy;
	protected LazyOptional<IEnergyStorage> lazyEnergyHandler;
	
	public AbstractZPMEnergyExtractorEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		this.zpmEnergy = createEnergyStorage();
		this.lazyEnergyHandler = LazyOptional.empty();
	}
	
	@Override
	public void onLoad()
	{
		lazyEnergyHandler = LazyOptional.of(() -> zpmEnergy);
		super.onLoad();
	}
	
	@Override
	public void invalidateCaps()
	{
		lazyEnergyHandler.invalidate();
		super.invalidateCaps();
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		zpmEnergy.updateFromZPMItem(itemHandler.getStackInSlot(0));
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side)
	{
		if(capability == ForgeCapabilities.ENERGY && isCorrectEnergySide(side))
			return lazyEnergyHandler.cast();
		
		return super.getCapability(capability, side);
	}
	
	//============================================================================================
	//******************************************Storage*******************************************
	//============================================================================================
	
	@Override
	public void onSlotContentsChanged(int slot)
	{
		zpmEnergy.updateFromZPMItem(itemHandler.getStackInSlot(0));
		setChanged();
		updateClient();
		
		super.onSlotContentsChanged(slot);
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	/**
	 * @param side Direction from which the Block Entity is being accessed
	 * @return True if the direction is a valid one for accessing energy, otherwise false
	 */
	public boolean isCorrectEnergySide(Direction side)
	{
		return true;
	}
	
	/**
	 * @return The maximum amount of energy that can be extracted from this Block Entity in a single tick by other Energy Storages
	 */
	public abstract long getMaxEnergyExtract();
	
	public void energyChanged(long difference, boolean simulate)
	{
		if(!simulate)
		{
			setChanged();
			if(difference != 0)
				updateClient();
		}
	}
	
	public ZeroPointEnergy createEnergyStorage()
	{
		return new ZeroPointEnergy(itemHandler.getStackInSlot(0), getMaxEnergyExtract())
		{
			@Override
			public long receiveLongEnergy(long maxReceive, boolean simulate)
			{
				updateFromZPMItem(itemHandler.getStackInSlot(0));
				return super.receiveLongEnergy(maxReceive, simulate);
			}
			
			@Override
			public long depleteEnergy(long maxExtract, boolean simulate)
			{
				updateFromZPMItem(itemHandler.getStackInSlot(0));
				return super.depleteEnergy(maxExtract, simulate);
			}
			
			@Override
			public long getTrueEnergyStored()
			{
				updateFromZPMItem(itemHandler.getStackInSlot(0));
				return this.energy;
				
			}
			
			@Override
			public void onEnergyChanged(long difference, boolean simulate)
			{
				updateZPMItem(itemHandler.getStackInSlot(0));
				energyChanged(difference, simulate);
			}
		};
	}
	
	public void outputEnergy(Direction outputDirection)
	{
		ItemStack stack = itemHandler.getStackInSlot(0);
		
		if(stack.is(ItemInit.ZPM.get()))
		{
			BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(outputDirection));
			
			if(blockEntity == null)
				return;
			
			blockEntity.getCapability(ForgeCapabilities.ENERGY, outputDirection.getOpposite()).ifPresent(otherEnergy ->
			{
				if(otherEnergy instanceof SGJourneyEnergy sgjourneyEnergy)
				{
					long simulatedOutputAmount = zpmEnergy.extractLongEnergy(getMaxEnergyExtract(), true);
					long simulatedReceiveAmount = sgjourneyEnergy.receiveZeroPointEnergy(simulatedOutputAmount, true);
					zpmEnergy.extractLongEnergy(simulatedReceiveAmount, false);
					sgjourneyEnergy.receiveZeroPointEnergy(simulatedReceiveAmount, false);
				}
				else if(CommonZPMConfig.other_mods_use_zero_point_energy.get())
				{
					int simulatedOutputAmount = zpmEnergy.extractEnergy(SGJourneyEnergy.regularEnergy(getMaxEnergyExtract()), true);
					int simulatedReceiveAmount = otherEnergy.receiveEnergy(simulatedOutputAmount, true);
					
					zpmEnergy.extractLongEnergy(simulatedReceiveAmount, false);
					otherEnergy.receiveEnergy(simulatedReceiveAmount, false);
				}
			});
		}
	}
}
