package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargatePart;

public class ClassicStargateRingBlock extends AbstractStargateRingBlock
{
	public ClassicStargateRingBlock(Properties properties)
	{
		super(properties, 8.0D, 0.0D);
	}

	public Stargate.Type getStargateType()
	{
		return Stargate.Type.CLASSIC;
	}

    @Override
	public Item asItem()
	{
		return BlockInit.CLASSIC_STARGATE.get().asItem();
	}
	
	@Override
	public InteractionResult use(BlockState state, Level level, BlockPos pos, Player player, InteractionHand hand, BlockHitResult result)
	{
		StargatePart part = state.getValue(PART);
		BlockPos baseBlockPos = part.getBaseBlockPos(pos, state.getValue(FACING), state.getValue(ORIENTATION));
		
		if(level.getBlockState(baseBlockPos).getBlock() instanceof ClassicStargateBlock baseBlock)
			return baseBlock.upgradeStargate(level, baseBlockPos, player, hand) ? InteractionResult.SUCCESS : InteractionResult.FAIL;
		
		return InteractionResult.FAIL;
	}
}
