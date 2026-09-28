package net.povstalec.sgjourney.common.block_entities.zpm;

import net.minecraft.core.BlockPos;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.jarjar.nio.util.Lazy;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.init.ItemInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;

public abstract class AbstractZPMHolderEntity extends BlockEntity implements ProtectedBlockEntity
{
	public static final String INVENTORY = "inventory";
	
	protected final ItemStackHandler itemHandler = createHandler();
	private final Lazy<IItemHandler> lazyItemHandler = Lazy.of(() -> itemHandler);
	
	protected boolean isProtected = false;
	
	public AbstractZPMHolderEntity(BlockEntityType<?> type, BlockPos pos, BlockState state)
	{
		super(type, pos, state);
	}
	
	@Override
	public void loadAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries)
	{
		super.loadAdditional(tag, registries);
		itemHandler.deserializeNBT(registries, tag.getCompound(INVENTORY));
		
		if(tag.contains(PROTECTED, CompoundTag.TAG_BYTE))
			isProtected = tag.getBoolean(PROTECTED);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag tag, @NotNull HolderLookup.Provider registries)
	{
		super.saveAdditional(tag, registries);
		tag.put(INVENTORY, itemHandler.serializeNBT(registries));
		
		if(isProtected)
			tag.putBoolean(PROTECTED, true);
	}
	
	@Override
	public ClientboundBlockEntityDataPacket getUpdatePacket()
	{
		return ClientboundBlockEntityDataPacket.create(this);
	}
	
	@Override
	public @NotNull CompoundTag getUpdateTag(@NotNull HolderLookup.Provider registries)
	{
		return this.saveWithoutMetadata(registries);
	}
	
	public void updateClient()
	{
		if(level != null && !level.isClientSide())
			level.sendBlockUpdated(getBlockPos(), getBlockState(), getBlockState(), Block.UPDATE_IMMEDIATE);
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	public IItemHandler getItemHandler()
	{
		return itemHandler;
	}
	
	public ItemStack getHeldItemStack()
	{
		return itemHandler.getStackInSlot(0);
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
