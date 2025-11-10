package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
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

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class EnergyBlockEntity extends BlockEntity
{
	public static final String ENERGY = "energy";
	
	private boolean canGenerateEnergy;
	protected SGJourneyEnergy ENERGY_STORAGE = createEnergyStorage();
	private Lazy<IEnergyStorage> lazyEnergyHandler = Lazy.of(() -> ENERGY_STORAGE);
	
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
		lazyEnergyHandler = Lazy.of(() -> ENERGY_STORAGE);
		super.onLoad();
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
		ENERGY_STORAGE.setEnergy(tag.getLong(ENERGY));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		tag.putLong(ENERGY, ENERGY_STORAGE.getTrueEnergyStored());
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	public SGJourneyEnergy getEnergyStorage()
	{
		return ENERGY_STORAGE;
	}
	
	@Nullable
	public IEnergyStorage getEnergyHandler(Direction side)
	{
		if(isCorrectEnergySide(side))
			return lazyEnergyHandler.get();
		
		return null;
	}
	
	@Nonnull
	protected SGJourneyEnergy createEnergyStorage()
	{
		return new SGJourneyEnergy(this.capacity(), this.maxReceive(), this.maxExtract())
		{
			@Override
			public long receiveZeroPointEnergy(long maxReceive, boolean simulate)
			{
				return canReceiveZeroPointEnergy() ? receiveLongEnergy(maxReceive, simulate) : 0;
			}
			
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
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	protected boolean isCorrectEnergySide(Direction side)
	{
		return true;
	}
	
	protected boolean canReceiveZeroPointEnergy()
	{
		return CommonZPMConfig.tech_uses_zero_point_energy.get();
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
	
	protected void drainEnergyStorage(IEnergyStorage energyStorage)
	{
		if(!energyStorage.canExtract())
			return;
		
		if(energyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
		{
			long simulatedOutputAmount = sgjourneyEnergy.extractLongEnergy(ENERGY_STORAGE.maxExtract(), true);
			long simulatedReceiveAmount = ENERGY_STORAGE.receiveLongEnergy(simulatedOutputAmount, true);
			
			sgjourneyEnergy.extractLongEnergy(simulatedReceiveAmount, false);
			ENERGY_STORAGE.receiveLongEnergy(simulatedReceiveAmount, false);
		}
		else
		{
			int simulatedOutputAmount = energyStorage.extractEnergy(SGJourneyEnergy.regularEnergy(ENERGY_STORAGE.maxExtract()), true);
			int simulatedReceiveAmount = ENERGY_STORAGE.receiveEnergy(simulatedOutputAmount, true);
			
			energyStorage.extractEnergy(simulatedReceiveAmount, false);
			ENERGY_STORAGE.receiveEnergy(simulatedReceiveAmount, false);
		}
	}
	
	protected void fillEnergyStorage(IEnergyStorage energyStorage)
	{
		if(!energyStorage.canReceive())
			return;
		
		if(energyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
		{
			long simulatedOutputAmount = ENERGY_STORAGE.extractLongEnergy(ENERGY_STORAGE.maxExtract(), true);
			long simulatedReceiveAmount = sgjourneyEnergy.receiveLongEnergy(simulatedOutputAmount, true);
			
			ENERGY_STORAGE.extractLongEnergy(simulatedReceiveAmount, false);
			sgjourneyEnergy.receiveLongEnergy(simulatedReceiveAmount, false);
		}
		else
		{
			int simulatedOutputAmount = ENERGY_STORAGE.extractEnergy(SGJourneyEnergy.regularEnergy(ENERGY_STORAGE.maxExtract()), true);
			int simulatedReceiveAmount = energyStorage.receiveEnergy(simulatedOutputAmount, true);
			
			ENERGY_STORAGE.extractEnergy(simulatedReceiveAmount, false);
			energyStorage.receiveEnergy(simulatedReceiveAmount, false);
		}
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
	
	public void getStatus(Player player)
	{
		if(level.isClientSide())
			return;
		player.sendSystemMessage(Component.translatable("info.sgjourney.energy").append(Component.literal(": " + this.getEnergyStored() + " FE")).withStyle(ChatFormatting.DARK_RED));
	}
}
