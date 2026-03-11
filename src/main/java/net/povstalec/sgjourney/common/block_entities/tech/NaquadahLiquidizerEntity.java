package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.init.TagInit;
import net.povstalec.sgjourney.common.recipe.LiquidizingRecipe;

import java.util.Optional;

public class NaquadahLiquidizerEntity extends AbstractNaquadahLiquidizerEntity<LiquidizingRecipe.NaquadahLiquidizer>
{
	public static final long ENERGY_CAPACITY = 1000000; // TODO Make this configurable
	public static final long MAX_ENERGY_RECEIVE = 100000; // TODO Make this configurable
	public static final long LIQUIDIZATION_ENERGY_PER_TICK = 1000; // TODO Make this configurable
	
	public NaquadahLiquidizerEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.NAQUADAH_LIQUIDIZER.get(), pos, state);
	}
	
	@Override
	public Fluid getInputFluid()
	{
		return Fluids.LAVA;
	}

	@Override
	public Fluid getOutputFluid()
	{
		return FluidInit.LIQUID_NAQUADAH_SOURCE.get();
	}
	
	@Override
	protected RecipeType<LiquidizingRecipe.NaquadahLiquidizer> getRecipeType()
	{
		return LiquidizingRecipe.NaquadahLiquidizer.TYPE;
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	protected long getCapacity()
	{
		return ENERGY_CAPACITY;
	}
	
	@Override
	protected long getMaxReceive()
	{
		return MAX_ENERGY_RECEIVE;
	}
	
	@Override
	protected long getMaxExtract()
	{
		return 0;
	}
	
	@Override
	public long energyPerProgressTick()
	{
		return LIQUIDIZATION_ENERGY_PER_TICK;
	}
}
