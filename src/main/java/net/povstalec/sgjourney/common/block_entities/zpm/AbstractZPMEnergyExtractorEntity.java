package net.povstalec.sgjourney.common.block_entities.zpm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.jarjar.nio.util.Lazy;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.capabilities.ZeroPointEnergy;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.init.ItemInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class AbstractZPMEnergyExtractorEntity extends AbstractZPMHolderEntity
{
	public final ZeroPointEnergy zpmEnergy;
	protected Lazy<IEnergyStorage> lazyEnergyHandler;
	
	public AbstractZPMEnergyExtractorEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		this.zpmEnergy = createEnergyStorage();
		lazyEnergyHandler = Lazy.of(() -> zpmEnergy);
	}
	
	@Override
	public void loadAdditional(@NotNull CompoundTag nbt, @NotNull HolderLookup.Provider registries)
	{
		super.loadAdditional(nbt, registries);
		zpmEnergy.updateFromZPMItem(itemHandler.getStackInSlot(0));
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	@Nullable
	public ZeroPointEnergy getEnergyHandler(Direction direction)
	{
		if(isCorrectEnergySide(direction))
			return zpmEnergy;
		
		return null;
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
			BlockPos otherPos = worldPosition.relative(outputDirection);
			BlockEntity blockEntity = level.getBlockEntity(otherPos);
			
			if(blockEntity == null)
				return;
			
			IEnergyStorage otherEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK, otherPos, outputDirection.getOpposite());
			
			if(otherEnergy instanceof SGJourneyEnergy sgjourneyEnergy)
			{
				long simulatedOutputAmount = zpmEnergy.extractLongEnergy(getMaxEnergyExtract(), true);
				long simulatedReceiveAmount = sgjourneyEnergy.receiveZeroPointEnergy(simulatedOutputAmount, true);
				zpmEnergy.extractLongEnergy(simulatedReceiveAmount, false);
				sgjourneyEnergy.receiveZeroPointEnergy(simulatedReceiveAmount, false);
			}
			else if(CommonZPMConfig.other_mods_use_zero_point_energy.get() && otherEnergy != null)
			{
				int simulatedOutputAmount = zpmEnergy.extractEnergy(SGJourneyEnergy.regularEnergy(getMaxEnergyExtract()), true);
				int simulatedReceiveAmount = otherEnergy.receiveEnergy(simulatedOutputAmount, true);
				
				zpmEnergy.extractLongEnergy(simulatedReceiveAmount, false);
				otherEnergy.receiveEnergy(simulatedReceiveAmount, false);
			}
		}
	}
}
