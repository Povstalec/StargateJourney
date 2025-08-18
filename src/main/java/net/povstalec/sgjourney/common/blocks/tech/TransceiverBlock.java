package net.povstalec.sgjourney.common.blocks.tech;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Nullable;

import org.joml.Vector3d;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.util.Tuple;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.Mirror;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.minecraft.world.level.block.state.properties.EnumProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.shapes.CollisionContext;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraftforge.network.NetworkHooks;
import net.povstalec.sgjourney.common.block_entities.tech.TransceiverEntity;
import net.povstalec.sgjourney.common.blockstates.Receiving;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.menu.TransceiverMenu;
import net.povstalec.sgjourney.common.misc.VoxelShapeProvider;

public class TransceiverBlock extends Block implements EntityBlock
{
	public static final DirectionProperty FACING = BlockStateProperties.HORIZONTAL_FACING;
	
	public static final EnumProperty<Receiving> RECEIVING = EnumProperty.create("receiving", Receiving.class);
	public static final BooleanProperty TRANSMITTING = BooleanProperty.create("transmitting");
	
	private static final int TICKS_ACTIVE = 20;
	
	private static final ArrayList<Tuple<Vector3d, Vector3d>> MIN_MAX = new ArrayList<Tuple<Vector3d, Vector3d>>(Arrays.asList(
			new Tuple<Vector3d, Vector3d>(new Vector3d(2.0D, 0.0D, 3.0D), new Vector3d(14.0D, 4.0D, 16.0D)) // Base
			));
	
	private static final VoxelShape SHAPE_NORTH = VoxelShapeProvider.getDirectionalShapes(MIN_MAX, Direction.NORTH);
	private static final VoxelShape SHAPE_EAST = VoxelShapeProvider.getDirectionalShapes(MIN_MAX, Direction.EAST);
	private static final VoxelShape SHAPE_SOUTH = VoxelShapeProvider.getDirectionalShapes(MIN_MAX, Direction.SOUTH);
	private static final VoxelShape SHAPE_WEST = VoxelShapeProvider.getDirectionalShapes(MIN_MAX, Direction.WEST);
	   
	public TransceiverBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.NORTH)
				.setValue(RECEIVING, Receiving.FALSE).setValue(TRANSMITTING, false));
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		return new TransceiverEntity(pos, state);
	}
	
	@Override
	public VoxelShape getShape(BlockState state, BlockGetter getter, BlockPos pos, CollisionContext collision) 
	{
		return switch(state.getValue(FACING))
		{
		case NORTH -> SHAPE_NORTH;
		case EAST -> SHAPE_EAST;
		case SOUTH -> SHAPE_SOUTH;
		case WEST -> SHAPE_WEST;
		
		default -> SHAPE_NORTH;
		};
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING).add(RECEIVING).add(TRANSMITTING);
	}
	
	@Override
	public BlockState rotate(BlockState state, Rotation rotation)
	{
		return state.setValue(FACING, rotation.rotation().rotate(state.getValue(FACING)));
	}

	@Override
	public BlockState mirror(BlockState state, Mirror mirror)
	{
		return state.setValue(FACING, mirror.rotation().rotate(state.getValue(FACING)));
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
        	BlockEntity blockEntity = level.getBlockEntity(pos);
			
        	if(blockEntity instanceof TransceiverEntity) 
        	{
        		MenuProvider containerProvider = new MenuProvider() 
        		{
        			@Override
        			public Component getDisplayName() 
        			{
        				return Component.translatable("screen.sgjourney.transceiver");
        			}
        			
        			@Override
        			public AbstractContainerMenu createMenu(int windowId, Inventory playerInventory, Player playerEntity) 
        			{
        				return new TransceiverMenu(windowId, playerInventory, blockEntity);
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
	
	@Override
	public boolean hasAnalogOutputSignal(BlockState state)
	{
		return true;
	}
	
	@Override
	public void tick(BlockState state, ServerLevel level, BlockPos pos, RandomSource source)
	{
		level.setBlock(pos, state.setValue(RECEIVING, Receiving.FALSE), 3);
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
	{
		Receiving receiving = state.getValue(RECEIVING);
		if(receiving != null)
			return receiving.getRedstonePower();
		
		return 0;
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean bool)
	{
		if(level.isClientSide())
			return;
		
		if(level.hasNeighborSignal(pos))
		{
			BlockEntity blockEntity = level.getBlockEntity(pos);
			
			if(blockEntity instanceof TransceiverEntity transceiver)
				transceiver.sendTransmission();
		}
		
		super.neighborChanged(state, level, pos, block, pos2, bool);
	}
	
	public void receiveTransmission(BlockState state, Level level, BlockPos pos, boolean codeIsCorrect)
	{
		if(codeIsCorrect)
			level.setBlock(pos, state.setValue(RECEIVING, Receiving.RECEIVING_CORRECT), 3);
		else
			level.setBlock(pos, state.setValue(RECEIVING, Receiving.RECEIVING_INCORRECT), 3);
		level.scheduleTick(pos, this, TICKS_ACTIVE);
	}
	
	public void stopTransmitting(BlockState state, Level level, BlockPos pos)
	{
		level.setBlock(pos, state.setValue(TRANSMITTING, false), 2);
	}
	
	@Override
    public void appendHoverText(ItemStack stack, @Nullable BlockGetter getter, List<Component> tooltipComponents, TooltipFlag isAdvanced)
    {
		tooltipComponents.add(Component.translatable("block.sgjourney.transceiver.description").withStyle(ChatFormatting.GRAY));
    }
	
	@SuppressWarnings("unchecked")
	@Nullable
	protected static <E extends BlockEntity, A extends BlockEntity> BlockEntityTicker<A> createTickerHelper(BlockEntityType<A> typeA, BlockEntityType<E> typeB, BlockEntityTicker<? super E> ticker)
	{
		return typeB == typeA ? (BlockEntityTicker<A>)ticker : null;
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.TRANSCEIVER.get(), TransceiverEntity::tick);
    }
}
