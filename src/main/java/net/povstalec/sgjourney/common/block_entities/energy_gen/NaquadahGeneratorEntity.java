package net.povstalec.sgjourney.common.block_entities.energy_gen;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ClientboundNaquadahGeneratorUpdatePacket;

public abstract class NaquadahGeneratorEntity extends EnergyBlockEntity
{
	private int reactionProgress = 0;
	
	private final ItemStackHandler itemHandler = createHandler();
	private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);
	
	public NaquadahGeneratorEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state, true);
	}
	
	@Override
	public void invalidateCaps()
	{
		super.invalidateCaps();
		lazyItemHandler.invalidate();
	}
	
	@Override
	public void load(CompoundTag nbt)
	{
		super.load(nbt);
		itemHandler.deserializeNBT(nbt.getCompound("Inventory"));
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt)
	{
		super.saveAdditional(nbt);
		nbt.put("Inventory", itemHandler.serializeNBT());
	}
	
	//============================================================================================
	//************************************Getters and setters*************************************
	//============================================================================================
	
	public boolean hasNaquadah()
	{
		return this.itemHandler.getStackInSlot(0).getItem() == ItemInit.NAQUADAH.get();
	}
	
	public void setReactionProgress(int reactionProgress)
	{
		this.reactionProgress = reactionProgress;
	}
	
	public int getReactionProgress()
	{
		return this.reactionProgress;
	}
	
	public abstract int getReactionTime();

	public abstract int getEnergyPerTick();
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side)
	{
		if(capability == ForgeCapabilities.ITEM_HANDLER)
			return lazyItemHandler.cast();
		
		return super.getCapability(capability, side);
	}
	
	//============================================================================================
	//******************************************Storage*******************************************
	//============================================================================================
	
	private ItemStackHandler createHandler()
	{
		return new ItemStackHandler(1)
			{
				@Override
				protected void onContentsChanged(int slot)
				{
					setChanged();
				}
				
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack)
				{
					switch(slot)
					{
					case 0:
						return stack.getItem() == ItemInit.NAQUADAH.get();
					default: 
						return false;
					}
				}
				
				// Limits the number of items per slot
				public int getSlotLimit(int slot)
				{
					return 16;
				}
				
				@Nonnull
				@Override
				public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
				{
					if(!isItemValid(slot, stack))
					{
						return stack;
					}
					
					return super.insertItem(slot, stack, simulate);
					
				}
			};
	}
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	protected boolean receivesEnergy()
	{
		return false;
	}
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	private void doReaction()
	{
		if(this.hasNaquadah() && this.reactionProgress == 0)
		{
			this.itemHandler.extractItem(0, 1, false);
			this.progressReaction();
		}
		else if(this.reactionProgress > 0 && this.reactionProgress < this.getReactionTime() && this.getEnergyStored() < this.capacity())
			this.progressReaction();
		else if(this.reactionProgress >= this.getReactionTime())
			this.reactionProgress = 0;
	}
	
	private void progressReaction()
	{
		this.generateEnergy(this.getEnergyPerTick());
		this.reactionProgress++;
	}
	
	public static void tick(Level level, BlockPos pos, BlockState state, NaquadahGeneratorEntity generator)
	{
		if(level.isClientSide)
			return;
		
		generator.doReaction();
		generator.outputEnergy(Direction.DOWN);
		
		PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(generator.worldPosition)), new ClientboundNaquadahGeneratorUpdatePacket(generator.worldPosition, generator.getReactionProgress(), generator.getEnergyStored()));
	}
}
