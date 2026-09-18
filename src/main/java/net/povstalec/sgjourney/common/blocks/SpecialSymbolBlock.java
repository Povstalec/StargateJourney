package net.povstalec.sgjourney.common.blocks;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.PointOfOrigin;
import net.povstalec.sgjourney.common.sgjourney.Symbols;

import javax.annotation.Nullable;

public interface SpecialSymbolBlock
{
	@Nullable
	default ResourceKey<PointOfOrigin> getPointOfOrigin(Level level, BlockPos pos, BlockState state)
	{
		return null;
	}
	
	@Nullable
	default ResourceKey<Symbols> getSymbols(Level level, BlockPos pos, BlockState state)
	{
		return null;
	}
	
	@Nullable
	default Address getAddress(Level level, BlockPos pos, BlockState state)
	{
		return null;
	}
}
