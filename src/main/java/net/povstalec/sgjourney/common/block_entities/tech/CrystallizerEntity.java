package net.povstalec.sgjourney.common.block_entities.tech;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;

import net.neoforged.neoforge.fluids.FluidStack;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.recipe.CrystallizingRecipe;

public class CrystallizerEntity extends AbstractCrystallizerEntity<CrystallizingRecipe.Crystallizer>
{
	public static final Map<Fluid, Boolean> VALID_FLUIDS_CACHE = new HashMap<>(); // Caching fluids the tank can hold
	
	public CrystallizerEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.CRYSTALLIZER.get(), pos, state);
	}

	@Override
	public boolean isDesiredInputFluid(FluidStack fluidStack)
	{
		return VALID_FLUIDS_CACHE.computeIfAbsent(fluidStack.getFluid(), fluid -> getAvailableRecipes()
				.map(recipe -> (CrystallizingRecipe.Crystallizer) recipe)
				.anyMatch(recipe -> recipe.getInputFluid().getFluid().equals(fluidStack.getFluid())));
	}
	
	@Override
	protected RecipeType<CrystallizingRecipe.Crystallizer> getRecipeType()
	{
		return CrystallizingRecipe.Crystallizer.TYPE;
	}
	
	//============================================================================================
	//*******************************************Fluids*******************************************
	//============================================================================================
	
	@Override
	public int inputFluidTankCapacity()
	{
		return CommonTechConfig.crystallizer_fluid_input_capacity.get();
	}
	
	@Override
	public int maxFluidReceive()
	{
		return CommonTechConfig.crystallizer_max_fluid_receive.get();
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	protected long getCapacity()
	{
		return CommonTechConfig.crystallizer_energy_capacity.get();
	}
	
	@Override
	protected long getMaxReceive()
	{
		return CommonTechConfig.crystallizer_max_energy_receive.get();
	}
	
	@Override
	protected long getMaxExtract()
	{
		return 0;
	}
	
	@Override
	public long energyPerProgressTick()
	{
		return CommonTechConfig.crystallizer_energy_per_tick.get();
	}
}
