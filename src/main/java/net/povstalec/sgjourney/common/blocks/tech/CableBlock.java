package net.povstalec.sgjourney.common.blocks.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.ticks.ScheduledTick;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.povstalec.sgjourney.common.block_entities.tech.CableBlockEntity;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import org.jetbrains.annotations.Nullable;

public abstract class CableBlock extends Block implements SimpleWaterloggedBlock, EntityBlock
{
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	public static final EnumProperty<ConnectorType> NORTH = EnumProperty.create("north", ConnectorType.class);
	public static final EnumProperty<ConnectorType> EAST = EnumProperty.create("east", ConnectorType.class);
	public static final EnumProperty<ConnectorType> SOUTH = EnumProperty.create("south", ConnectorType.class);
	public static final EnumProperty<ConnectorType> WEST = EnumProperty.create("west", ConnectorType.class);
	public static final EnumProperty<ConnectorType> UP = EnumProperty.create("up", ConnectorType.class);
	public static final EnumProperty<ConnectorType> DOWN = EnumProperty.create("down", ConnectorType.class);
	
	private static final VoxelShape SHAPE_CABLE_NORTH = Shapes.box(0.4, 0.4, 0.0, 0.6, 0.6, 0.4);
	private static final VoxelShape SHAPE_CABLE_SOUTH = Shapes.box(0.4, 0.4, 0.6, 0.6, 0.6, 1.0);
	private static final VoxelShape SHAPE_CABLE_WEST = Shapes.box(0.0, 0.4, 0.4, 0.4, 0.6, 0.6);
	private static final VoxelShape SHAPE_CABLE_EAST = Shapes.box(0.6, 0.4, 0.4, 1.0, 0.6, 0.6);
	private static final VoxelShape SHAPE_CABLE_UP = Shapes.box(0.4, 0.6, 0.4, 0.6, 1.0, 0.6);
	private static final VoxelShape SHAPE_CABLE_DOWN = Shapes.box(0.4, 0.0, 0.4, 0.6, 0.4, 0.6);
	
	public static final VoxelShape[] SHAPE_CACHE = buildShapeCache();
	
