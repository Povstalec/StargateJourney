package net.povstalec.sgjourney.common.blocks.stargate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.stargate.PegasusStargateEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class PegasusStargateBlock extends AbstractStargateBaseBlock
{
	public PegasusStargateBlock(Properties properties)
	{
		super(properties, 7.0D, 1.0D);
	}

	public Stargate.Type getStargateType()
	{
		return Stargate.Type.PEGASUS;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		PegasusStargateEntity stargate = new PegasusStargateEntity(pos, state);
		
		return stargate;
	}
	
	public BlockState ringState()
	{
		return BlockInit.PEGASUS_RING.get().defaultBlockState();
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		if(level.isClientSide())
			return null;
		return createTickerHelper(type, BlockEntityInit.PEGASUS_STARGATE.get(), PegasusStargateEntity::tick);
    }
}
