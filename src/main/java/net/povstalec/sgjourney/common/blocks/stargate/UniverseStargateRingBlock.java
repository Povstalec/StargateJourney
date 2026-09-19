package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.blocks.SpecialEngravableBlock;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class UniverseStargateRingBlock extends RotatingStargateRingBlock implements SpecialEngravableBlock
{
	public UniverseStargateRingBlock(Properties properties)
	{
		super(properties, 7.0D, 1.0D);
	}
	
	@Override
	public @NotNull Item asItem()
	{
		return BlockInit.UNIVERSE_STARGATE.get().asItem();
	}
	
	@Override
	public InteractionResult onGraverUsed(Level level, BlockPos pos, @Nullable Player player, InteractionHand hand, ItemStack graverStack)
	{
		if(!level.isClientSide())
		{
			BlockPos baseBlockPos = getBaseBlockPos(pos, level.getBlockState(pos));
			BlockState baseBlockState = level.getBlockState(baseBlockPos);
			if(baseBlockState.getBlock() instanceof UniverseStargateBlock baseBlock)
				baseBlock.openStargateGravingMenu(level, baseBlockPos, baseBlockState, player);
		}
		
		return InteractionResult.PASS;
	}
	
	@Override
	public @Nullable ResourceKey<PointOfOrigin> getPointOfOrigin(Level level, BlockPos pos, BlockState state)
	{
		AbstractStargateEntity<?> stargate = getStargate(level, pos, state);
		
		if(stargate != null)
			return stargate.symbolInfo().pointOfOrigin();
		
		return null;
	}
	
	@Override
	public @Nullable ResourceKey<Symbols> getSymbols(Level level, BlockPos pos, BlockState state)
	{
		AbstractStargateEntity<?> stargate = getStargate(level, pos, state);
		
		if(stargate != null)
			return stargate.symbolInfo().symbols();
		
		return null;
	}
}
