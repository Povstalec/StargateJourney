package net.povstalec.sgjourney.common.blocks.stargate;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.UniverseStargateEntity;
import net.povstalec.sgjourney.common.blocks.SpecialSymbolBlock;
import net.povstalec.sgjourney.common.blocks.stargate.shielding.AbstractShieldingBlock;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

public class UniverseStargateBlock extends RotatingStargateBaseBlock implements SpecialSymbolBlock
{
	public UniverseStargateBlock(Properties properties)
	{
		super(properties, 7.0D, 1.0D);
	}
	
	@Nullable
	@Override
	public BlockEntity newBlockEntity(BlockPos pos, BlockState state) 
	{
		return new UniverseStargateEntity(pos, state);
	}

	@Override
	public AbstractStargateRingBlock getRing()
	{
		return BlockInit.UNIVERSE_RING.get();
	}
	
	@Override
	public AbstractShieldingBlock getIris()
	{
		return BlockInit.UNIVERSE_SHIELDING.get();
	}

	@Override
	public BlockState ringState()
	{
		return getRing().defaultBlockState();
	}
	
	@Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state, BlockEntityType<T> type)
	{
		return createTickerHelper(type, BlockEntityInit.UNIVERSE_STARGATE.get(), UniverseStargateEntity::tick);
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
