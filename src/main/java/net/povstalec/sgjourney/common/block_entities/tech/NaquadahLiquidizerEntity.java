package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.neoforged.neoforge.fluids.FluidStack;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.recipe.LiquidizingRecipe;

import java.util.HashMap;
import java.util.Map;

public class NaquadahLiquidizerEntity extends AbstractNaquadahLiquidizerEntity<LiquidizingRecipe.NaquadahLiquidizer>
{
	public static final Map<Fluid, Boolean> VALID_FLUIDS_CACHE = new HashMap<>(); // Caching fluids the tank can hold
	
	public NaquadahLiquidizerEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.NAQUADAH_LIQUIDIZER.get(), pos, state);
	}
	
	@Override
	public boolean isDesiredInputFluid(FluidStack fluidStack)
	{
		return VALID_FLUIDS_CACHE.computeIfAbsent(fluidStack.getFluid(), fluid -> getAvailableRecipes()
				.map(recipe -> (LiquidizingRecipe.NaquadahLiquidizer) recipe)
				.anyMatch(recipe -> recipe.getInputFluid().getFluid().equals(fluidStack.getFluid())));
	}
	
	@Override
	protected RecipeType<LiquidizingRecipe.NaquadahLiquidizer> getRecipeType()
	{
		return LiquidizingRecipe.NaquadahLiquidizer.TYPE;
	}
	
	//============================================================================================
	//*******************************************Fluids*******************************************
	//============================================================================================
	
	@Override
	public int inputFluidTankCapacity()
	{
		return CommonTechConfig.naquadah_liquidizer_fluid_input_capacity.get();
	}
	
	@Override
	public int maxFluidReceive()
	{
		return CommonTechConfig.naquadah_liquidizer_max_fluid_receive.get();
	}
	
	@Override
	public int outputFluidTankCapacity()
	{
		return CommonTechConfig.naquadah_liquidizer_fluid_output_capacity.get();
	}
	
	@Override
	public int maxFluidExtract()
	{
		return CommonTechConfig.naquadah_liquidizer_max_fluid_extract.get();
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	protected long getCapacity()
	{
		return CommonTechConfig.naquadah_liquidizer_energy_capacity.get();
	}
	
	@Override
	protected long getMaxReceive()
	{
		return CommonTechConfig.naquadah_liquidizer_max_energy_receive.get();
	}
	
	@Override
	protected long getMaxExtract()
	{
		return 0;
	}
	
	@Override
	public long energyPerProgressTick()
	{
		return CommonTechConfig.naquadah_liquidizer_energy_per_tick.get();
	}
}
