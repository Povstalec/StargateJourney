package net.povstalec.sgjourney.common.blocks;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.EntityBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.common.block_entities.TransceiverEntity;

public class TransceiverBlock extends Block implements EntityBlock
{
	public static final BooleanProperty POWERED = BlockStateProperties.POWERED;
	
	private static final int TICKS_ACTIVE = 20;
	   
	public TransceiverBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, false));
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		return new TransceiverEntity(pos, state);
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(POWERED);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
	{
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
		level.setBlock(pos, state.setValue(POWERED, false), 3);
	}
	
	@Override
	public int getAnalogOutputSignal(BlockState state, Level level, BlockPos pos)
	{
		return state.getValue(POWERED) ? 15 : 0;
	}
	
	@Override
	public void neighborChanged(BlockState state, Level level, BlockPos pos, Block block, BlockPos pos2, boolean bool)
	{
		/*if(level.isClientSide)
			return;
		
		int newSignalStrength = level.getBestNeighborSignal(pos);
		
		if(newSignalStrength > signalStrength || newSignalStrength == 0)
			signalStrength = newSignalStrength;
		
		if(level.hasNeighborSignal(pos) && signalStrength == newSignalStrength)
		{
			int measured = detectATAGene(state, level, pos, (double) signalStrength * 2);
			
			if(state.getValue(MEASURED_GENE) != measured)
				level.setBlock(pos, state.setValue(MEASURED_GENE, measured), 3);
		}*/
		super.neighborChanged(state, level, pos, block, pos2, bool);
	}
	
	public void receiveTransmission(BlockState state, Level level, BlockPos pos)
	{
		level.setBlock(pos, state.setValue(POWERED, true), 3);
		level.scheduleTick(pos, this, TICKS_ACTIVE);
	}
}
