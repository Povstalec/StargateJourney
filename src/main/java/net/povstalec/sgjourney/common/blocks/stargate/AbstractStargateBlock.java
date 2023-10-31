package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.misc.Orientation;
import net.povstalec.sgjourney.common.misc.VoxelShapeProvider;
import net.povstalec.sgjourney.common.stargate.ConnectionState;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargatePart;

public abstract class AbstractStargateBlock extends Block implements SimpleWaterloggedBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<Orientation> ORIENTATION = EnumProperty.create("orientation", Orientation.class);
	public static final EnumProperty<StargatePart> PART = EnumProperty.create("stargate_part", StargatePart.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final EnumProperty<ConnectionState> CONNECTION_STATE = EnumProperty.create("connection_state", ConnectionState.class);
	public static final IntegerProperty CHEVRONS_ACTIVE = IntegerProperty.create("chevrons_active", 0, 9);
	//TODO public static final BooleanProperty FULL = BooleanProperty.create("full");

	protected VoxelShapeProvider shapeProvider;

	public AbstractStargateBlock(Properties properties, double width, double horizontalOffset)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ORIENTATION, Orientation.REGULAR).setValue(CONNECTION_STATE, ConnectionState.IDLE).setValue(CHEVRONS_ACTIVE, 0).setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(PART, StargatePart.BASE)/*.setValue(FULL, Boolean.valueOf(false))*/);
		shapeProvider = new VoxelShapeProvider(width, horizontalOffset);
	}

	public abstract Stargate.Type getStargateType();

	public VoxelShape getShapeFromArray(VoxelShape[][] shapes, Direction direction, Orientation orientation)
	{
		int horizontal = direction.get2DDataValue();
		int vertical = orientation.get2DDataValue();

		return shapes[vertical][horizontal % shapes[vertical].length];
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING).add(WATERLOGGED).add(ORIENTATION).add(PART).add(CONNECTION_STATE).add(CHEVRONS_ACTIVE);
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

	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos)
	{
		return true;
	}
	
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos position, CollisionContext context)
	{
		if(state.getValue(ORIENTATION) != Orientation.REGULAR)
			return shapeProvider.HORIZONTAL;
		return state.getValue(FACING).getAxis() == Direction.Axis.X ? shapeProvider.Z_FULL : shapeProvider.X_FULL;
	}

	@Override
	public BlockState updateShape(BlockState oldState, Direction direction, BlockState newState, LevelAccessor levelAccessor, BlockPos oldPos, BlockPos newPos)
	{
		if(oldState.getValue(WATERLOGGED))
			levelAccessor.scheduleTick(oldPos, Fluids.WATER, Fluids.WATER.getTickDelay(levelAccessor));

		return super.updateShape(oldState, direction, newState, levelAccessor, oldPos, newPos);
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(state.getValue(PART).getBaseBlockPos(pos, state.getValue(FACING), state.getValue(ORIENTATION)));
		if(blockentity instanceof AbstractStargateEntity stargate)
		{
			if(!level.isClientSide() && !player.isCreative())
			{
				ItemStack itemstack = new ItemStack(asItem());

				blockentity.saveToItem(itemstack);

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				itementity.setUnlimitedLifetime();
				level.addFreshEntity(itementity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}

	@Override
	public PushReaction getPistonPushReaction(BlockState state)
	{
		return PushReaction.BLOCK;
	}

	@Override
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}
}
