package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;

import javax.annotation.Nullable;

public abstract class EnergyBlockEntity extends BlockEntity
{
	public static final String ENERGY = "energy";
	
	public final SGJourneyEnergy energyStorage;
	protected Lazy<IEnergyStorage> lazyEnergyHandler;
	
	public EnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
		this.energyStorage = createEnergyStorage();
		this.lazyEnergyHandler = Lazy.of(() -> energyStorage);
	}
	
	@Override
	public void invalidateCapabilities()
	{
		lazyEnergyHandler.invalidate();
		super.invalidateCapabilities();
	}
	
	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		energyStorage.setEnergy(tag.getLong(ENERGY));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		tag.putLong(ENERGY, energyStorage.getTrueEnergyStored());
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag(HolderLookup.Provider registries)
	{
		return this.saveWithoutMetadata(registries);
	}
	
	public void updateClient()
	{
		if(!level.isClientSide())
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	public SGJourneyEnergy getEnergyStorage()
	{
		return energyStorage;
	}
	
	@Nullable
	public IEnergyStorage getEnergyHandler(Direction side)
	{
		if(isCorrectEnergySide(side))
			return lazyEnergyHandler.get();
		
		return null;
	}
	
	//============================================================================================
	//****************************************Energy setup****************************************
	//============================================================================================
	
	/**
	 * @param side Direction from which the Block Entity is being accessed
	 * @return True if the direction is a valid one for accessing energy, otherwise false
	 */
	protected boolean isCorrectEnergySide(Direction side)
	{
		return true;
	}
	
	/**
	 * @return True if this Block Entity is capable of receiving energy from a Zero Point Module, otherwise false
	 */
	protected boolean canReceiveZeroPointEnergy()
	{
		return CommonZPMConfig.tech_uses_zero_point_energy.get();
	}
	
	/**
	 * @return The maximum amount of energy this Block Entity can hold inside at any given time
	 */
	protected abstract long getCapacity();
	
	/**
	 * @return The maximum amount of energy this Block Entity can receive in a single tick from other Energy Storages
	 */
	protected abstract long getMaxReceive();
	
	/**
	 * @return The maximum amount of energy that can be extracted from this Block Entity in a single tick by other Energy Storages
	 */
	protected abstract long getMaxExtract();
	
	/**
	 * @return The amount of energy that can be depleted from this Block Entity in a single tick (distinct from {@link #getMaxExtract()})
	 */
	protected long getMaxDeplete()
	{
		return getMaxExtract();
	}
	
	protected void energyChanged(long difference, boolean simulate)
	{
		if(!simulate)
		{
			this.setChanged();
			if(difference != 0)
				updateClient();
		}
	}
	
	protected SGJourneyEnergy createEnergyStorage()
	{
		return new SGJourneyEnergy(this.getCapacity(), this.getMaxReceive(), this.getMaxExtract())
		{
			@Override
			public long receiveZeroPointEnergy(long maxReceive, boolean simulate)
			{
				return canReceiveZeroPointEnergy() ? receiveLongEnergy(maxReceive, simulate) : 0;
			}
			
			@Override
			public void onEnergyChanged(long difference, boolean simulate)
			{
				energyChanged(difference, simulate);
			}
		};
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	protected void generateEnergy(long energyGenerated)
	{
		long moreEnergy = energyStorage.getTrueEnergyStored() + energyGenerated;
		
		if(this.getCapacity() >= moreEnergy)
			this.energyStorage.setEnergy(moreEnergy);
	}
	
	protected void drainEnergyStorage(IEnergyStorage energyStorage)
	{
		if(!energyStorage.canExtract())
			return;
		
		if(energyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
		{
			long simulatedOutputAmount = sgjourneyEnergy.extractLongEnergy(this.energyStorage.maxReceive(), true);
			long simulatedReceiveAmount = this.energyStorage.receiveLongEnergy(simulatedOutputAmount, true);
			
			sgjourneyEnergy.extractLongEnergy(simulatedReceiveAmount, false);
			this.energyStorage.receiveLongEnergy(simulatedReceiveAmount, false);
		}
		else
		{
			int simulatedOutputAmount = energyStorage.extractEnergy(SGJourneyEnergy.regularEnergy(this.energyStorage.maxReceive()), true);
			int simulatedReceiveAmount = this.energyStorage.receiveEnergy(simulatedOutputAmount, true);
			
			energyStorage.extractEnergy(simulatedReceiveAmount, false);
			this.energyStorage.receiveEnergy(simulatedReceiveAmount, false);
		}
	}
	
	protected void fillEnergyStorage(IEnergyStorage energyStorage)
	{
		if(!energyStorage.canReceive())
			return;
		
		if(energyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
		{
			long simulatedOutputAmount = this.energyStorage.extractLongEnergy(this.energyStorage.maxExtract(), true);
			long simulatedReceiveAmount = sgjourneyEnergy.receiveLongEnergy(simulatedOutputAmount, true);
			
			this.energyStorage.extractLongEnergy(simulatedReceiveAmount, false);
			sgjourneyEnergy.receiveLongEnergy(simulatedReceiveAmount, false);
		}
		else
		{
			int simulatedOutputAmount = this.energyStorage.extractEnergy(SGJourneyEnergy.regularEnergy(this.energyStorage.maxExtract()), true);
			int simulatedReceiveAmount = energyStorage.receiveEnergy(simulatedOutputAmount, true);
			
			this.energyStorage.extractEnergy(simulatedReceiveAmount, false);
			energyStorage.receiveEnergy(simulatedReceiveAmount, false);
		}
	}
	
	protected void outputEnergy(Direction outputDirection)
	{
		if(outputDirection == null)
			return;
		
		if(energyStorage.canExtract())
		{
			BlockEntity blockentity = level.getBlockEntity(worldPosition.relative(outputDirection));
			
			if(blockentity == null)
				return;
			
			IEnergyStorage energyStorage = level.getCapability(Capabilities.EnergyStorage.BLOCK, getBlockPos().relative(outputDirection), outputDirection.getOpposite());
			if(energyStorage != null)
				fillEnergyStorage(energyStorage);
		}
	}
	
	public void extractItemEnergy(ItemStack stack)
	{
		IEnergyStorage itemEnergy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if(itemEnergy != null)
			drainEnergyStorage(itemEnergy);
	}
	
	public void fillItemEnergy(ItemStack stack)
	{
		IEnergyStorage itemEnergy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
		if(itemEnergy != null)
			fillEnergyStorage(itemEnergy);
	}
}
