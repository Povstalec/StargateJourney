package net.povstalec.sgjourney.block_entities;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import dan200.computercraft.api.peripheral.IPeripheral;
import dan200.computercraft.shared.Capabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.blocks.BasicInterfaceBlock;
import net.povstalec.sgjourney.blocks.stargate.AbstractStargateRingBlock;
import net.povstalec.sgjourney.init.BlockEntityInit;
import net.povstalec.sgjourney.peripherals.BasicInterfacePeripheral;
import net.povstalec.sgjourney.stargate.StargatePart;

public class BasicInterfaceEntity extends EnergyBlockEntity
{
	public LazyOptional<IPeripheral> peripheral = LazyOptional.of(() -> new BasicInterfacePeripheral(this));
	
	protected AbstractStargateEntity stargate = null;

	private int desiredSymbol = 0;
	private boolean rotate = false;
	private boolean rotateClockwise = true;
	
	public BasicInterfaceEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.BASIC_INTERFACE.get(), pos, state);
	}
	
	protected BasicInterfaceEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(cap == Capabilities.CAPABILITY_PERIPHERAL)
			return peripheral.cast();
		
		return super.getCapability(cap, side);
	}
	
	public Direction getDirection()
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		
		if(gateState.getBlock() instanceof BasicInterfaceBlock)
			return this.level.getBlockState(gatePos).getValue(BasicInterfaceBlock.FACING);

		StargateJourney.LOGGER.info("Couldn't find Direction");
		return null;
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	public boolean isCorrectSide(Direction side)
	{
		if(side == getDirection())
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
		long simulatedOutputAmount = this.extractEnergy(this.maxExtract(), true);
		long simulatedReceiveAmount = stargate.receiveEnergy(simulatedOutputAmount, true);
		
		this.extractEnergy(simulatedReceiveAmount, false);
		stargate.receiveEnergy(simulatedReceiveAmount, false);
	}
	
	//============================================================================================
	//*****************************************CC: Tweaked****************************************
	//============================================================================================
	
	public boolean isConnectedToStargate()
	{
		if(stargate != null)
			return true;
		
		return false;
	}
	
	public AbstractStargateEntity getStargate()
	{
		return stargate;
	}
	
	public void rotateStargate(boolean clockwise, int symbol)
	{
		this.desiredSymbol = symbol;
		this.rotateClockwise = clockwise;
		this.rotate = true;
	}
	
	public boolean raiseChevron()
	{
		if(stargate instanceof MilkyWayStargateEntity milkyWayStargate)
			return milkyWayStargate.raiseChevron();
		return false;
	}
	
	public boolean lowerChevron()
	{
		if(stargate instanceof MilkyWayStargateEntity milkyWayStargate)
			return milkyWayStargate.lowerChevron();
		return false;
	}
	
	public int getChevronsEngaged()
	{
		return stargate.getChevronsEngaged();
	}
	
	public int getOpenTime()
	{
		return stargate.getOpenTime();
	}
	
	public boolean isCurrentSymbol(int symbol)
	{
		if(stargate instanceof MilkyWayStargateEntity milkyWayStargate)
			return milkyWayStargate.isCurrentSymbol(symbol);
		return false;
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, BasicInterfaceEntity basicInterface)
	{
		Direction direction = basicInterface.getDirection();
		if(direction == null)
			return;
		
		BlockPos realPos = pos.relative(basicInterface.getDirection());
		
		if(level.getBlockState(realPos).getBlock() instanceof AbstractStargateRingBlock)
			realPos = StargatePart.getMainBlockPos(realPos, level.getBlockState(realPos).getValue(AbstractStargateRingBlock.FACING), level.getBlockState(realPos).getValue(AbstractStargateRingBlock.PART));
		
		if(level.getBlockEntity(realPos) instanceof AbstractStargateEntity stargate)
			basicInterface.stargate = stargate;
		else
			basicInterface.stargate = null;
		
		if(basicInterface.stargate != null)
		{
			basicInterface.rotateStargate();
			basicInterface.outputEnergy(direction);
		}
			
	}
	
	private void rotateStargate()
	{
		if(this.rotate && isConnectedToStargate() && this.stargate instanceof MilkyWayStargateEntity milkyWayStargate)
		{
			if(milkyWayStargate.isCurrentSymbol(this.desiredSymbol))
				this.rotate = false;
			else
				milkyWayStargate.rotate(rotateClockwise);
		}
	}
}
