package net.povstalec.sgjourney.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
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
import net.minecraftforge.network.NetworkHooks;
import net.povstalec.sgjourney.common.block_entities.RingPanelEntity;
import net.povstalec.sgjourney.common.menu.RingPanelMenu;


public class RingPanelBlock extends HorizontalDirectionalBlock implements EntityBlock
{
	protected static final VoxelShape NORTH = Block.box(2.0D, 0.0D, 13.0D, 14.0D, 16.0D, 16.0D);
	protected static final VoxelShape SOUTH = Block.box(2.0D, 0.0D, 0.0D, 14.0D, 16.0D, 3.0D);
	protected static final VoxelShape EAST = Block.box(0.0D, 0.0D, 2.0D, 3.0D, 16.0D, 14.0D);
	protected static final VoxelShape WEST = Block.box(13.0D, 0.0D, 2.0D, 16.0D, 16.0D, 14.0D);
	
	public RingPanelBlock(Properties properties) 
	{
		super(properties);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		return new RingPanelEntity(pos, state);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) 
	{
        if (!level.isClientSide()) 
        {
        	BlockEntity blockEntity = level.getBlockEntity(pos);
			
        	if (blockEntity instanceof RingPanelEntity panel) 
        	{
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
        		NetworkHooks.openScreen((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
        	}
        	else
        	{
        		throw new IllegalStateException("Our named container provider is missing!");
        	}
        }
        return InteractionResult.SUCCESS;
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
	
}
