package net.povstalec.sgjourney.common.block_entities;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.capability.IFluidHandler;
import net.minecraftforge.fluids.capability.templates.FluidTank;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.FluidInit;

public class LiquidizerEntity extends BlockEntity
{
	private LazyOptional<IFluidHandler> lazyFluidHandler = LazyOptional.empty();
	
	public LiquidizerEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.LIQUIDIZER.get(), pos, state);
	}
	
	@Override
	public void onLoad()
	{
		super.onLoad();
		lazyFluidHandler = LazyOptional.of(() -> fluidTank);
	}
	
	@Override
	public void invalidateCaps()
	{
		super.invalidateCaps();
		lazyFluidHandler.invalidate();
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		fluidTank.readFromNBT(nbt);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		super.saveAdditional(nbt);
		nbt = fluidTank.writeToNBT(nbt);
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, Direction side)
	{
		if(capability == ForgeCapabilities.FLUID_HANDLER)
			return lazyFluidHandler.cast();
		
		return super.getCapability(capability, side);
	}
	
	private final FluidTank fluidTank = new FluidTank(64000)
	{
		@Override
		protected void onContentsChanged()
		{
			setChanged();
	    }
		
		@Override
	    public boolean isFluidValid(int tank, @NotNull FluidStack stack)
	    {
			return stack.getFluid() == FluidInit.LIQUID_NAQUADAH_SOURCE.get();
	    }
	};
	
	public void setFluid(FluidStack fluidStack)
	{
		this.fluidTank.setFluid(fluidStack);
	}
	
	public FluidStack getFluid()
	{
		return this.fluidTank.getFluid();
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, LiquidizerEntity liquidizer)
	{
		if(level.isClientSide())
			return;
		
		//PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(generator.worldPosition)), new ClientboundNaquadahGeneratorUpdatePacket(generator.worldPosition, generator.getReactionProgress(), generator.getEnergyStored()));
	}
	
}
