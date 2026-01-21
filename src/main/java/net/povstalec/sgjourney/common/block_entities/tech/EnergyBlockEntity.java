package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;

public abstract class EnergyBlockEntity extends BlockEntity
{
	public static final String ENERGY = "Energy"; // TODO Change this to "energy"
	
	private boolean canGenerateEnergy;
	public final SGJourneyEnergy energyStorage = createEnergyStorage();
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
		lazyEnergyHandler = LazyOptional.of(() -> energyStorage);
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
		energyStorage.setEnergy(nbt.getLong(ENERGY));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		super.saveAdditional(nbt);
		nbt.putLong(ENERGY, energyStorage.getTrueEnergyStored());
	}
	
	public void updateClient()
	{
		if(!level.isClientSide())
			((ServerLevel) level).getChunkSource().blockChanged(worldPosition);
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
	
	protected void changeEnergy(long difference, boolean simulate)
	{
		this.setChanged();
		updateClient();
	}
	
	public long depleteEnergy(long amount, boolean simulate)
	{
		long storedEnergy = this.getEnergyStored();
		long maxEnergyDepletion = Math.min(amount, maxExtract());
		long energyDepleted = Math.min(storedEnergy, maxEnergyDepletion);
		
		if(!simulate)
			this.setEnergy(storedEnergy - energyDepleted);
		
		if(energyDepleted != 0)
			energyStorage.onEnergyChanged(energyDepleted, simulate);
		
		return energyDepleted;
	}
	
	public long getEnergyStored()
	{
		return energyStorage.getTrueEnergyStored();
	}
	
	public long extractEnergy(long maxExtract, boolean simulate)
	{
		return energyStorage.extractLongEnergy(maxExtract, simulate);
	}
	
	public long receiveEnergy(long maxReceive, boolean simulate)
	{
		return energyStorage.receiveLongEnergy(maxReceive, simulate);
	}
	
	public long setEnergy(long energy)
	{
		return energyStorage.setEnergy(energy);
	}
	
	public boolean canExtract()
	{
		return energyStorage.canExtract();
	}
	
	public boolean canExtractEnergy(long energy)
	{
		// Max amount of energy that can be stored
		if(energyStorage.getTrueMaxEnergyStored() < energy)
			return false;

		// Max amount of energy that can be extracted
		if(energyStorage.maxExtract() < energy)
			return false;
		
		// Amount of energy that is stored
		if(energyStorage.getTrueEnergyStored() < energy)
			return false;
		
		return true;
	}
	
	public boolean canReceive()
	{
		return energyStorage.canReceive();
	}
	
	public long getEnergyCapacity()
	{
		return energyStorage.getTrueMaxEnergyStored();
	}
	
	public long getMaxExtract()
	{
		return energyStorage.maxExtract();
	}
	
	public long getMaxReceive()
	{
		return energyStorage.maxReceive();
	}
	
	protected void generateEnergy(long energyGenerated)
	{
		if(!this.canGenerateEnergy)
			return;
		
		long moreEnergy = getEnergyStored() + energyGenerated;
		
		if(this.capacity() >= moreEnergy)
			this.energyStorage.setEnergy(moreEnergy);
	}
	
	public boolean canReceive(long receivedEnergy)
	{
		return energyStorage.canReceive(receivedEnergy);
	}
	
	protected void drainEnergyStorage(IEnergyStorage energyStorage)
	{
		if(!energyStorage.canExtract())
			return;
		
		if(energyStorage instanceof SGJourneyEnergy sgjourneyEnergy)
		{
			long simulatedOutputAmount = sgjourneyEnergy.extractLongEnergy(this.energyStorage.maxExtract(), true);
			long simulatedReceiveAmount = this.energyStorage.receiveLongEnergy(simulatedOutputAmount, true);
			
			sgjourneyEnergy.extractLongEnergy(simulatedReceiveAmount, false);
			this.energyStorage.receiveLongEnergy(simulatedReceiveAmount, false);
		}
		else
		{
			int simulatedOutputAmount = energyStorage.extractEnergy(SGJourneyEnergy.regularEnergy(this.energyStorage.maxExtract()), true);
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
			
			blockentity.getCapability(ForgeCapabilities.ENERGY, outputDirection.getOpposite()).ifPresent((energyStorage) -> fillEnergyStorage(energyStorage));
		}
	}
	
	public void extractItemEnergy(ItemStack stack)
	{
		stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(itemEnergy -> drainEnergyStorage(itemEnergy));
	}
	
	public void fillItemEnergy(ItemStack stack)
	{
		stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(itemEnergy -> fillEnergyStorage(itemEnergy));
	}
}
