package net.povstalec.sgjourney.common.blocks.tech;

import java.util.List;

import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
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
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraftforge.network.NetworkHooks;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.MilkyWayStargateEntity;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.blockstates.InterfaceMode;
import net.povstalec.sgjourney.common.menu.InterfaceMenu;

public abstract class AbstractInterfaceBlock extends BaseEntityBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.FACING;
	public static final BooleanProperty UPDATE = BooleanProperty.create("update");
	public static final EnumProperty<InterfaceMode> MODE = EnumProperty.create("mode", InterfaceMode.class);
	
	protected AbstractInterfaceBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH).setValue(UPDATE, false).setValue(MODE, InterfaceMode.OFF));
	}
	 
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING).add(UPDATE).add(MODE);
	}
	 
	public BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotate(state.getValue(FACING)));
	}

	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.rotate(mirror.getRotation(state.getValue(FACING)));
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
	      return this.defaultBlockState().setValue(FACING, context.getNearestLookingDirection());
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult trace) 
	{
        if(!level.isClientSide()) 
        {
			BlockEntity blockEntity = level.getBlockEntity(pos);
			if(blockEntity instanceof AbstractInterfaceEntity interfaceEntity) 
        	{
        		if(!player.isShiftKeyDown())
        		{
        			MenuProvider containerProvider = new MenuProvider() 
            		{
            			@Override
            			public Component getDisplayName() 
            			{
            				return Component.translatable("screen.sgjourney." + interfaceEntity.getInterfaceType().getName());
            			}
            			
            			@Override
            			public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) 
            			{
            				return new InterfaceMenu(windowId, playerInventory, blockEntity);
            			}
            		};
            		NetworkHooks.openScreen((ServerPlayer) player, containerProvider, blockEntity.getBlockPos());
        		}
        		else if(player.isShiftKeyDown() && player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
        			level.setBlock(pos, state.cycle(MODE), 3);
        	}
        	else
        	{
        		throw new IllegalStateException("Our named container provider is missing!");
        	}
        }
        return InteractionResult.SUCCESS;
    }
	
	public RenderShape getRenderShape(BlockState state)
	{
		return RenderShape.MODEL;
	}
	
	public abstract Block getDroppedBlock();
	
	public abstract long getCapacity();
	
	@Override
	public void playerWillDestroy(Level level, BlockPos pos, BlockState state, Player player)
	{
		BlockEntity blockentity = level.getBlockEntity(pos);
		if(blockentity instanceof AbstractInterfaceEntity)
		{
			if(!level.isClientSide() && !player.isCreative())
			{
				ItemStack itemstack = new ItemStack(getDroppedBlock());
				
				blockentity.saveToItem(itemstack);

				ItemEntity itementity = new ItemEntity(level, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, itemstack);
				itementity.setDefaultPickUpDelay();
				level.addFreshEntity(itementity);
			}
		}

		super.playerWillDestroy(level, pos, state, player);
	}
	
	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource source)
	{
		level.setBlock(pos, state.setValue(UPDATE, false), 3);
	}
	
	public void updateInterface(BlockState state, Level level, BlockPos pos)
	{
		level.setBlock(pos, state.setValue(BasicInterfaceBlock.UPDATE, true), 3);
		level.scheduleTick(pos, this, 2);
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean bool)
	{
		if(level.isClientSide())
			return;
		
		Direction direction = state.getValue(FACING);
		BlockPos targetPos = pos.relative(direction);
		
		if(targetPos.equals(pos2) && level.getBlockEntity(pos) instanceof AbstractInterfaceEntity interfaceEntity && interfaceEntity.updateInterface(level, targetPos, block, state))
			level.updateNeighborsAtExceptFromFacing(pos, state.getBlock(), state.getValue(FACING));
	}
	
	private int getRotationOutput(EnergyBlockEntity blockEntity)
	{
		if(blockEntity instanceof MilkyWayStargateEntity stargate)
			return stargate.getCurrentSymbol() / 3;
		return 0;
	}
	
	private int getChevronOutput(EnergyBlockEntity blockEntity)
	{
		if(blockEntity instanceof AbstractStargateEntity stargate)
			return stargate.getChevronsEngaged();
		return 0;
	}
	
	private int getConnectionOutput(EnergyBlockEntity blockEntity)
	{
		if(blockEntity instanceof AbstractStargateEntity stargate)
			return stargate.isConnected() ? 15 : 0;
		return 0;
	}
	
	public int comparatorOutput(BlockState state, EnergyBlockEntity blockEntity)
	{
		switch(state.getValue(MODE))
		{
		case RING_ROTATION:
			return getRotationOutput(blockEntity);
		case CHEVRONS_ACTIVE:
			return getChevronOutput(blockEntity);
		case WORMHOLE_ACTIVE:
			return getConnectionOutput(blockEntity);
		default:
			return 0;
		}
	}
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state)
	{
		return true;
	}

	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
	{
		BlockEntity entity = level.getBlockEntity(pos);

		if(entity instanceof AbstractInterfaceEntity interfaceEntity)
			return comparatorOutput(state, interfaceEntity.energyBlockEntity);

		return 0;
	}
	
    @Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
    	int energy = 0;
    	
		if(stack.hasTag() && stack.getTag().getCompound("BlockEntityTag").contains("Energy"))
			energy = stack.getTag().getCompound("BlockEntityTag").getInt("Energy");
		
        tooltipComponents.add(Component.translatable("tooltip.sgjourney.energy").append(Component.literal(": " + energy + "/" + getCapacity() +" FE")).withStyle(ChatFormatting.DARK_RED));
        super.appendHoverText(stack, getter, tooltipComponents, isAdvanced);
    }
}
