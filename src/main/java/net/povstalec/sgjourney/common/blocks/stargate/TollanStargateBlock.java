package net.povstalec.sgjourney.common.blocks.stargate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.common.block_entities.stargate.TollanStargateEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.misc.Orientation;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class TollanStargateBlock extends AbstractStargateBaseBlock
{
	public TollanStargateBlock(Properties properties)
	{
		super(properties, 3.0D, 1.0D);
	}

	public Stargate.Type getStargateType()
	{
		return Stargate.Type.TOLLAN;
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

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos position, CollisionContext context)
	{
		if(state.getValue(ORIENTATION) != Orientation.REGULAR)
			return getShapeFromArray(shapeProvider.BOTTOM, state.getValue(FACING), state.getValue(ORIENTATION));
		return state.getValue(FACING).getAxis() == Direction.Axis.X ? shapeProvider.Z_BOTTOM : shapeProvider.X_BOTTOM;
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