	public CableBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(WATERLOGGED, NORTH, EAST, SOUTH, WEST, UP, DOWN);
	}
	
	@Override
	public FluidState getFluidState(BlockState state)
	{
		return state.getValue(WATERLOGGED) ? Fluids.WATER.getSource(false) : super.getFluidState(state);
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos fromPos, boolean isMoving)
	{
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
		
		if(!level.isClientSide() && level.getBlockEntity(pos) instanceof CableBlockEntity cable)
			cable.invalidate();
	}
	
	@Nullable
	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
		Level level = context.getLevel();
		BlockPos pos = context.getClickedPos();
		
		return calculateState(level, pos, defaultBlockState()).setValue(WATERLOGGED, level.getFluidState(pos).getType() == Fluids.WATER);
	}
	
	@Override
	public void setPlacedBy(Level level, BlockPos pos, BlockState state, LivingEntity entity, ItemStack stack)
	{
		super.setPlacedBy(level, pos, state, entity, stack);
		
		if(!level.isClientSide() && level.getBlockEntity(pos) instanceof CableBlockEntity cable)
			cable.invalidate();
		
		BlockState blockState = calculateState(level, pos, state);
		if(state != blockState)
			level.setBlockAndUpdate(pos, blockState);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter world, BlockPos pos, CollisionContext context)
	{
		ConnectorType north = connectionTypeTo(world, pos, Direction.NORTH);
		ConnectorType east = connectionTypeTo(world, pos, Direction.EAST);
		ConnectorType south = connectionTypeTo(world, pos, Direction.SOUTH);
		ConnectorType west = connectionTypeTo(world, pos, Direction.WEST);
		ConnectorType up = connectionTypeTo(world, pos, Direction.UP);
		ConnectorType down = connectionTypeTo(world, pos, Direction.DOWN);
		
		return SHAPE_CACHE[bitsFromConnectors(north, east, south, west, up, down)];
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor level, BlockPos current, BlockPos offset)
	{
		if(state.getValue(WATERLOGGED))
			level.getFluidTicks().schedule(new ScheduledTick<>(Fluids.WATER, current, Fluids.WATER.getTickDelay(level), 0L));
		
		return calculateState(level, current, state);
	}
	
	public ConnectorType connectionTypeTo(BlockGetter getter, BlockPos pos, Direction direction)
	{
		BlockPos otherPos = pos.relative(direction);
		BlockState state = getter.getBlockState(otherPos);
		
		if(state.isAir())
			return ConnectorType.NONE;
		
		BlockEntity blockEntity = getter.getBlockEntity(otherPos);
		if(blockEntity == null)
			return ConnectorType.NONE;
		else if(blockEntity instanceof CableBlockEntity)
			return ConnectorType.CABLE;
		
		return blockEntity.getCapability(ForgeCapabilities.ENERGY, direction).isPresent() ? ConnectorType.BLOCK : ConnectorType.NONE;
	}
	
	protected BlockState calculateState(LevelAccessor level, BlockPos pos, BlockState state)
	{
		ConnectorType north = connectionTypeTo(level, pos, Direction.NORTH);
		ConnectorType east = connectionTypeTo(level, pos, Direction.EAST);
		ConnectorType south = connectionTypeTo(level, pos, Direction.SOUTH);
		ConnectorType west = connectionTypeTo(level, pos, Direction.WEST);
		ConnectorType up = connectionTypeTo(level, pos, Direction.UP);
		ConnectorType down = connectionTypeTo(level, pos, Direction.DOWN);
		
		return state.setValue(NORTH, north).setValue(SOUTH, south).setValue(WEST, west).setValue(EAST, east).setValue(UP, up).setValue(DOWN, down);
	}
	
	public static VoxelShape[] buildShapeCache()
	{
		VoxelShape[] shapes = new VoxelShape[0b1000000];
		for(byte i = 0; i < 0b1000000; ++i)
		{
			shapes[i] = shapeFromBits(i);
		}
		
		return shapes;
	}
	
	public static VoxelShape shapeFromBits(byte bits)
	{
		VoxelShape shape = Shapes.box(0.4, 0.4, 0.4, 0.6, 0.6, 0.6);
		
		if((0b000001 & bits) != 0)
			shape = Shapes.or(shape, SHAPE_CABLE_NORTH);
		if((0b000010 & bits) != 0)
			shape = Shapes.or(shape, SHAPE_CABLE_EAST);
		if((0b000100 & bits) != 0)
			shape = Shapes.or(shape, SHAPE_CABLE_SOUTH);
		if((0b001000 & bits) != 0)
			shape = Shapes.or(shape, SHAPE_CABLE_WEST);
		if((0b010000 & bits) != 0)
			shape = Shapes.or(shape, SHAPE_CABLE_UP);
		if((0b100000 & bits) != 0)
			shape = Shapes.or(shape, SHAPE_CABLE_DOWN);
		
		return shape;
	}
	
	public static byte bitsFromConnectors(ConnectorType north, ConnectorType east, ConnectorType south, ConnectorType west, ConnectorType up, ConnectorType down)
	{
		byte bits = 0b000000;
		
		if(north != ConnectorType.NONE)
			bits |= 0b000001;
		if(east != ConnectorType.NONE)
			bits |= 0b000010;
		if(south != ConnectorType.NONE)
			bits |= 0b000100;
		if(west != ConnectorType.NONE)
			bits |= 0b001000;
		if(up != ConnectorType.NONE)
			bits |= 0b010000;
		if(down != ConnectorType.NONE)
			bits |= 0b100000;
		
		return bits;
	}
	
	
	public static class NaquadahCable extends CableBlock
	{
		public NaquadahCable(Properties properties)
		{
			super(properties);
		}
		
		@Override
		public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
		{
			return new CableBlockEntity.NaquadahCable(pos, state);
		}
	}
	
	
	
	public enum ConnectorType implements StringRepresentable
	{
		BLOCK("name"),
		CABLE("cable"),
		NONE("none");
		
		private String name;
		
		ConnectorType(String name)
		{
			this.name = name;
		}
		
		@Override
		public String getSerializedName()
		{
			return this.name;
		}
	}
}
