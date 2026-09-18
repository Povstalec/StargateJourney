package net.povstalec.sgjourney.common.blocks.stargate;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.blocks.SpecialSymbolBlock;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;

public class PegasusStargateRingBlock extends AbstractStargateRingBlock implements SpecialSymbolBlock
{
	public PegasusStargateRingBlock(Properties properties)
	{
		super(properties, 7.0D, 1.0D);
	}

	@Override
	public @NotNull Item asItem()
	{
		return BlockInit.PEGASUS_STARGATE.get().asItem();
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
	
	@Override
	public @Nullable Address getAddress(Level level, BlockPos pos, BlockState state)
	{
		AbstractStargateEntity<?> stargate = getStargate(level, pos, state);
		
		if(stargate != null)
			return stargate.getEncodedSymbols();
		
		return null;
	}
}
