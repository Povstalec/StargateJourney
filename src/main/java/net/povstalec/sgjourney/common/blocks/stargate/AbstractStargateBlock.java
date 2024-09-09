package net.povstalec.sgjourney.common.blocks.stargate;

import java.util.ArrayList;
import java.util.Optional;

import org.jetbrains.annotations.Nullable;

import com.google.common.collect.ImmutableMap;
import com.mojang.serialization.MapCodec;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.SimpleWaterloggedBlock;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.level.block.state.properties.IntegerProperty;
import net.minecraft.world.level.block.state.properties.Property;
import net.minecraft.world.level.material.FluidState;
import net.minecraft.world.level.material.Fluids;
import net.minecraft.world.level.material.PushReaction;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.common.ForgeHooks;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.shielding.AbstractShieldingBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.ShieldingPart;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.items.StargateIrisItem;
import net.povstalec.sgjourney.common.misc.CoverBlockPlaceContext;
import net.povstalec.sgjourney.common.misc.VoxelShapeProvider;
import net.povstalec.sgjourney.common.stargate.StargateBlockCover;
import net.povstalec.sgjourney.common.stargate.StargateConnection;

public abstract class AbstractStargateBlock extends Block implements SimpleWaterloggedBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	public static final EnumProperty<Orientation> ORIENTATION = EnumProperty.create("orientation", Orientation.class);
	public static final EnumProperty<StargatePart> PART = EnumProperty.create("stargate_part", StargatePart.class);
	public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;
	public static final EnumProperty<StargateConnection.State> CONNECTION_STATE = EnumProperty.create("connection_state", StargateConnection.State.class);
	public static final IntegerProperty CHEVRONS_ACTIVE = IntegerProperty.create("chevrons_active", 0, 9);

	protected VoxelShapeProvider shapeProvider;
	protected StateDefinition<Block, BlockState> stargateStateDefinition;

	public AbstractStargateBlock(Properties properties, double width, double horizontalOffset)
	{
		super(properties);
		
		final StateDefinition.Builder<Block, BlockState> stateDefinitionBuilder = new StateDefinition.Builder<>(this);
		this.createBlockStateDefinition(stateDefinitionBuilder);
		
		this.stargateStateDefinition = stateDefinitionBuilder.create(Block::defaultBlockState, StargateBlockState::new);
		
		this.registerDefaultState(this.stargateStateDefinition.any().setValue(FACING, Direction.NORTH)
				.setValue(ORIENTATION, Orientation.REGULAR).setValue(CONNECTION_STATE, StargateConnection.State.IDLE)
				.setValue(CHEVRONS_ACTIVE, 0).setValue(WATERLOGGED, Boolean.valueOf(false)).setValue(PART, StargatePart.BASE));
		shapeProvider = new VoxelShapeProvider(width, horizontalOffset);
	}

	public StateDefinition<Block, BlockState> getStateDefinition()
	{
		return this.stargateStateDefinition;
	}

	public ArrayList<StargatePart> getParts(boolean shielded)
	{
		return shielded ? StargatePart.DEFAULT_SHIELDED_PARTS : StargatePart.DEFAULT_PARTS;
	}

	public ArrayList<StargatePart> getParts()
	{
		return getParts(false);
	}

	public ArrayList<ShieldingPart> getShieldingParts()
	{
		return ShieldingPart.DEFAULT_PARTS;
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
	
	protected VoxelShape shape(BlockState state, BlockGetter reader, BlockPos position, CollisionContext context)
	{
		if(state.getValue(ORIENTATION) != Orientation.REGULAR)
			return shapeProvider.HORIZONTAL_FULL;
		return state.getValue(FACING).getAxis() == Direction.Axis.X ? shapeProvider.Z_FULL : shapeProvider.X_FULL;
	}
	
	public Optional<StargateBlockCover> getBlockCover(BlockGetter reader, BlockState state, BlockPos position)
	{
		AbstractStargateEntity stargate = getStargate(reader, position, state);
		if(stargate != null)
		{
			return Optional.of(stargate.blockCover);
		}
		
		return Optional.empty();
	}
	
	/*public Optional<StargateBlockCover> getBlockCover(Level level, BlockState state, BlockPos position)
	{
		AbstractStargateEntity stargate = getStargate(level, position, state);
		if(stargate != null)
			return Optional.of(stargate.blockCover);
		
		return Optional.empty();
	}*/
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos pos, CollisionContext context)
	{
		Optional<StargateBlockCover> blockCover = getBlockCover(reader, state, pos);
		
		if(blockCover.isPresent())
		{
			StargatePart part = state.getValue(PART);
			Optional<BlockState> coverState = blockCover.get().getBlockAt(part);
			
			if(coverState.isPresent())
				return Shapes.or(shape(state, reader, pos, context), coverState.get().getShape(reader, pos));
		}
		
		return shape(state, reader, pos, context);
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
		AbstractStargateEntity stargate = getStargate(level, pos, state);
		if(stargate != null)
		{
			if(!level.isClientSide() && !player.isCreative())
			{
				ItemStack itemstack = new ItemStack(asItem());

				stargate.saveToItem(itemstack);

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				itementity.setUnlimitedLifetime();
				level.addFreshEntity(itementity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}
	
	public void destroyStargate(Level level, BlockPos pos, ArrayList<StargatePart> parts, ArrayList<ShieldingPart> shieldingParts, Direction direction, Orientation orientation, StargatePart stargatePart)
	{
		if(direction == null)
		{
			StargateJourney.LOGGER.error("Failed to destroy Stargate because direction is null");
			return;
		}
		
		if(orientation == null)
		{
			StargateJourney.LOGGER.error("Failed to destroy Stargate because orientation is null");
			return;
		}
		
		AbstractShieldingBlock.destroyShielding(level, pos, shieldingParts, direction, orientation);
		
		for(StargatePart part : parts)
		{
			if(!stargatePart.equals(part))
			{
				BlockPos ringPos = part.getRingPos(pos, direction, orientation);
				BlockState state = level.getBlockState(ringPos);
				
				if(state.getBlock() instanceof AbstractStargateBlock)
				{
					boolean waterlogged = state.getBlock() instanceof AbstractStargateRingBlock ? state.getValue(AbstractStargateRingBlock.WATERLOGGED) : false;
					
					level.setBlock(ringPos, waterlogged ? Blocks.WATER.defaultBlockState() : Blocks.AIR.defaultBlockState(), 3);
				}
			}
		}
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
	
	public abstract AbstractStargateEntity getStargate(BlockGetter reader, BlockPos pos, BlockState state);
	
	public boolean setCover(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
	{
		if(!player.isShiftKeyDown())
		{
			Optional<StargateBlockCover> blockCover = getBlockCover(level, state, pos);
			
			if(blockCover.isPresent())
			{
				StargatePart part = state.getValue(PART);
				ItemStack stack = player.getItemInHand(InteractionHand.MAIN_HAND);
				
				if(stack.getItem() instanceof BlockItem blockItem && blockCover.get().getBlockAt(part).isEmpty())
				{
					CoverBlockPlaceContext context = new CoverBlockPlaceContext(level, player, InteractionHand.MAIN_HAND, stack, result);
					BlockState coverState = blockItem.getBlock().getStateForPlacement(context);
					
					if(coverState != null && !(coverState.getBlock() instanceof EntityBlock))
					{
						if(blockCover.get().setBlockAt(part, coverState))
						{
							level.playSound(player, pos, coverState.getBlock().getSoundType(coverState).getPlaceSound(), SoundSource.BLOCKS);
							
							if(!player.isCreative())
								stack.shrink(1);
							
							return true;
						}
					}
				}
			}
		}
		
		return false;
	}
	
	public boolean setIris(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
	{
		ItemStack stack = player.getItemInHand(hand);
		if(stack.getItem() instanceof StargateIrisItem && !level.isClientSide())
		{
			AbstractStargateEntity stargate = getStargate(level, pos, state);
			if(stargate != null )
			{
				if(stargate.addIris(stack))
				{
					if(!player.isCreative())
						player.getItemInHand(hand).shrink(1);
					return true;
				}
				else
					player.displayClientMessage(Component.translatable("message.sgjourney.stargate.error.already_has_iris"), true);;
			}
		}
		
		return false;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
	{
		if(setCover(state, level, pos, player, hand, result))
			return InteractionResult.SUCCESS;
		else if(setIris(state, level, pos, player, hand, result))
			return InteractionResult.SUCCESS;
		
		return super.use(state, level, pos, player, hand, result);
	}
	
	@Override
	public boolean onDestroyedByPlayer(BlockState state, Level level, BlockPos pos, Player player, boolean willHarvest, FluidState fluid)
    {
		Optional<StargateBlockCover> blockCover = getBlockCover(level, state, pos);
		
		if(blockCover.isPresent() && !blockCover.get().blockStates.isEmpty())
		{
			StargatePart part = state.getValue(AbstractStargateBlock.PART);
			
			if(blockCover.get().mineBlockAt(level, player, part, pos))
				return false;
		}
		
        return super.onDestroyedByPlayer(state, level, pos, player, willHarvest, fluid);
    }
	
	@Override
	public ItemStack getCloneItemStack(BlockState state, HitResult target, BlockGetter level, BlockPos pos, Player player)
	{
		Optional<StargateBlockCover> blockCover = getBlockCover(level, state, pos);
		
		if(blockCover.isPresent() && !blockCover.get().blockStates.isEmpty())
		{
			StargatePart part = state.getValue(AbstractStargateBlock.PART);
			
			ItemStack stack = blockCover.get().getStackAt(target, level, player, part, pos);
			
			if(!stack.isEmpty())
				return stack;
		}
		
        return super.getCloneItemStack(state, target, level, pos, player);
	}
	
	public static class StargateBlockState extends BlockState
	{

		public StargateBlockState(Block block, ImmutableMap<Property<?>, Comparable<?>> properties,
				MapCodec<BlockState> states)
		{
			super(block, properties, states);
		}

		@Override
		public float getDestroySpeed(BlockGetter reader, BlockPos pos)
		{
			if(this.getBlock() instanceof AbstractStargateBlock stargate)
			{
				Optional<StargateBlockCover> blockCover = stargate.getBlockCover(reader, this, pos);
				
				if(blockCover.isPresent())
				{
					StargatePart part = this.getValue(PART);
					Optional<BlockState> coverState = blockCover.get().getBlockAt(part);
					
					if(coverState.isPresent()) // Destroy speed for the cover block
						return coverState.get().getDestroySpeed(reader, pos);
				}
			}
			
			return super.getDestroySpeed(reader, pos);
		}
		
		@Override
		public float getDestroyProgress(Player player, BlockGetter reader, BlockPos pos)
		{
			float destroySpeed = getDestroySpeed(reader, pos);
			if(destroySpeed == -1.0F)
				return 0.0F;
			
			if(this.getBlock() instanceof AbstractStargateBlock stargate)
			{
				Optional<StargateBlockCover> blockCover = stargate.getBlockCover(reader, this, pos);
				
				if(blockCover.isPresent())
				{
					StargatePart part = this.getValue(PART);
					Optional<BlockState> coverState = blockCover.get().getBlockAt(part);
					
					if(coverState.isPresent())
					{
						float multiplier = ForgeHooks.isCorrectToolForDrops(coverState.get(), player) ? 30F : 100F;
						
						return player.getDigSpeed(coverState.get(), pos) / destroySpeed / multiplier;
					}
				}
			}
			
			return super.getDestroyProgress(player, reader, pos);
		}
		
		@Override
		public SoundType getSoundType(LevelReader level, BlockPos pos, @Nullable Entity entity)
		{
			BlockState state = level.getBlockState(pos);
			
			if(state.getBlock() instanceof AbstractStargateBlock stargate)
			{
				Optional<StargateBlockCover> blockCover = stargate.getBlockCover(level, state, pos);
				
				if(blockCover.isPresent())
				{
					StargatePart part = state.getValue(PART);
					Optional<BlockState> coverState = blockCover.get().getBlockAt(part);
					
					if(coverState.isPresent()) // Destroy speed for the cover block
						return coverState.get().getSoundType(level, pos, entity);
				}
			}
			
			return super.getSoundType(level, pos, entity);
			
		}
	}
}
