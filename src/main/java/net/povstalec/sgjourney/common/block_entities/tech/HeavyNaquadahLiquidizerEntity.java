package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.recipe.LiquidizingRecipe;

import java.util.HashMap;
import java.util.Map;

public class HeavyNaquadahLiquidizerEntity extends AbstractNaquadahLiquidizerEntity<LiquidizingRecipe.HeavyNaquadahLiquidizer>
{
	public static final Map<Fluid, Boolean> VALID_FLUIDS_CACHE = new HashMap<>(); // Caching fluids the tank can hold
	
	public HeavyNaquadahLiquidizerEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.HEAVY_NAQUADAH_LIQUIDIZER.get(), pos, state);
	}
	
	@Override
	public boolean isDesiredInputFluid(FluidStack fluidStack)
	{
		return VALID_FLUIDS_CACHE.computeIfAbsent(fluidStack.getFluid(), fluid -> getAvailableRecipes()
				.map(recipe -> (LiquidizingRecipe.HeavyNaquadahLiquidizer) recipe)
				.anyMatch(recipe -> recipe.getInputFluid().getFluid().equals(fluidStack.getFluid())));
	}
	
	@Override
	protected RecipeType<LiquidizingRecipe.HeavyNaquadahLiquidizer> getRecipeType()
	{
		return LiquidizingRecipe.HeavyNaquadahLiquidizer.TYPE;
	}
	
	//============================================================================================
	//*******************************************Fluids*******************************************
	//============================================================================================
	
	@Override
	public int inputFluidTankCapacity()
	{
		return CommonTechConfig.heavy_naquadah_liquidizer_fluid_input_capacity.get();
	}
	
	@Override
	public int maxFluidReceive()
	{
		return CommonTechConfig.heavy_naquadah_liquidizer_max_fluid_receive.get();
	}
	
	@Override
	public int outputFluidTankCapacity()
	{
		return CommonTechConfig.heavy_naquadah_liquidizer_fluid_output_capacity.get();
	}
	
	@Override
	public int maxFluidExtract()
	{
		return CommonTechConfig.heavy_naquadah_liquidizer_max_fluid_extract.get();
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	protected long getCapacity()
	{
		return CommonTechConfig.heavy_naquadah_liquidizer_energy_capacity.get();
	}
	
	@Override
	protected long getMaxReceive()
	{
		return CommonTechConfig.heavy_naquadah_liquidizer_max_energy_receive.get();
	}
	
	@Override
	protected long getMaxExtract()
	{
		return 0;
	}
	
	@Override
	public long energyPerProgressTick()
	{
		return CommonTechConfig.heavy_naquadah_liquidizer_energy_per_tick.get();
	}
}
