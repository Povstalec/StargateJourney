package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.common.block_entities.SGJourneyBlockEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateRingBlock;
import net.povstalec.sgjourney.common.blocks.stargate.ClassicStargateBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.stargate.Address;

public class ClassicStargateBaseBlock extends HorizontalDirectionalBlock
{
	public ClassicStargateBaseBlock(Properties properties)
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
	public BlockState getStateForPlacement(BlockPlaceContext context) 
	{
		return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
	{
		if(!level.isClientSide())
		{
			ItemStack stack = player.getItemInHand(hand);
			Address address = new Address();
			
			if(CommonStargateConfig.enable_address_choice.get() && stack.is(ItemInit.CONTROL_CRYSTAL.get()))
			{
				String name = stack.getHoverName().getString();
				address = new Address(name);
				
				if(address.getLength() != 8)
				{
					player.displayClientMessage(Component.translatable("block.sgjourney.stargate.classic.invalid_address"), true);
					return InteractionResult.FAIL;
				}
				
				if(BlockEntityList.get(level).getStargate(address.immutable()).isPresent())
				{
					player.displayClientMessage(Component.translatable("block.sgjourney.stargate.classic.address_exists"), true);
					return InteractionResult.FAIL;
				}
			}
			
			Direction direction = level.getBlockState(pos).getValue(FACING);
			Orientation orientation = getPlacementOrientation(level, pos, direction);
			
			if(orientation == null)
			{
				player.displayClientMessage(Component.translatable("block.sgjourney.stargate.classic.incorrect_setup"), true);
				return InteractionResult.FAIL;
			}
			
			ClassicStargateBlock block = BlockInit.CLASSIC_STARGATE.get();
			level.setBlock(pos, block.defaultBlockState()
					.setValue(ClassicStargateBlock.FACING, direction)
					.setValue(AbstractStargateRingBlock.ORIENTATION, orientation), 3);
			
			for(StargatePart part : block.getParts())
			{
				if(!part.equals(StargatePart.BASE))
				{
					level.setBlock(part.getRingPos(pos, direction, orientation), 
							BlockInit.CLASSIC_RING.get().defaultBlockState()
							.setValue(AbstractStargateRingBlock.PART, part)
							.setValue(AbstractStargateRingBlock.FACING, direction)
							.setValue(AbstractStargateRingBlock.ORIENTATION, orientation), 3);
				}
			}
			
			BlockEntity baseEntity = level.getBlockEntity(pos);
			
			if(baseEntity instanceof SGJourneyBlockEntity blockEntity)
			{
				if(address.getLength() == 8)
				{
					blockEntity.setID(address.toString());
					
					if(!player.isCreative())
						stack.shrink(1);
				}
				
				blockEntity.addToBlockEntityList();
			}
			
			return InteractionResult.SUCCESS;
		}
		
		return InteractionResult.SUCCESS;
	}
	
	private static Block getClassicStargateBlock(StargatePart part)
	{
		Block block;
		switch(part)
		{
		case LEFT, LEFT3_ABOVE, LEFT3_ABOVE4, LEFT2_ABOVE6, ABOVE6, RIGHT2_ABOVE6, RIGHT3_ABOVE4, RIGHT3_ABOVE, RIGHT -> block = BlockInit.CLASSIC_STARGATE_CHEVRON_BLOCK.get();
		default -> block = BlockInit.CLASSIC_STARGATE_RING_BLOCK.get();
		}
		
		return block;
	}
	
	private static boolean checkParts(Level level, BlockPos pos, Direction direction, Orientation orientation)
	{
		ClassicStargateBlock block = BlockInit.CLASSIC_STARGATE.get();
		for(StargatePart part : block.getParts())
		{
			if(!part.equals(StargatePart.BASE) && !(level.getBlockState(part.getRingPos(pos, direction, orientation))
					.is(getClassicStargateBlock(part))))
				return false;
		}
		
		return true;
	}
	
	private static Orientation getPlacementOrientation(Level level, BlockPos pos, Direction direction)
	{
		for(Orientation orientation : Orientation.values())
		{
			if(checkParts(level, pos, direction, orientation))
				return orientation;
		}
		
		return null;
	}
}
