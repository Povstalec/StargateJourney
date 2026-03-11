package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.init.ItemInit;

public class HeavyNaquadahLiquidizerEntity extends AbstractNaquadahLiquidizerEntity
{
	public static final long ENERGY_CAPACITY = 1000000; // TODO Make this configurable
	public static final long MAX_ENERGY_RECEIVE = 100000; // TODO Make this configurable
	public static final long LIQUIDIZATION_ENERGY_PER_TICK = 1000; // TODO Make this configurable
	
	public HeavyNaquadahLiquidizerEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.HEAVY_NAQUADAH_LIQUIDIZER.get(), pos, state);
	}
	
	@Override
	public Fluid getDesiredFluid1()
	{
		return FluidInit.LIQUID_NAQUADAH_SOURCE.get();
	}

	@Override
	public Fluid getDesiredFluid2()
	{
		return FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get();
	}

	@Override
	protected boolean hasIngredients()
	{
		return itemInputHandler.getStackInSlot(0).is(ItemInit.PURE_NAQUADAH.get());
	}
	
	@Override
	protected int producedAmount()
	{
		return 200;
	}

	@Override
	protected void makeLiquidNaquadah()
	{
		useUpItems(1);
		
		this.fluidTank2.fill(new FluidStack(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get(), producedAmount()), IFluidHandler.FluidAction.EXECUTE);
		
		this.progress = 0;
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
	public long liquidizationEnergyPerTick()
	{
		return LIQUIDIZATION_ENERGY_PER_TICK;
	}
}
