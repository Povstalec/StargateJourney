package net.povstalec.sgjourney.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.StateDefinition;
import net.minecraft.world.level.block.state.properties.BooleanProperty;
import net.minecraft.world.phys.BlockHitResult;

public class SecretSwitchBlock extends Block
{
	public static final BooleanProperty POWERED = BooleanProperty.create("powered");

	public SecretSwitchBlock(Properties properties)
	{
		super(properties);
		this.registerDefaultState(this.stateDefinition.any().setValue(POWERED, Boolean.valueOf(false)));
	}
	
	@Override
	protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> state)
	{
		state.add(POWERED);
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
	{
		if(player.getItemInHand(InteractionHand.MAIN_HAND).isEmpty())
		{
			if(level.isClientSide())
				return InteractionResult.SUCCESS;
			
			level.setBlock(pos, state.cycle(POWERED), 3);
			return InteractionResult.CONSUME;
		}
		else
			return InteractionResult.FAIL;
	}
	
	@Override
	public void onRemove(BlockState state, Level level, BlockPos pos, BlockState state2, boolean p_54651_)
	{
	      if (!p_54651_ && !state.is(state2.getBlock()))
	      {
	         if (state.getValue(POWERED))
	         {
	            this.updateNeighbours(state, level, pos);
	         }

	         super.onRemove(state, level, pos, state2, p_54651_);
	      }
	   }
	
		@Override
	   public int getSignal(BlockState state, BlockGetter getter, BlockPos pos, Direction direction)
	   {
	      return state.getValue(POWERED) ? 15 : 0;
	   }
		
		@Override
	   public boolean isSignalSource(BlockState state)
	   {
	      return true;
	   }

	   private void updateNeighbours(BlockState state, Level level, BlockPos pos)
	   {
	      level.updateNeighborsAt(pos, this);
	   }

}
