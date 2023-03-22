package net.povstalec.sgjourney.block_entities;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import dan200.computercraft.api.peripheral.IPeripheral;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.blocks.BasicInterfaceBlock;
import net.povstalec.sgjourney.blocks.stargate.AbstractStargateRingBlock;
import net.povstalec.sgjourney.capabilities.CCTweakedCapabilities;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.peripherals.BasicInterfacePeripheral;
import net.povstalec.sgjourney.peripherals.BasicStargatePeripheral;
import net.povstalec.sgjourney.peripherals.MilkyWayStargatePeripheral;
import net.povstalec.sgjourney.peripherals.PeripheralHolder;

public class BasicInterfaceEntity extends EnergyBlockEntity
{
	private int desiredSymbol = 0;
	private boolean rotate = false;
	private boolean rotateClockwise = true;
	
	protected EnergyBlockEntity energyBlockEntity = null;
	//PeripheralHolder peripheralHolder = new PeripheralHolder();
	private BasicInterfacePeripheral basicInterfacePeripheral;
	private LazyOptional<IPeripheral> peripheral;
	
	public BasicInterfaceEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.BASIC_INTERFACE.get(), pos, state);
	}
	
	protected BasicInterfaceEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	public static BasicInterfacePeripheral createPeripheral(BasicInterfaceEntity basicInterface, EnergyBlockEntity energyBlockEntity)
	{
		if(energyBlockEntity instanceof AbstractStargateEntity stargate)
		{
			if(stargate instanceof MilkyWayStargateEntity milkyWayStargate)
			{
				System.out.println("Milky Way Stargate");
				return new MilkyWayStargatePeripheral(basicInterface, milkyWayStargate);
			}

			System.out.println("Stargate");
			return new BasicStargatePeripheral(basicInterface, stargate);
		}

		System.out.println("Interface");
		return new BasicInterfacePeripheral(basicInterface);
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(ModList.get().isLoaded("computercraft") && cap == CCTweakedCapabilities.CAPABILITY_PERIPHERAL)
		{
			System.out.println("=====Fetching=====");
			if (peripheral == null) {
				basicInterfacePeripheral = createPeripheral(this, findEnergyBlockEntity());
				peripheral = LazyOptional.of(() -> basicInterfacePeripheral);
			}
			return peripheral.cast();
		}
		return super.getCapability(cap, side);
	}
	
	public boolean updateInterface()
	{
		BasicInterfacePeripheral newPeripheral = createPeripheral(this, findEnergyBlockEntity());
		if (basicInterfacePeripheral != null && basicInterfacePeripheral.equals(newPeripheral))
		{
			// Peripheral is same as before, no changes needed.
			return false;
		}

		// Peripheral has changed, invalidate the capability and trigger a block update.
		basicInterfacePeripheral = newPeripheral;
		if (peripheral != null) {
			peripheral.invalidate();
			peripheral = LazyOptional.of(() -> newPeripheral);
		}
		return true;
	}
	
	public Direction getDirection()
	{
		BlockState gateState = getBlockState();

		if(gateState.getBlock() instanceof BasicInterfaceBlock)
			return gateState.getValue(BasicInterfaceBlock.FACING);

		StargateJourney.LOGGER.info("Couldn't find Direction");
		return null;
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	public boolean isCorrectSide(Direction side)
	{
		if(side == getDirection().getOpposite())
			return false;
		return true;
	}

	@Override
	protected boolean outputsEnergy()
	{
		return false;
	}
	
	@Override
	protected boolean receivesEnergy()
	{
		return true;
	}

	@Override
	public long capacity()
	{
		return 5000000;
	}

	@Override
	public long maxReceive()
	{
		return 100000;
	}

	@Override
	public long maxExtract()
	{
		return 100000;
	}
	
	@Override
	protected void outputEnergy(Direction outputDirection)
	{
		long storedEnergy = this.getEnergyStored();
		
		long energyExtracted = Math.min(storedEnergy, maxExtract());
		long simulatedReceiveAmount = energyBlockEntity.receiveEnergy(energyExtracted, true);

		this.setEnergy(storedEnergy - energyExtracted);
		energyBlockEntity.receiveEnergy(simulatedReceiveAmount, false);
	}

	private @Nullable EnergyBlockEntity findEnergyBlockEntity() {
		Direction direction = getDirection();
		if(direction == null) return null;

		BlockPos realPos = getBlockPos().relative(direction);

		if(level.getBlockState(realPos).getBlock() instanceof AbstractStargateRingBlock)
			realPos = level.getBlockState(realPos).getValue(AbstractStargateRingBlock.PART).getMainBlockPos(realPos, level.getBlockState(realPos).getValue(AbstractStargateRingBlock.FACING));

		return level.getBlockEntity(realPos) instanceof EnergyBlockEntity energyBlockEntity ? energyBlockEntity : null;
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	public void rotateStargate(boolean clockwise, int symbol)
	{
		this.desiredSymbol = symbol;
		this.rotateClockwise = clockwise;
		this.rotate = true;
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, BasicInterfaceEntity basicInterface)
	{
		basicInterface.energyBlockEntity = basicInterface.findEnergyBlockEntity();
		if(basicInterface.energyBlockEntity != null)
		{

			basicInterface.outputEnergy(basicInterface.getDirection());
			
			if(basicInterface.energyBlockEntity instanceof MilkyWayStargateEntity stargate)
				basicInterface.rotateStargate(stargate);
		}
			
	}
	
	private void rotateStargate(MilkyWayStargateEntity stargate)
	{
		if(this.rotate)
		{
			if(stargate.isCurrentSymbol(this.desiredSymbol))
				this.rotate = false;
			else
				stargate.rotate(rotateClockwise);
		}
	}
}
