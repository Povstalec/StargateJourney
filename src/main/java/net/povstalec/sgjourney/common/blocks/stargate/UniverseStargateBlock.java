package net.povstalec.sgjourney.common.blocks.stargate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;

public class UniverseStargateBlock extends AbstractStargateBaseBlock
{
	public UniverseStargateBlock(Properties properties)
	{
		super(properties);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		UniverseStargateEntity stargate = new UniverseStargateEntity(pos, state);
		
		 return stargate;
	}
	
	public BlockState ringState()
	{
		return BlockInit.UNIVERSE_RING.get().defaultBlockState();
	}

	public Block getStargate()
	{
		return BlockInit.UNIVERSE_STARGATE.get();
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.UNIVERSE_STARGATE.get(), UniverseStargateEntity::tick);
    }
}
