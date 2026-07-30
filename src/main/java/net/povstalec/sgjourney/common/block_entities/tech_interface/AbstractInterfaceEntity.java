package net.povstalec.sgjourney.common.block_entities.tech_interface;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.fml.ModList;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.IrisStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.RotatingStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.tech.EnergySlotBlockEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBlock;
import net.povstalec.sgjourney.common.blocks.tech_interface.AbstractInterfaceBlock;
import net.povstalec.sgjourney.common.blockstates.InterfaceMode;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.compatibility.cctweaked.peripherals.InterfacePeripheralWrapper;
import net.povstalec.sgjourney.common.config.CommonInterfaceConfig;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.sgjourney.info.IrisInfo;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public abstract class AbstractInterfaceEntity extends EnergySlotBlockEntity
{
	public static final String ENERGY_TARGET = "energy_target";

	public int signalStrength = 0;
	
	private int lastSymbol = 0;
	private RotatingStargateEntity.RotationDirection rotationDirection = RotatingStargateEntity.RotationDirection.NONE;
	
	private IrisInfo.IrisMotion irisMotion = IrisInfo.IrisMotion.IDLE;
	
	private long energyTarget = CommonInterfaceConfig.default_energy_target.get();
	
	protected boolean requiresUpdate = true;
	@Nullable
	protected EnergyBlockEntity energyBlockEntity = null;
	protected InterfacePeripheralWrapper peripheralWrapper;
	
	public enum InterfaceType
	{
		BASIC("basic_interface"),
		CRYSTAL("crystal_interface"),
		ADVANCED_CRYSTAL("advanced_crystal_interface");
		
		private final String typeName;
		
		InterfaceType(String typeName)
		{
			this.typeName = typeName;
		}
		
		public String getName()
		{
			return this.typeName;
		}
		
		public boolean hasCrystalMethods()
		{
			return this == CRYSTAL || this == ADVANCED_CRYSTAL;
		}
		
		public boolean hasAdvancedCrystalMethods()
		{
			return this == ADVANCED_CRYSTAL;
		}
	}
	
	protected InterfaceType interfaceType;
	
	public AbstractInterfaceEntity(BlockEntityType<?> type, BlockPos pos, BlockState state, InterfaceType interfaceType)
	{
		super(type, pos, state);
		
		this.interfaceType = interfaceType;
		
		if(ModList.get().isLoaded(StargateJourney.COMPUTERCRAFT_MODID))
			peripheralWrapper = new InterfacePeripheralWrapper(this);
	}
	
	@Override
	public void onLoad()
	{
		Level level = this.getLevel();
		BlockPos pos = this.getBlockPos();
		BlockState state = this.getLevel().getBlockState(pos);
		if(level.getBlockState(pos).getBlock() instanceof AbstractInterfaceBlock interfaceBlock)
			interfaceBlock.updateInterface(state, level, pos);
		
		super.onLoad();
	}
	
	@Override
	public void loadAdditional(CompoundTag tag, HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		energyTarget = tag.getLong(ENERGY_TARGET);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, HolderLookup.Provider registries)
	{
		tag.putLong(ENERGY_TARGET, energyTarget);
		super.saveAdditional(tag, registries);
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	@Override
	public void invalidateCapabilities()
	{
		super.invalidateCapabilities();
		this.peripheralWrapper.getPeripheral().invalidate();
	}

	@Override
	public void invalidateCaps()
	{
		super.invalidateCaps();
		this.peripheralWrapper.getPeripheral().invalidate();
	}

	public boolean updateInterface(Level level, BlockPos pos, Block block, BlockState state)
	{
		requiresUpdate = true;
		
		if(peripheralWrapper != null)
		{
			boolean result = peripheralWrapper.resetInterface();
			level.invalidateCapabilities(pos);
			return result;
		}
		
		if(level.getBlockState(pos).getBlock() instanceof AbstractInterfaceBlock ccInterface)
			ccInterface.updateInterface(state, level, pos);
		
		return true;
	}
	
	public Direction getDirection()
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		
		if(gateState.getBlock() instanceof AbstractInterfaceBlock)
			return gateState.getValue(AbstractInterfaceBlock.FACING);

		StargateJourney.LOGGER.error("Couldn't find Direction " + this.getBlockPos().toString());
		return null;
	}
	
	public InterfaceMode getMode()
	{
		BlockPos pos = this.getBlockPos();
		BlockState state = this.level.getBlockState(pos);
		
		if(state.getBlock() instanceof AbstractInterfaceBlock)
			return state.getValue(AbstractInterfaceBlock.MODE);
		
		return InterfaceMode.OFF;
	}

	@Nullable
	public EnergyBlockEntity findEnergyBlockEntity()
	{
		Direction direction = getDirection();
		if(direction == null)
			return null;

		BlockPos realPos = getBlockPos().relative(direction);
		BlockState state = level.getBlockState(realPos);

		if(level.getBlockState(realPos).getBlock() instanceof AbstractStargateBlock stargateBlock)
			return stargateBlock.getStargate(level, realPos, state);
		else if(level.getBlockEntity(realPos) instanceof AbstractTransporterEntity<?> transporter)
			return transporter;

		return null;
	}
	
	public EnergyBlockEntity getEnergyBlockEntity()
	{
		if(energyBlockEntity == null || requiresUpdate)
		{
			requiresUpdate = false;
			energyBlockEntity = findEnergyBlockEntity();
		}
		
		return energyBlockEntity;
	}
	
	public void disconnectFromBlockEntity()
	{
		if(energyBlockEntity instanceof AbstractStargateEntity<?> stargate)
		{
			// Stops iris from moving when disconnected
			if(irisMotion != IrisInfo.IrisMotion.IDLE && stargate instanceof IrisStargateEntity<?> irisStargate)
				irisStargate.irisInfo().setIrisMotion(IrisInfo.IrisMotion.IDLE);
			
			// Stops stargate from rotating when disconnected
			if(rotationDirection != RotatingStargateEntity.RotationDirection.NONE && stargate instanceof RotatingStargateEntity<?> rotatingStargate)
				rotatingStargate.endRotation(true);
		}
		
		energyBlockEntity = null;
	}
	
	public InterfaceType getInterfaceType()
	{
		return this.interfaceType;
	}
	
	public int getStargateOpenTime()
	{
		if(getEnergyBlockEntity() instanceof AbstractStargateEntity<?> stargate)
			return stargate.getOpenTime();
		
		return -1;
	}
	
	public int getStargateTimeSinceLastTraveler()
	{
		if (getEnergyBlockEntity() instanceof AbstractStargateEntity<?> stargate)
			return stargate.getTimeSinceLastTraveler();
		
		return -1;
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	public long getEnergyBlockEnergy()
	{
		if(getEnergyBlockEntity() == null)
			return -1;
		
		return getEnergyBlockEntity().energyStorage.getTrueEnergyStored();
	}
	
	@Override
	protected boolean canReceiveZeroPointEnergy()
	{
		return CommonZPMConfig.stargates_use_zero_point_energy.get();
	}
	
	@Override
	public boolean isCorrectEnergySide(Direction side)
	{
		return side != getDirection();
	}
	
	@Override
	protected void outputEnergy(Direction outputDirection)
	{
		if(getEnergyBlockEntity().energyStorage.getTrueEnergyStored() >= getEnergyTarget())
			return;
		
		long needed = SGJourneyEnergy.energyToTarget(getEnergyTarget(), getEnergyBlockEntity().energyStorage.getTrueEnergyStored(), this.getMaxDeplete());
		
		long simulatedOutputAmount = this.energyStorage.depleteEnergy(needed, true);
		long simulatedReceiveAmount = getEnergyBlockEntity().energyStorage.receiveLongEnergy(simulatedOutputAmount, true);
		this.energyStorage.depleteEnergy(simulatedReceiveAmount, false);
		getEnergyBlockEntity().energyStorage.receiveLongEnergy(simulatedReceiveAmount, false);
	}
	
	public long getEnergyTarget()
	{
		return this.energyTarget;
	}
	
	public void setEnergyTarget(long energyTarget)
	{
		this.energyTarget = energyTarget;
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	public InterfacePeripheralWrapper getPeripheralWrapper()
	{
		if(!ModList.get().isLoaded(StargateJourney.COMPUTERCRAFT_MODID))
			return null;
		
		return this.peripheralWrapper;
	}
	
	public void queueEvent(String eventName, Object... objects)
	{
		if(!ModList.get().isLoaded(StargateJourney.COMPUTERCRAFT_MODID))
			return;
		
		if(this.peripheralWrapper != null)
			this.peripheralWrapper.queueEvent(eventName, objects);
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, AbstractInterfaceEntity interfaceEntity)
	{
		EnergySlotBlockEntity.tick(level, pos, state, interfaceEntity);
		
		if(interfaceEntity.getEnergyBlockEntity() != null)
		{
			int currentSymbol = 0;
			if(interfaceEntity.energyBlockEntity instanceof RotatingStargateEntity<?> stargate)
				currentSymbol = stargate.getCurrentSymbol();
			else if(interfaceEntity.energyBlockEntity instanceof PegasusStargateEntity stargate)
				currentSymbol = stargate.getCurrentSymbol();
			
			interfaceEntity.outputEnergy(interfaceEntity.getDirection());
			
			if(interfaceEntity.getEnergyBlockEntity() instanceof AbstractStargateEntity<?> stargate)
				interfaceEntity.handleShielding(state, stargate);

			if(currentSymbol != interfaceEntity.lastSymbol)
			{
				if(!level.isClientSide())
				{
					setChanged(level, pos, state);
					level.updateNeighborsAtExceptFromFacing(pos, state.getBlock(), state.getValue(AbstractInterfaceBlock.FACING));
				}
			}
			
			interfaceEntity.lastSymbol = currentSymbol;
		}
		
		interfaceEntity.updateClient();
	}
	
	protected void handleShielding(BlockState state, AbstractStargateEntity<?> stargate)
	{
		handleRedstone(state, stargate);
	}
	
	protected void handleRedstone(BlockState state, AbstractStargateEntity<?> stargate)
	{
		InterfaceMode mode = state.getValue(AbstractInterfaceBlock.MODE);
		
		if(mode != InterfaceMode.IRIS || irisMotion.irisMotionType == IrisInfo.IrisMotionType.COMPUTER)
			return;
		
		if(stargate instanceof IrisStargateEntity<?> irisStargate)
		{
			if(signalStrength == 0 && irisMotion != IrisInfo.IrisMotion.IDLE)
				setIrisMotion(irisStargate, IrisInfo.IrisMotion.IDLE);
			else if(signalStrength > 0 && signalStrength <= 7 && irisMotion != IrisInfo.IrisMotion.CLOSING_REDSTONE && irisStargate.irisInfo().belowMaxProgress())
				setIrisMotion(irisStargate, IrisInfo.IrisMotion.CLOSING_REDSTONE);
			else if(signalStrength >= 8 && signalStrength <= 15 && irisMotion != IrisInfo.IrisMotion.OPENING_REDSTONE && irisStargate.irisInfo().aboveMinProgress())
				setIrisMotion(irisStargate, IrisInfo.IrisMotion.OPENING_REDSTONE);
		}
	}
	
	public boolean setStargateRotationDirection(RotatingStargateEntity.RotationDirection rotationDirection)
	{
		if(this.rotationDirection == rotationDirection)
			return false;
		
		this.rotationDirection = rotationDirection;
		return true;
	}
	
	public boolean setIrisMotion(IrisStargateEntity<?> irisStargate, IrisInfo.IrisMotion irisMotion)
	{
		if(this.irisMotion == irisMotion)
			return false;
		
		this.irisMotion = irisMotion;
		irisStargate.irisInfo().setIrisMotion(irisMotion);
		return true;
	}
}
