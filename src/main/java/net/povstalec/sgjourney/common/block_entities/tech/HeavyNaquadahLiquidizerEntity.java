package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.recipe.LiquidizingRecipe;

import java.util.Optional;

public class HeavyNaquadahLiquidizerEntity extends AbstractNaquadahLiquidizerEntity<LiquidizingRecipe.HeavyNaquadahLiquidizer>
{
	public static final long ENERGY_CAPACITY = 1000000; // TODO Make this configurable
	public static final long MAX_ENERGY_RECEIVE = 100000; // TODO Make this configurable
	public static final long LIQUIDIZATION_ENERGY_PER_TICK = 1000; // TODO Make this configurable
	
	public HeavyNaquadahLiquidizerEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.HEAVY_NAQUADAH_LIQUIDIZER.get(), pos, state);
	}
	
	@Override
	public Fluid getInputFluid()
	{
		return FluidInit.LIQUID_NAQUADAH_SOURCE.get();
	}

	@Override
	public Fluid getOutputFluid()
	{
		return FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get();
	}
	
	@Override
	protected RecipeType<LiquidizingRecipe.HeavyNaquadahLiquidizer> getRecipeType()
	{
		return LiquidizingRecipe.HeavyNaquadahLiquidizer.TYPE;
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
