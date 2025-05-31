package net.povstalec.sgjourney.common.blocks.tech;

import javax.annotation.Nullable;

import com.mojang.serialization.MapCodec;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.level.block.state.properties.DirectionProperty;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.block_entities.tech.TransportRingsEntity;
import net.povstalec.sgjourney.common.blocks.ProtectedBlock;
import net.povstalec.sgjourney.common.init.BlockEntityInit;


public class TransportRingsBlock extends AbstractTransporterBlock implements ProtectedBlock
{
	public static final DirectionProperty FACING = DirectionProperty.create("facing", Direction.Plane.VERTICAL);
	public static final BooleanProperty ACTIVATED = BooleanProperty.create("activated");

	public static final MapCodec<TransportRingsBlock> CODEC = simpleCodec(TransportRingsBlock::new);

	public TransportRingsBlock(Properties properties) 
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(FACING, Direction.UP).setValue(ACTIVATED, false));
	}

	protected MapCodec<TransportRingsBlock> codec() {
		return CODEC;
	}

	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(FACING).add(ACTIVATED);
	}
	
	public BlockState getStateForPlacement(BlockPlaceContext context)
	{
	      return this.defaultBlockState().setValue(FACING, context.getNearestLookingVerticalDirection().getOpposite());
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state)
	{
		return new TransportRingsEntity(pos, state);
	}
	
	@Nullable
	@Override
	public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.TRANSPORT_RINGS.get(), TransportRingsEntity::tick);
	}

	@Override
	public ProtectedBlockEntity getProtectedBlockEntity(BlockGetter level, BlockPos pos, BlockState state) {
		BlockEntity blockEntity = level.getBlockEntity(pos);
		if(blockEntity instanceof TransportRingsEntity transportRingsEntity)
			return transportRingsEntity;

		return null;
	}

	@Override
	public boolean hasPermissions(BlockGetter level, BlockPos pos, BlockState state, Player player, boolean sendMessage) {
		ProtectedBlockEntity blockEntity = getProtectedBlockEntity(level, pos, state);
		if(blockEntity instanceof TransportRingsEntity transportRingsEntity)
			return transportRingsEntity.hasPermissions(player, sendMessage);

		return true;
	}
}
