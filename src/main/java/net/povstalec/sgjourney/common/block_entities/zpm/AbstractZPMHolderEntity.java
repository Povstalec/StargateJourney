package net.povstalec.sgjourney.common.block_entities.zpm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.init.ItemInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractZPMHolderEntity extends BlockEntity implements ProtectedBlockEntity
{
	protected final ItemStackHandler itemHandler = createHandler();
	private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);
	
	protected boolean isProtected = false;
	
	public AbstractZPMHolderEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public void invalidateCaps()
	{
		super.invalidateCaps();
		lazyItemHandler.invalidate();
	}
	
	@Override
	public void load(CompoundTag tag)
	{
		super.load(tag);
		itemHandler.deserializeNBT(tag.getCompound("Inventory"));
		
		if(tag.contains(PROTECTED, CompoundTag.TAG_BYTE))
			isProtected = tag.getBoolean(PROTECTED);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag)
	{
		super.saveAdditional(tag);
		tag.put("Inventory", itemHandler.serializeNBT());
		
		if(isProtected)
			tag.putBoolean(PROTECTED, true);
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag()
	{
		return this.saveWithoutMetadata();
	}
	
	public void updateClient()
	{
		if(level != null && !level.isClientSide())
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
	}
	
	public LazyOptional<IItemHandler> getItemHandler()
	{
		return lazyItemHandler.cast();
	}
	
	public ItemStack getHeldItemStack()
	{
		return getItemHandler().map(itemHandler -> itemHandler.getStackInSlot(0)).orElse(ItemStack.EMPTY);
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	@Override
	public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side)
	{
		if(capability == ForgeCapabilities.ITEM_HANDLER && (!isProtected() || CommonPermissionConfig.protected_inventory_access.get()))
			return lazyItemHandler.cast();
		
		return super.getCapability(capability, side);
	}
	
	//============================================================================================
	//******************************************Storage*******************************************
	//============================================================================================
	
	public void onSlotContentsChanged(int slot)
	{
		setChanged();
	}
	
	private ItemStackHandler createHandler()
	{
		return new ItemStackHandler(1)
			{
				@Override
				protected void onContentsChanged(int slot)
				{
					onSlotContentsChanged(slot);
				}
				
				@Override
				public boolean isItemValid(int slot, @Nonnull ItemStack stack)
				{
					return stack.isEmpty() || stack.is(ItemInit.ZPM.get());
				}
				
				// Limits the number of items per slot
				public int getSlotLimit(int slot)
				{
					return 1;
				}
				
				@Nonnull
				@Override
				public ItemStack insertItem(int slot, @Nonnull ItemStack stack, boolean simulate)
				{
					if(!isItemValid(slot, stack))
						return stack;
					
					return super.insertItem(slot, stack, simulate);
					
				}
			};
	}
	
	public void drops()
	{
		SimpleContainer inventory = new SimpleContainer(itemHandler.getSlots());
		for (int i = 0; i < itemHandler.getSlots(); i++)
		{
			inventory.setItem(i, itemHandler.getStackInSlot(i));
		}
		
		Containers.dropContents(this.level, this.worldPosition, inventory);
	}
	
	@Override
	public void setProtected(boolean isProtected)
	{
		this.isProtected = isProtected;
	}
	
	@Override
	public boolean isProtected()
	{
		return isProtected;
	}
}
