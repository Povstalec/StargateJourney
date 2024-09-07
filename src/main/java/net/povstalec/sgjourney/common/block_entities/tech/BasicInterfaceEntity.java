package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.blocks.tech.BasicInterfaceBlock;
import net.povstalec.sgjourney.common.blockstates.InterfaceMode;
import net.povstalec.sgjourney.common.config.CommonInterfaceConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;

public class BasicInterfaceEntity extends AbstractInterfaceEntity
{
	public BasicInterfaceEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.BASIC_INTERFACE.get(), pos, state, InterfaceType.BASIC);
	}
	
	protected BasicInterfaceEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state, InterfaceType.BASIC);
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	public long capacity()
	{
		return CommonInterfaceConfig.basic_interface_capacity.get();
	}

	@Override
	public long maxReceive()
	{
		return CommonInterfaceConfig.basic_interface_max_transfer.get();
	}

	@Override
	public long maxExtract()
	{
		return CommonInterfaceConfig.basic_interface_max_transfer.get();
	}
	
	protected void handleShielding(BlockState state, AbstractStargateEntity stargate)
	{
		handleIris(state, stargate);
	}
	
	private void handleIris(BlockState state, AbstractStargateEntity stargate)
	{
		InterfaceMode mode = state.getValue(BasicInterfaceBlock.MODE);
		
		if(mode != InterfaceMode.SHIELDING)
			return;
		
		if(signalStrength > 0 && signalStrength <= 7)
			stargate.increaseIrisProgress();
		else if(signalStrength > 8 && signalStrength <= 15)
			stargate.decreaseIrisProgress();
	}
}
