package net.povstalec.sgjourney.common.block_entities.tech;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.fluids.capability.IFluidHandler;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.init.ItemInit;

public class NaquadahLiquidizerEntity extends AbstractNaquadahLiquidizerEntity
{
	public NaquadahLiquidizerEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.NAQUADAH_LIQUIDIZER.get(), pos, state);
	}
	
	@Override
	public Fluid getDesiredFluid1()
	{
		return Fluids.LAVA;
	}

	@Override
	public Fluid getDesiredFluid2()
	{
		return FluidInit.LIQUID_NAQUADAH_SOURCE.get();
	}

	@Override
	protected boolean hasMaterial()
	{
		return itemStackHandler.getStackInSlot(0).is(ItemInit.RAW_NAQUADAH.get());
	}

	@Override
	protected void makeLiquidNaquadah()
	{
		useUpItems(1);
		
		this.fluidTank2.fill(new FluidStack(FluidInit.LIQUID_NAQUADAH_SOURCE.get(), 100), IFluidHandler.FluidAction.EXECUTE);
		
		this.progress = 0;
	}
}
