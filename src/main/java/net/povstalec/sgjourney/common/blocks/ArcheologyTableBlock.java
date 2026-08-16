package net.povstalec.sgjourney.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.DirectionalBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;

public class ArcheologyTableBlock extends DirectionalBlock
{
	public static final DirectionProperty FACING = HorizontalDirectionalBlock.FACING;
	
	private static final VoxelShape TOP = Block.box(0.0D, 14.0D, 0.0D, 16.0D, 16.0D, 16.0D);
	private static final VoxelShape LEG_1 = Block.box(1.0D, 0.0D, 1.0D, 3.0D, 14.0D, 3.0D);
	private static final VoxelShape LEG_2 = Block.box(13.0D, 0.0D, 1.0D, 15.0D, 14.0D, 3.0D);
	private static final VoxelShape LEG_3 = Block.box(1.0D, 0.0D, 13.0D, 3.0D, 14.0D, 15.0D);
	private static final VoxelShape LEG_4 = Block.box(13.0D, 0.0D, 13.0D, 15.0D, 14.0D, 15.0D);
	
	private static final VoxelShape TABLE = Shapes.or(TOP, LEG_1, LEG_2, LEG_3, LEG_4);

	public static final MapCodec<ArcheologyTableBlock> CODEC = simpleCodec(ArcheologyTableBlock::new);
	
	public ArcheologyTableBlock(Properties properties) 
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	protected MapCodec<ArcheologyTableBlock> codec() {
		return CODEC;
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context) 
	{
	      return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	
	public boolean propagatesSkylightDown(BlockState state, BlockGetter getter, BlockPos pos) 
	{
		return true;
	}
	
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collision) {
	      return TABLE;
	   }
	
	public RenderShape getRenderType(BlockState state) 
    {
        return RenderShape.MODEL;
    }
	
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state) 
	{
	      state.add(FACING);
	}

	@Override
	public BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
}
