package net.povstalec.sgjourney.common.blocks.stargate.shielding;

import java.util.ArrayList;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.IrisStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateBaseBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.ShieldingPart;
import net.povstalec.sgjourney.common.blockstates.ShieldingState;
import net.povstalec.sgjourney.common.config.CommonIrisConfig;
import net.povstalec.sgjourney.common.init.TagInit;
import net.povstalec.sgjourney.common.misc.VoxelShapeProvider;

public abstract class AbstractShieldingBlock extends Block implements SimpleWaterloggedBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<Orientation> ORIENTATION = EnumProperty.create("orientation", Orientation.class);
	public static final EnumProperty<ShieldingState> SHIELDING_STATE = EnumProperty.create("shielding_state", ShieldingState.class);
	public static final EnumProperty<ShieldingPart> PART = EnumProperty.create("shielding_part", ShieldingPart.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	
	protected VoxelShapeProvider shapeProvider;

	public AbstractShieldingBlock(Properties properties, double width, double horizontalOffset)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(ORIENTATION, Orientation.REGULAR)
				.setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(PART, ShieldingPart.ABOVE).setValue(SHIELDING_STATE, ShieldingState.OPEN));
		shapeProvider = new VoxelShapeProvider(width, horizontalOffset);
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING).add(WATERLOGGED).add(ORIENTATION).add(PART).add(SHIELDING_STATE);
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

	@Override
	public PushReaction getPistonPushReaction(BlockState state)
	{
		return PushReaction.BLOCK;
	}

	@Override
	protected void spawnDestroyParticles(Level level, Player player, BlockPos pos, BlockState state)
	{
		SoundType soundtype = state.getSoundType(level, pos, null);
		level.playLocalSound(pos, soundtype.getBreakSound(), SoundSource.BLOCKS, (soundtype.getVolume() + 1.0F) / 2.0F, soundtype.getPitch() * 0.8F, false);
	}
	
	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(state.getValue(PART).getBaseBlockPos(pos, state.getValue(FACING), state.getValue(ORIENTATION)));
		
		if(blockentity instanceof IrisStargateEntity stargate)
		{
			if(stargate.irisInfo().getIris() != null && !stargate.irisInfo().getIris().isEmpty())
				return stargate.irisInfo().getIris().copy();
		}
		
        return super.getCloneItemStack(state, target, level, pos, player);
	}

	@Override
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos position, CollisionContext context)
	{
		Direction direction = state.getValue(FACING);
		Orientation orientation = state.getValue(ORIENTATION);
		ShieldingState shieldingState = state.getValue(SHIELDING_STATE);
		
		return switch (state.getValue(PART))
		{
			// Outer parts
			case LEFT_ABOVE5, ABOVE5, RIGHT_ABOVE5 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_1) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_TOP, direction, orientation);
			case LEFT2_ABOVE4, LEFT2_ABOVE3, LEFT2_ABOVE2 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_1) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_LEFT, direction, orientation);
			case RIGHT2_ABOVE2, RIGHT2_ABOVE3, RIGHT2_ABOVE4 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_1) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_RIGHT, direction, orientation);
			case LEFT_ABOVE, ABOVE, RIGHT_ABOVE -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_1) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_BOTTOM, direction, orientation);
			
			// Inner corner parts
			case LEFT_ABOVE4 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_2) ? 
					(shieldingState.isAfter(ShieldingState.MOVING_3) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_STAIR_BOTTOM_RIGHT) : 
						shapeProvider.IRIS_CORNER_TOP_LEFT, direction, orientation);
			case RIGHT_ABOVE4 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_2) ? 
					(shieldingState.isAfter(ShieldingState.MOVING_3) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_STAIR_BOTTOM_LEFT) : 
						shapeProvider.IRIS_CORNER_TOP_RIGHT, direction, orientation);
			case LEFT_ABOVE2 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_2) ? 
					(shieldingState.isAfter(ShieldingState.MOVING_3) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_STAIR_TOP_RIGHT) : 
						shapeProvider.IRIS_CORNER_BOTTOM_LEFT, direction, orientation);
			case RIGHT_ABOVE2 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_2) ? 
					(shieldingState.isAfter(ShieldingState.MOVING_3) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_STAIR_TOP_LEFT) : 
						shapeProvider.IRIS_CORNER_BOTTOM_RIGHT, direction, orientation);

			// Inner parts
			case ABOVE4 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_3) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_TOP, direction, orientation);
			case LEFT_ABOVE3 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_3) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_LEFT, direction, orientation);
			case RIGHT_ABOVE3 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_3) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_RIGHT, direction, orientation);
			case ABOVE2 -> VoxelShapeProvider.getShapeFromArray(shieldingState.isAfter(ShieldingState.MOVING_3) ? shapeProvider.IRIS_FULL : shapeProvider.IRIS_BOTTOM, direction, orientation);
			
			default -> VoxelShapeProvider.getShapeFromArray(shapeProvider.IRIS_FULL, direction, orientation);
		};
	}

	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockPos baseBlockPos = state.getValue(PART).getBaseBlockPos(pos, state.getValue(FACING), state.getValue(ORIENTATION));
		
		BlockState stargateState = level.getBlockState(baseBlockPos);
		
		if(stargateState.getBlock() instanceof AbstractStargateBaseBlock stargateBlock)
		{
			AbstractStargateEntity stargate = stargateBlock.getStargate(level, baseBlockPos, state);
			if(stargate != null && stargate instanceof IrisStargateEntity irisStargate)
			{
				ItemStack irisStack = irisStargate.irisInfo().getIris();
				
				if(!level.isClientSide() && !player.isCreative() && !irisStack.equals(ItemStack.EMPTY))
				{
					ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, irisStack);
					itementity.setDefaultPickUpDelay();
					itementity.setUnlimitedLifetime();
					level.addFreshEntity(itementity);
				}
			}
		}
		
		/*AbstractShieldingBlock.destroyShielding(level, baseBlockPos, getShieldingParts(), state.getValue(FACING), state.getValue(ORIENTATION));
		
		if(stargateState.getBlock() instanceof AbstractStargateBaseBlock stargate)
				stargate.unsetIris(stargateState, level, baseBlockPos);*/

		super.playerWillDestroy(level, pos, state, player);
	}
	
	@Override
    public void onRemove(BlockState oldState, Level level, BlockPos pos, BlockState newState, boolean isMoving)
	{
		if(oldState.getBlock() != newState.getBlock())
		{
			ShieldingPart shieldingPart = oldState.getValue(PART);
			BlockPos baseBlockPos = shieldingPart.getBaseBlockPos(pos, oldState.getValue(FACING), oldState.getValue(ORIENTATION));
			
			BlockState stargateState = level.getBlockState(baseBlockPos);
			
			if(stargateState.getBlock() instanceof AbstractStargateBaseBlock stargateBlock)
			{
				AbstractStargateEntity stargate = stargateBlock.getStargate(level, baseBlockPos, stargateState);
				if(stargate != null && stargate instanceof IrisStargateEntity irisStargate)
				{
					if(shieldingPart.shieldingState().isBefore(irisStargate.irisInfo().getIrisProgress()))
					{
						AbstractShieldingBlock.destroyShielding(level, baseBlockPos, getShieldingParts(), oldState.getValue(FACING), oldState.getValue(ORIENTATION));
						stargateBlock.unsetIris(stargateState, level, baseBlockPos);
					}
				}
			}
			
	        super.onRemove(oldState, level, pos, newState, isMoving);
		}
    }
	
	public abstract ArrayList<ShieldingPart> getShieldingParts();
	
	public static void destroyShielding(Level level, BlockPos baseBlockPos, ArrayList<ShieldingPart> parts, Direction direction, Orientation orientation)
	{
		if(direction == null)
		{
			StargateJourney.LOGGER.error("Failed to destroy Shielding because direction is null");
			return;
		}
		
		if(orientation == null)
		{
			StargateJourney.LOGGER.error("Failed to destroy Shielding because orientation is null");
			return;
		}
		
		for(ShieldingPart part : parts)
		{
			BlockPos ringPos = part.getShieldingPos(baseBlockPos, direction, orientation);
			BlockState state = level.getBlockState(ringPos);
			
			if(state.getBlock() instanceof AbstractShieldingBlock)
			{
				boolean waterlogged = state.getValue(AbstractShieldingBlock.WATERLOGGED);
				
				level.setBlock(ringPos, waterlogged ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
			}
		}
	}
	
	public static void setIrisState(AbstractShieldingBlock irisBlock, Level level, BlockPos baseBlockPos, ArrayList<ShieldingPart> parts, Direction direction, Orientation orientation, ShieldingState shieldingState)
	{
		if(direction == null)
		{
			StargateJourney.LOGGER.error("Failed to place Shielding because direction is null");
			return;
		}
		
		if(orientation == null)
		{
			StargateJourney.LOGGER.error("Failed to place Shielding because orientation is null");
			return;
		}
		
		for(ShieldingPart part : parts)
		{
			BlockPos shieldingPos = part.getShieldingPos(baseBlockPos, direction, orientation);
			BlockState state = level.getBlockState(shieldingPos);
			
			// Remove Shielding Block
			if(state.getBlock() instanceof AbstractShieldingBlock && !part.canExist(shieldingState))
			{
				boolean waterlogged = state.getValue(AbstractShieldingBlock.WATERLOGGED);
				
				level.setBlock(shieldingPos, waterlogged ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
			}
			// Change or place new Shielding Block
			else if(part.canExist(shieldingState))
			{
				float destroySpeed = state.getDestroySpeed(level, shieldingPos);
				
				if(state.getBlock() instanceof AbstractShieldingBlock || state.is(Blocks.AIR) || state.is(Blocks.WATER))
				{
					level.setBlock(part.getShieldingPos(baseBlockPos,  direction, orientation), 
							irisBlock.defaultBlockState()
							.setValue(AbstractShieldingBlock.SHIELDING_STATE, shieldingState)
							.setValue(AbstractShieldingBlock.PART, part)
							.setValue(AbstractShieldingBlock.FACING, direction)
							.setValue(AbstractShieldingBlock.ORIENTATION, orientation)
							.setValue(AbstractShieldingBlock.WATERLOGGED,  Boolean.valueOf(level.getFluidState(part.getShieldingPos(baseBlockPos, direction, orientation)).getType() == Fluids.WATER)), 3);
				}
				else if(destroySpeed > 0 && destroySpeed < CommonIrisConfig.iris_breaking_strength.get() && !state.is(TagInit.Blocks.IRIS_RESISTANT))
				{
					level.levelEvent((Player) null, 2001, shieldingPos, getId(state)); // Spawns breaking particles and makes a breaking sound
					
					level.setBlock(part.getShieldingPos(baseBlockPos,  direction, orientation), 
							irisBlock.defaultBlockState()
							.setValue(AbstractShieldingBlock.SHIELDING_STATE, shieldingState)
							.setValue(AbstractShieldingBlock.PART, part)
							.setValue(AbstractShieldingBlock.FACING, direction)
							.setValue(AbstractShieldingBlock.ORIENTATION, orientation)
							.setValue(AbstractShieldingBlock.WATERLOGGED,  Boolean.valueOf(level.getFluidState(part.getShieldingPos(baseBlockPos, direction, orientation)).getType() == Fluids.WATER)), 3);
				}
			}
			
		}
	}
}
