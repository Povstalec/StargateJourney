package net.povstalec.sgjourney.common.blocks.zpm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.common.block_entities.zpm.ZPMPlugEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class ZPMPlugBlock extends HorizontalDirectionalZPMHolderBlock
{
	private static final VoxelShape BODY_X = Block.box(0.0D, 0.0D, 4.0D, 16.0D, 11.0D, 12.0D);
	private static final VoxelShape BODY_Z = Block.box(4.0D, 0.0D, 0.0D, 12.0D, 11.0D, 16.0D);
	private static final VoxelShape TOP_1_X = Block.box(0.0D, 11.0D, 4.0D, 5.0D, 13.0D, 12.0D);
	private static final VoxelShape TOP_1_Z = Block.box(4.0D, 11.0D, 0.0D, 12.0D, 13.0D, 5.0D);
	private static final VoxelShape TOP_2_X = Block.box(11.0D, 11.0D, 4.0D, 16.0D, 13.0D, 12.0D);
	private static final VoxelShape TOP_2_Z = Block.box(4.0D, 11.0D, 11.0D, 12.0D, 13.0D, 16.0D);
	
	private static final VoxelShape ZPM_PLUG_X = Shapes.or(BODY_X, TOP_1_X, TOP_2_X);
	private static final VoxelShape ZPM_PLUG_Z = Shapes.or(BODY_Z, TOP_1_Z, TOP_2_Z);
	
	public ZPMPlugBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING);
	}
	
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}

	@Override
	public BlockEntity newBlockEntity(@NotNull BlockPos pos, @NotNull BlockState state)
	{
		return new ZPMPlugEntity(pos, state);
	}
	
	@Override
	public @NotNull VoxelShape getShape(BlockState state, @NotNull BlockGetter getter, @NotNull BlockPos pos, @NotNull CollisionContext collision)
	{
		return state.getValue(FACING).getAxis() == Direction.Axis.X ? ZPM_PLUG_X : ZPM_PLUG_Z;
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(@NotNull Level level, @NotNull BlockState state, @NotNull BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.ZPM_PLUG.get(), ZPMPlugEntity::tick);
    }
}
