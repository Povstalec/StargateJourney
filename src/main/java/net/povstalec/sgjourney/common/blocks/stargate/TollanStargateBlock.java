package net.povstalec.sgjourney.common.blocks.stargate;

import java.util.ArrayList;

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
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;

public class TollanStargateBlock extends AbstractStargateBaseBlock
{
	public TollanStargateBlock(Properties properties)
	{
		super(properties, 3.0D, 1.0D);
	}
	
	@Override
	public ArrayList<StargatePart> getParts()
	{
		return StargatePart.TOLLAN_PARTS;
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		TollanStargateEntity stargate = new TollanStargateEntity(pos, state);
		
		return stargate;
	}

	@Override
	public AbstractStargateRingBlock getRing()
	{
		return BlockInit.TOLLAN_RING.get();
	}

	@Override
	public BlockState ringState()
	{
		return getRing().defaultBlockState();
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
		return createTickerHelper(type, BlockEntityInit.TOLLAN_STARGATE.get(), TollanStargateEntity::tick);
    }
}
