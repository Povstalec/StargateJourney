package net.povstalec.sgjourney.common.block_entities;

import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.blocks.BasicInterfaceBlock;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateRingBlock;
import net.povstalec.sgjourney.common.capabilities.CCTweakedCapabilities;
import net.povstalec.sgjourney.common.cctweaked.peripherals.BasicPeripheralHolder;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientboundBasicInterfaceUpdatePacket;

public class BasicInterfaceEntity extends EnergyBlockEntity
{
	private int desiredSymbol = 0;
	private int currentSymbol = 0;
	private boolean rotate = false;
	private boolean rotateClockwise = true;
	
	public EnergyBlockEntity energyBlockEntity = null;
	BasicPeripheralHolder peripheralHolder;
	
	public BasicInterfaceEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.BASIC_INTERFACE.get(), pos, state);
		
		if(ModList.get().isLoaded("computercraft"))
			peripheralHolder = new BasicPeripheralHolder(this);
	}
	
	protected BasicInterfaceEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public void onLoad()
	{
		Level level = this.getLevel();
		BlockPos pos = this.getBlockPos();
		BlockState state = this.getLevel().getBlockState(pos);
		if(level.getBlockState(pos).getBlock() instanceof BasicInterfaceBlock ccInterface)
			ccInterface.updateInterface(state, level, pos);
		
		super.onLoad();
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side)
	{
		if(ModList.get().isLoaded("computercraft") && cap == CCTweakedCapabilities.CAPABILITY_PERIPHERAL)
			return peripheralHolder.newPeripheral().cast();
			
		return super.getCapability(cap, side);
	}
	
	public boolean updateInterface(Level level, BlockPos pos, Block block, BlockState state)
	{
		if(peripheralHolder != null)
			return peripheralHolder.resetInterface();
		
		if(level.getBlockState(pos).getBlock() instanceof BasicInterfaceBlock ccInterface)
			ccInterface.updateInterface(state, level, pos);
		
		return true;
	}
	
	public Direction getDirection()
	{
		BlockPos gatePos = this.getBlockPos();
		BlockState gateState = this.level.getBlockState(gatePos);
		
		if(gateState.getBlock() instanceof BasicInterfaceBlock)
			return gateState.getValue(BasicInterfaceBlock.FACING);

		StargateJourney.LOGGER.info("Couldn't find Direction");
		return null;
	}

	@Nullable
	public EnergyBlockEntity findEnergyBlockEntity()
	{
		Direction direction = getDirection();
		if(direction == null)
			return null;

		BlockPos realPos = getBlockPos().relative(direction);
		BlockState state = level.getBlockState(realPos);

		if(level.getBlockState(realPos).getBlock() instanceof AbstractStargateRingBlock)
			realPos = state.getValue(AbstractStargateRingBlock.PART)
					.getBaseBlockPos(realPos, state.getValue(AbstractStargateRingBlock.FACING), state.getValue(AbstractStargateRingBlock.ORIENTATION));

		return level.getBlockEntity(realPos) instanceof EnergyBlockEntity energyBlockEntity ? energyBlockEntity : null;
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
			int lastSymbol = basicInterface.currentSymbol;
			basicInterface.outputEnergy(basicInterface.getDirection());
			
			if(basicInterface.energyBlockEntity instanceof MilkyWayStargateEntity stargate)
				basicInterface.rotateStargate(stargate);

			if (lastSymbol != basicInterface.currentSymbol) {
				if(!level.isClientSide()) {
//					System.out.println("Block at " + pos + " has detected change from symbol " + lastSymbol + " to " + basicInterface.currentSymbol);

					setChanged(level, pos, state);
					level.updateNeighborsAtExceptFromFacing(pos, state.getBlock(), state.getValue(BasicInterfaceBlock.FACING));
				}
			}
		}
		
		if(level.isClientSide())
			return;
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(basicInterface.worldPosition)),
				new ClientboundBasicInterfaceUpdatePacket(basicInterface.worldPosition, basicInterface.getEnergyStored()));
			
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

		this.currentSymbol = stargate.getCurrentSymbol();
	}
}
