package net.povstalec.sgjourney.common.blocks.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.FrontAndTop;
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
import net.minecraft.world.level.block.BaseEntityBlock;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.RenderShape;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.povstalec.sgjourney.common.block_entities.NaquadahGeneratorEntity;
import net.povstalec.sgjourney.common.menu.NaquadahGeneratorMenu;

public abstract class NaquadahGeneratorBlock extends BaseEntityBlock
{
	public static final EnumProperty<FrontAndTop> ORIENTATION = BlockStateProperties.ORIENTATION;
	
	public NaquadahGeneratorBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(ORIENTATION, FrontAndTop.NORTH_UP));
	}

	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(ORIENTATION);
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(ORIENTATION, rotation.rotation().rotate(state.getValue(ORIENTATION)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.setValue(ORIENTATION, mirror.rotation().rotate(state.getValue(ORIENTATION)));
	}

	@Override
	public BlockState getStateForPlacement(BlockPlaceContext context) 
	{
		Direction clickedDirection = context.getClickedFace();
		Direction lookingDirection;
		
		if (clickedDirection.getAxis() == Direction.Axis.Y)
			lookingDirection = context.getHorizontalDirection().getOpposite();
		else
			lookingDirection = Direction.UP;
		
		return this.defaultBlockState().setValue(ORIENTATION, FrontAndTop.fromFrontAndTop(clickedDirection, lookingDirection));
	}

	@Override
	public boolean propagatesSkylightDown(BlockState state, BlockGetter reader, BlockPos pos) 
	{
		return true;
	}

	@Override
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) 
	{
        if(!level.isClientSide()) 
        {
        	BlockEntity blockEntity = level.getBlockEntity(pos);
			
        	if (blockEntity instanceof NaquadahGeneratorEntity) 
        	{
        		MenuProvider containerProvider = new MenuProvider() 
        		{
        			@Override
        			public Component getDisplayName() 
        			{
        				return Component.translatable("screen.sgjourney.naquadah_generator");
        			}
        			
        			@Override
        			public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) 
        			{
        				return new NaquadahGeneratorMenu(windowId, playerInventory, blockEntity);
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
}
