package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;

import javax.annotation.Nullable;

public class TollanStargateBlock extends AbstractStargateBaseBlock
{
	public TollanStargateBlock(Properties properties)
	{
		super(properties);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		TollanStargateEntity stargate = new TollanStargateEntity(pos, state);
		
		return stargate;
	}
	
	public BlockState ringState()
	{
		return BlockInit.TOLLAN_RING.get().defaultBlockState();
	}

	public Block getStargate()
	{
		return BlockInit.TOLLAN_STARGATE.get();
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		if(level.isClientSide())
			return null;
		return createTickerHelper(type, BlockEntityInit.TOLLAN_STARGATE.get(), TollanStargateEntity::tick);
    }
}
