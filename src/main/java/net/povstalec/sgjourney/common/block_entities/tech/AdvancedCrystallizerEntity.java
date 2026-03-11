package net.povstalec.sgjourney.common.block_entities.tech;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.recipe.CrystallizingRecipe;

public class AdvancedCrystallizerEntity extends AbstractCrystallizerEntity<CrystallizingRecipe.AdvancedCrystallizer>
{
	public static final long ENERGY_CAPACITY = 1000000; // TODO Make this configurable
	public static final long MAX_ENERGY_RECEIVE = 100000; // TODO Make this configurable
	public static final long CRYSTALLIZATION_ENERGY_PER_TICK = 1000; // TODO Make this configurable
	
	public AdvancedCrystallizerEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.ADVANCED_CRYSTALLIZER.get(), pos, state);
	}

	@Override
	public Fluid getDesiredFluid()
	{
		return FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get();
	}
	
	@Override
	protected RecipeType<CrystallizingRecipe.AdvancedCrystallizer> getRecipeType()
	{
		return CrystallizingRecipe.AdvancedCrystallizer.TYPE;
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
		return CRYSTALLIZATION_ENERGY_PER_TICK;
	}
}
