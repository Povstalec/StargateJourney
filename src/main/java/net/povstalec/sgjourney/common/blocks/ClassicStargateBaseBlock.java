package net.povstalec.sgjourney.common.blocks;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
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
import net.povstalec.sgjourney.common.block_entities.stargate.ClassicStargateEntity;
import net.povstalec.sgjourney.common.blocks.stargate.AbstractStargateRingBlock;
import net.povstalec.sgjourney.common.blocks.stargate.ClassicStargateBlock;
import net.povstalec.sgjourney.common.blockstates.Orientation;
import net.povstalec.sgjourney.common.blockstates.StargatePart;
import net.povstalec.sgjourney.common.config.CommonStargateConfig;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

public class ClassicStargateBaseBlock extends HorizontalDirectionalBlock
{
	public static final MapCodec<ClassicStargateBaseBlock> CODEC = simpleCodec(ClassicStargateBaseBlock::new);

	public ClassicStargateBaseBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH));
	}

	protected MapCodec<ClassicStargateBaseBlock> codec() {
		return CODEC;
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
	public ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		if(!level.isClientSide())
		{
			Address address = new Address();
			
			if(CommonStargateConfig.enable_address_choice.get() && stack.is(ItemInit.CONTROL_CRYSTAL.get()))
			{
				String name = stack.getHoverName().getString();
				address = new Address(name);
				
				if(address.getLength() != 8)
				{
					player.displayClientMessage(Component.translatable("block.sgjourney.stargate.classic.invalid_address"), true);
					return ItemInteractionResult.FAIL;
				}
				
				if(BlockEntityList.get(level).containsStargate(address.immutable()))
				{
					player.displayClientMessage(Component.translatable("block.sgjourney.stargate.classic.address_exists"), true);
					return ItemInteractionResult.FAIL;
				}
			}
			
			Direction direction = level.getBlockState(pos).getValue(FACING);
			Orientation orientation = getPlacementOrientation(level, pos, direction);
			
			if(orientation == null)
			{
				player.displayClientMessage(Component.translatable("block.sgjourney.stargate.classic.incorrect_setup"), true);
				return ItemInteractionResult.FAIL;
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
			
			if(baseEntity instanceof ClassicStargateEntity stargate)
			{
				if(address.getLength() == 8)
				{
					stargate.set9ChevronAddress(address);
					
					if(!player.isCreative())
						stack.shrink(1);
				}

				stargate.symbolInfo().setPointOfOrigin(PointOfOrigin.randomPointOfOrigin(level.getServer(), level.dimension()));
				stargate.symbolInfo().setSymbols(Symbols.fromDimension(level.getServer(), level.dimension()));
				stargate.displayID();
				stargate.addStargateToNetwork();
			}
			
			return ItemInteractionResult.SUCCESS;
		}
		
		return ItemInteractionResult.SUCCESS;
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
