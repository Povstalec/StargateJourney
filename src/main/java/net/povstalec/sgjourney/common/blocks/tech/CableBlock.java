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
import net.povstalec.sgjourney.common.data.ConduitNetworks;
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
	
	private final VoxelShape shapeCenter;
	private final VoxelShape shapeNorth;
	private final VoxelShape shapeSouth;
	private final VoxelShape shapeWest;
	private final VoxelShape shapeEast;
	private final VoxelShape shapeUp;
	private final VoxelShape shapeDown;
	
	public final VoxelShape[] shapeCache;
	
	public CableBlock(Properties properties, double thickness)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(WATERLOGGED, false));
		
		double sideSpace = (1 - thickness) / 2; // Empty space on one side of the cable
		
		this.shapeCenter = Shapes.box(sideSpace, sideSpace, sideSpace, sideSpace + thickness, sideSpace + thickness, sideSpace + thickness);
		this.shapeNorth = Shapes.box(sideSpace, sideSpace, 0.0, sideSpace + thickness, sideSpace + thickness, sideSpace);
		this.shapeSouth = Shapes.box(sideSpace, sideSpace, sideSpace + thickness, sideSpace + thickness, sideSpace + thickness, 1.0);
		this.shapeWest = Shapes.box(0.0, sideSpace, sideSpace, sideSpace, sideSpace + thickness, sideSpace + thickness);
		this.shapeEast = Shapes.box(sideSpace + thickness, sideSpace, sideSpace, 1.0, sideSpace + thickness, sideSpace + thickness);
		this.shapeUp = Shapes.box(sideSpace, sideSpace + thickness, sideSpace, sideSpace + thickness, 1.0, sideSpace + thickness);
		this.shapeDown = Shapes.box(sideSpace, 0.0, sideSpace, sideSpace + thickness, sideSpace, sideSpace + thickness);
		
		this.shapeCache = buildShapeCache();
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
		state = calculateState(level, pos, state);
		super.neighborChanged(state, level, pos, block, fromPos, isMoving);
		
		// We don't want a Block Entity to be present when the cable isn't connected to any energy block
		if(!isEdge(state) && level.getBlockEntity(pos) instanceof CableBlockEntity cable)
		{
			level.removeBlockEntity(pos);
			cable.setRemoved();
			level.setBlock(pos, state, 3);
		}
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
		
		updateCable(level, pos, state);
		
		BlockState blockState = calculateState(level, pos, state);
		if(state != blockState)
			level.setBlockAndUpdate(pos, blockState);
	}
	
	@Override
	public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(oldState.getBlock() != newState.getBlock())
		{
			ConduitNetworks.get(level).removeCable(level, pos);
			super.onRemove(oldState, level, pos, newState, isMoving);
		}
		else
		{
			super.onRemove(oldState, level, pos, newState, isMoving);
			updateCable(level, pos, newState);
		}
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext context)
	{
		ConnectorType north = connectorType(state, NORTH);
		ConnectorType east = connectorType(state, EAST);
		ConnectorType south = connectorType(state, SOUTH);
		ConnectorType west = connectorType(state, WEST);
		ConnectorType up = connectorType(state, UP);
		ConnectorType down = connectorType(state, DOWN);
		
		return shapeCache[bitsFromConnectors(north, east, south, west, up, down)];
	}
	
	@Override
	public BlockState updateShape(BlockState state, Direction direction, BlockState neighbourState, LevelAccessor level, BlockPos current, BlockPos offset)
	{
		if(state.getValue(WATERLOGGED))
			level.getFluidTicks().schedule(new ScheduledTick<>(Fluids.WATER, current, Fluids.WATER.getTickDelay(level), 0L));
		
		return calculateState(level, current, state);
	}
	
	private void updateCable(Level level, BlockPos pos, BlockState state)
	{
		if(!level.isClientSide())
		{
			ConduitNetworks.get(level).update(level, pos);
			if(level.getBlockEntity(pos) instanceof CableBlockEntity cable)
				cable.update();
		}
	}
	
	public static ConnectorType connectionTypeTo(BlockGetter getter, BlockPos pos, Direction direction)
	{
		BlockPos otherPos = pos.relative(direction);
		BlockState state = getter.getBlockState(otherPos);
		
		if(state.isAir())
			return ConnectorType.NONE;
		
		if(state.getBlock() instanceof CableBlock)
			return ConnectorType.CABLE;
		
		BlockEntity blockEntity = getter.getBlockEntity(otherPos);
		if(blockEntity == null)
			return ConnectorType.NONE;
		
		return blockEntity.getCapability(ForgeCapabilities.ENERGY, direction).isPresent() ? ConnectorType.BLOCK : ConnectorType.NONE;
	}
	
	public static ConnectorType connectorType(BlockState state, EnumProperty<ConnectorType> property)
	{
		try
		{
			return state.getValue(property);
		}
		catch(IllegalArgumentException e)
		{
			return ConnectorType.NONE;
		}
	}
	
	public static ConnectorType connectionTypeSide(BlockGetter getter, BlockPos pos, Direction direction)
	{
		BlockState state = getter.getBlockState(pos);
		if(state.isAir())
			return ConnectorType.NONE;
		
		return switch(direction)
		{
			case NORTH -> connectorType(state, NORTH);
			case EAST -> connectorType(state, EAST);
			case SOUTH -> connectorType(state, SOUTH);
			case WEST -> connectorType(state, WEST);
			case UP -> connectorType(state, UP);
			case DOWN -> connectorType(state, DOWN);
		};
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
	
	public VoxelShape[] buildShapeCache()
	{
		VoxelShape[] shapes = new VoxelShape[0b1000000];
		for(byte i = 0; i < 0b1000000; ++i)
		{
			shapes[i] = shapeFromBits(i);
		}
		
		return shapes;
	}
	
	public VoxelShape shapeFromBits(byte bits)
	{
		VoxelShape shape = shapeCenter;
		
		if((0b000001 & bits) != 0)
			shape = Shapes.or(shape, shapeNorth);
		if((0b000010 & bits) != 0)
			shape = Shapes.or(shape, shapeEast);
		if((0b000100 & bits) != 0)
			shape = Shapes.or(shape, shapeSouth);
		if((0b001000 & bits) != 0)
			shape = Shapes.or(shape, shapeWest);
		if((0b010000 & bits) != 0)
			shape = Shapes.or(shape, shapeUp);
		if((0b100000 & bits) != 0)
			shape = Shapes.or(shape, shapeDown);
		
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
	
	public boolean isEdge(BlockState state)
	{
		return connectorType(state, NORTH).isEdge() || connectorType(state, EAST).isEdge() ||
				connectorType(state, SOUTH).isEdge() || connectorType(state, WEST).isEdge() ||
				connectorType(state, UP).isEdge() || connectorType(state, DOWN).isEdge();
	}
	
	
	public static class NaquadahCable extends CableBlock
	{
		public NaquadahCable(Properties properties, double thickness)
		{
			super(properties, thickness);
		}
		
		@Override
		public @Nullable BlockEntity newBlockEntity(BlockPos pos, BlockState state)
		{
			return isEdge(state) ? new CableBlockEntity.NaquadahCable(pos, state) : null;
		}
	}
	
	
	
	public enum ConnectorType implements StringRepresentable
	{
		BLOCK("block", true),
		CABLE("cable", false),
		NONE("none", false);
		
		private String name;
		private boolean isEdge; // Connector cable connects to an energy block
		
		ConnectorType(String name, boolean isEdge)
		{
			this.name = name;
			this.isEdge = isEdge;
		}
		
		@Override
		public String getSerializedName()
		{
			return this.name;
		}
		
		public boolean isEdge()
		{
			return this.isEdge;
		}
	}
}
