package net.povstalec.sgjourney.common.block_entities.tech;

import java.util.HashMap;
import java.util.Map;

import net.minecraft.core.BlockPos;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.povstalec.sgjourney.common.config.CommonTechConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.recipe.CrystallizingRecipe;

public class AdvancedCrystallizerEntity extends AbstractCrystallizerEntity<CrystallizingRecipe.AdvancedCrystallizer>
{
	public static final Map<Fluid, Boolean> VALID_FLUIDS_CACHE = new HashMap<>(); // Caching fluids the tank can hold
	
	public AdvancedCrystallizerEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.ADVANCED_CRYSTALLIZER.get(), pos, state);
	}

	@Override
	public boolean isDesiredInputFluid(FluidStack fluidStack)
	{
		return VALID_FLUIDS_CACHE.computeIfAbsent(fluidStack.getFluid(), fluid -> getAvailableRecipes()
				.map(recipe -> (CrystallizingRecipe.AdvancedCrystallizer) recipe)
				.anyMatch(recipe -> recipe.getInputFluid().getFluid().equals(fluidStack.getFluid())));
	}
	
	@Override
	protected RecipeType<CrystallizingRecipe.AdvancedCrystallizer> getRecipeType()
	{
		return CrystallizingRecipe.AdvancedCrystallizer.TYPE;
	}
	
	//============================================================================================
	//*******************************************Fluids*******************************************
	//============================================================================================
	
	@Override
	public int inputFluidTankCapacity()
	{
		return CommonTechConfig.advanced_crystallizer_fluid_input_capacity.get();
	}
	
	@Override
	public int maxFluidReceive()
	{
		return CommonTechConfig.advanced_crystallizer_max_fluid_receive.get();
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	protected long getCapacity()
	{
		return CommonTechConfig.advanced_crystallizer_energy_capacity.get();
	}
	
	@Override
	protected long getMaxReceive()
	{
		return CommonTechConfig.advanced_crystallizer_max_energy_receive.get();
	}
	
	@Override
	protected long getMaxExtract()
	{
		return 0;
	}
	
	@Override
	public long energyPerProgressTick()
	{
		return CommonTechConfig.advanced_crystallizer_energy_per_tick.get();
	}
}
