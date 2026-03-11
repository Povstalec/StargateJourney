package net.povstalec.sgjourney.common.block_entities.tech_interface;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
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
	public long getCapacity()
	{
		return CommonInterfaceConfig.basic_interface_capacity.get();
	}

	@Override
	public long getMaxReceive()
	{
		return CommonInterfaceConfig.basic_interface_max_transfer.get();
	}

	@Override
	public long getMaxExtract()
	{
		return 0;
	}
	
	@Override
	public long getMaxDeplete()
	{
		return CommonInterfaceConfig.basic_interface_max_transfer.get();
	}
}
