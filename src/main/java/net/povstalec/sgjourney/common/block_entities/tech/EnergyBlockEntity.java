package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import org.jetbrains.annotations.NotNull;

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
	public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		energyStorage.setEnergyNoUpdate(tag.getLong(ENERGY));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries)
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
		if(level != null && !level.isClientSide())
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
	public boolean isCorrectEnergySide(Direction side)
	{
		return true;
	}
	
	/**
	 * @return True if this Block Entity is capable of receiving energy from a Zero Point Module, otherwise false
	 */
	public boolean canReceiveZeroPointEnergy()
	{
		return CommonZPMConfig.tech_uses_zero_point_energy.get();
	}
	
	/**
	 * @return The maximum amount of energy this Block Entity can hold inside at any given time
	 */
	public abstract long getEnergyCapacity();
	
	/**
	 * @return The maximum amount of energy this Block Entity can receive in a single tick from other Energy Storages
	 */
	public abstract long getMaxEnergyReceive();
	
	/**
	 * @return The maximum amount of energy that can be extracted from this Block Entity in a single tick by other Energy Storages
	 */
	public abstract long getMaxEnergyExtract();
	
	/**
	 * @return The amount of energy that can be depleted from this Block Entity in a single tick (distinct from {@link #getMaxEnergyExtract()})
	 */
	public long getMaxEnergyDeplete()
	{
		return getMaxEnergyExtract();
	}
	
	public void energyChanged(long difference, boolean simulate)
	{
		if(!simulate)
		{
			this.setChanged();
			if(difference != 0)
				updateClient();
		}
	}
	
	public SGJourneyEnergy createEnergyStorage()
	{
		return new SGJourneyEnergy(this.getEnergyCapacity(), this.getMaxEnergyReceive(), this.getMaxEnergyExtract())
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
	
	public void generateEnergy(long energyGenerated)
	{
		long moreEnergy = energyStorage.getTrueEnergyStored() + energyGenerated;
		
		if(this.getEnergyCapacity() >= moreEnergy)
			this.energyStorage.setEnergy(moreEnergy);
	}
	
	public void drainEnergyStorage(IEnergyStorage otherEnergyStorage)
	{
		if(!otherEnergyStorage.canExtract())
			return;
		
		this.energyStorage.drainOtherEnergyStorage(otherEnergyStorage, this.energyStorage.maxReceive());
	}
	
	public void fillEnergyStorage(IEnergyStorage otherEnergyStorage)
	{
		if(!otherEnergyStorage.canReceive())
			return;
		
		this.energyStorage.fillOtherEnergyStorage(otherEnergyStorage, this.energyStorage.maxExtract());
	}
	
	public void outputEnergy(Direction outputDirection)
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
