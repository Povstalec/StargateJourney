package net.povstalec.sgjourney.common.blocks;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.ItemInteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelReader;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.block_entities.RingPanelEntity;
import net.povstalec.sgjourney.common.menu.RingPanelMenu;
import net.povstalec.sgjourney.common.misc.NetworkUtils;


public class RingPanelBlock extends HorizontalDirectionalBlock implements EntityBlock, ProtectedBlock
{
	protected static final VoxelShape NORTH = Block.box(2.0D, 0.0D, 13.0D, 14.0D, 16.0D, 16.0D);
	protected static final VoxelShape SOUTH = Block.box(2.0D, 0.0D, 0.0D, 14.0D, 16.0D, 3.0D);
	protected static final VoxelShape EAST = Block.box(0.0D, 0.0D, 2.0D, 3.0D, 16.0D, 14.0D);
	protected static final VoxelShape WEST = Block.box(13.0D, 0.0D, 2.0D, 16.0D, 16.0D, 14.0D);

	public static final MapCodec<RingPanelBlock> CODEC = simpleCodec(RingPanelBlock::new);

	public RingPanelBlock(Properties properties) 
	{
		super(properties);
	}

	protected MapCodec<RingPanelBlock> codec() {
		return CODEC;
	}

	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		return new RingPanelEntity(pos, state);
	}

	public void use(Level level, BlockPos pos, Player player)
	{
        if (!level.isClientSide()) 
        {
        	BlockEntity blockEntity = level.getBlockEntity(pos);
			
        	if (blockEntity instanceof RingPanelEntity panel) 
        	{
        		panel.setTransportRings();
        		panel.getNearest6Rings(level, pos, 32768);
				
        		MenuProvider containerProvider = new MenuProvider() 
        		{
        			@Override
        			public Component getDisplayName() 
        			{
        				return Component.translatable("screen.sgjourney.transport_rings");
        			}
        			
        			@Override
        			public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) 
        			{
        				return new RingPanelMenu(windowId, playerInventory, blockEntity);
        			}
        		};
				NetworkUtils.openMenu((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
        	}
        	else
        	{
        		throw new IllegalStateException("Our named container provider is missing!");
        	}
        }
    }

	@Override
	protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos, Player player, BlockHitResult hitResult)
	{
		use(level, pos, player);

		return InteractionResult.SUCCESS;
	}

	@Override
	protected ItemInteractionResult useItemOn(ItemStack stack, BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult hitResult)
	{
		use(level, pos, player);

		return ItemInteractionResult.SUCCESS;
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
	      return this.defaultBlockState().setValue(FACING, context.getHorizontalDirection().getOpposite());
	}
	 
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING);
	}
	
	public boolean canSurvive(BlockState state, LevelReader reader, BlockPos pos)
	{
		Direction direction = state.getValue(FACING);
		BlockPos blockpos = pos.relative(direction.getOpposite());
		BlockState blockstate = reader.getBlockState(blockpos);
		
		return blockstate.isFaceSturdy(reader, blockpos, direction);
	}
	
	public VoxelShape getShape(BlockState state, BlockGetter reader, BlockPos position, CollisionContext context)
	{
		switch(state.getValue(FACING))
		{
		case EAST:
			return EAST;
		case SOUTH:
			return SOUTH;
		case WEST:
			return WEST;
		default:
			return NORTH;
		}
	}

	@Override
	public ProtectedBlockEntity getProtectedBlockEntity(BlockGetter level, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(blockEntity instanceof RingPanelEntity ringPanelEntity)
			return ringPanelEntity;

		return null;
	}

	@Override
	public boolean hasPermissions(BlockGetter level, BlockPos pos, BlockState state, Player player, boolean sendMessage) {
		ProtectedBlockEntity blockEntity = getProtectedBlockEntity(level, pos, state);
		if(blockEntity instanceof RingPanelEntity ringPanelEntity)
			return ringPanelEntity.hasPermissions(player, sendMessage);

		return true;
	}

}
