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
	protected boolean hasMaterial()
	{
		return itemHandler.getStackInSlot(0).is(ItemInit.PURE_NAQUADAH.get());
	}

	@Override
	protected void makeLiquidNaquadah()
	{
		useUpItems(1);
		
		this.fluidTank2.fill(new FluidStack(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get(), 200), IFluidHandler.FluidAction.EXECUTE);
		
		this.progress = 0;
	}
}
