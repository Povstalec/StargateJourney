package net.povstalec.sgjourney.common.block_entities;

import net.minecraft.core.HolderLookup;
import org.jetbrains.annotations.NotNull;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;

public abstract class EnergyBlockEntity extends BlockEntity
{
	private boolean canGenerateEnergy;
	private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
	
	public EnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, boolean canGenerateEnergy)
	{
		super(type, pos, state);
		this.canGenerateEnergy = canGenerateEnergy;
	}
	
	public EnergyBlockEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		this(type, pos, state, false);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		lazyEnergyHandler = LazyOptional.of(() -> ENERGY_STORAGE);
	}
	
	@Override
	public void invalidateCaps()
	{
		lazyEnergyHandler.invalidate();
		super.invalidateCaps();
	}
	
	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		ENERGY_STORAGE.setEnergy(tag.getLong("Energy"));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		tag.putLong("Energy", ENERGY_STORAGE.getTrueEnergyStored());
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side)
	{
		if(capability == ForgeCapabilities.ENERGY && isCorrectEnergySide(side))
			return lazyEnergyHandler.cast();
		
		return super.getCapability(capability, side);
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	protected boolean isCorrectEnergySide(Direction side)
	{
		return true;
	}
	
	protected boolean outputsEnergy()
	{
		return getMaxExtract() > 0;
	}
	
	protected boolean receivesEnergy()
	{
		return getMaxReceive() > 0;
	}
	
	protected abstract long capacity();
	
	protected abstract long maxReceive();
	
	protected abstract long maxExtract();
	
	public final SGJourneyEnergy ENERGY_STORAGE = new SGJourneyEnergy(this.capacity(), this.maxReceive(), this.maxExtract())
	{
		@Override
		public boolean canExtract()
		{
			return outputsEnergy();
		}
		
		@Override
		public boolean canReceive()
		{
			return receivesEnergy();
		}
		
		@Override
		public void onEnergyChanged(long difference, boolean simulate)
		{
			changeEnergy(difference, simulate);
		}
	};
	
	protected void changeEnergy(long difference, boolean simulate)
	{
		this.setChanged();
	}
	
	public long depleteEnergy(long amount, boolean simulate)
	{
		long storedEnergy = this.getEnergyStored();
		long maxEnergyDepletion = Math.min(amount, maxExtract());
		long energyDepleted = Math.min(storedEnergy, maxEnergyDepletion);
		
		if(!simulate)
			this.setEnergy(storedEnergy - energyDepleted);
		
		if(energyDepleted != 0)
			ENERGY_STORAGE.onEnergyChanged(energyDepleted, simulate);
		
		return energyDepleted;
	}
	
	public long getEnergyStored()
	{
		return ENERGY_STORAGE.getTrueEnergyStored();
	}
	
	public long extractEnergy(long maxExtract, boolean simulate)
	{
		return ENERGY_STORAGE.extractLongEnergy(maxExtract, simulate);
	}
	
	public long receiveEnergy(long maxReceive, boolean simulate)
	{
		return ENERGY_STORAGE.receiveLongEnergy(maxReceive, simulate);
	}
	
	public long setEnergy(long energy)
	{
		return ENERGY_STORAGE.setEnergy(energy);
	}
	
	public boolean canExtract()
	{
		return ENERGY_STORAGE.canExtract();
	}
	
	public boolean canExtractEnergy(long energy)
	{
		// Max amount of energy that can be stored
		if(ENERGY_STORAGE.getTrueMaxEnergyStored() < energy)
			return false;

		// Max amount of energy that can be extracted
		if(ENERGY_STORAGE.maxExtract() < energy)
			return false;
		
		// Amount of energy that is stored
		if(ENERGY_STORAGE.getTrueEnergyStored() < energy)
			return false;
		
		return true;
	}
	
	public boolean canReceive()
	{
		return ENERGY_STORAGE.canReceive();
	}
	
	public long getEnergyCapacity()
	{
		return ENERGY_STORAGE.getTrueMaxEnergyStored();
	}
	
	public long getMaxExtract()
	{
		return ENERGY_STORAGE.maxExtract();
	}
	
	public long getMaxReceive()
	{
		return ENERGY_STORAGE.maxReceive();
	}
	
	protected void generateEnergy(long energyGenerated)
	{
		if(!this.canGenerateEnergy)
			return;
		
		long moreEnergy = getEnergyStored() + energyGenerated;
		
		if(this.capacity() >= moreEnergy)
			this.ENERGY_STORAGE.setEnergy(moreEnergy);
	}
	
	public boolean canReceive(long receivedEnergy)
	{
		return ENERGY_STORAGE.canReceive(receivedEnergy);
	}
	
	protected void outputEnergy(Direction outputDirection)
	{
		if(outputDirection == null)
			return;
		
		if(ENERGY_STORAGE.canExtract())
		{
			BlockEntity blockentity = level.getBlockEntity(worldPosition.relative(outputDirection));
			
			if(blockentity == null)
				return;
			else if(blockentity instanceof EnergyBlockEntity energyBE)
			{
				long simulatedOutputAmount = this.extractEnergy(this.maxExtract(), true);
				long simulatedReceiveAmount = energyBE.receiveEnergy(simulatedOutputAmount, true);
				
				this.extractEnergy(simulatedReceiveAmount, false);
				energyBE.receiveEnergy(simulatedReceiveAmount, false);
			}
			else
			{
				blockentity.getCapability(ForgeCapabilities.ENERGY, outputDirection.getOpposite()).ifPresent((energyStorage) ->
				{
					int simulatedOutputAmount = ENERGY_STORAGE.extractEnergy(SGJourneyEnergy.getRegularEnergy(ENERGY_STORAGE.maxExtract()), true);
					int simulatedReceiveAmount = energyStorage.receiveEnergy(simulatedOutputAmount, true);
					
					ENERGY_STORAGE.extractEnergy(simulatedReceiveAmount, false);
					energyStorage.receiveEnergy(simulatedReceiveAmount, false);
				});
			}
		}
	}
	
	public void getStatus(Player player)
	{
		if(level.isClientSide())
			return;
		player.sendSystemMessage(Component.literal("Energy: " + this.getEnergyStored() + " FE").withStyle(ChatFormatting.DARK_RED));
	}
}
