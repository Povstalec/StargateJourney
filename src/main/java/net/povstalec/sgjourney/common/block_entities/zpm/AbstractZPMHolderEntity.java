package net.povstalec.sgjourney.common.block_entities.zpm;

import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.block_entities.tech.EnergyBlockEntity;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.capabilities.ZeroPointEnergy;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.init.ItemInit;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class AbstractZPMHolderEntity extends EnergyBlockEntity implements ProtectedBlockEntity
{
	private final ItemStackHandler itemHandler = createHandler();
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
	
	//============================================================================================
	//*******************************************Energy*******************************************
	//============================================================================================
	
	@Override
	public void outputEnergy(Direction outputDirection)
	{
		ItemStack stack = itemHandler.getStackInSlot(0);
		
		if(stack.is(ItemInit.ZPM.get()))
		{
			stack.getCapability(ForgeCapabilities.ENERGY).ifPresent(itemEnergy ->
			{
				if(itemEnergy instanceof ZeroPointEnergy zpmEnergy)
				{
					BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(outputDirection));
					
					if(blockEntity == null)
						return;
					
					blockEntity.getCapability(ForgeCapabilities.ENERGY, outputDirection.getOpposite()).ifPresent(otherEnergy ->
					{
						if(otherEnergy instanceof SGJourneyEnergy sgjourneyEnergy)
						{
							long simulatedOutputAmount = zpmEnergy.extractLongEnergy(this.getMaxEnergyDeplete(), true);
							long simulatedReceiveAmount = sgjourneyEnergy.receiveZeroPointEnergy(simulatedOutputAmount, true);
							zpmEnergy.extractLongEnergy(simulatedReceiveAmount, false);
							sgjourneyEnergy.receiveZeroPointEnergy(simulatedReceiveAmount, false);
						}
						else if(CommonZPMConfig.other_mods_use_zero_point_energy.get())
						{
							int simulatedOutputAmount = zpmEnergy.extractEnergy(SGJourneyEnergy.regularEnergy(this.getMaxEnergyDeplete()), true);
							int simulatedReceiveAmount = otherEnergy.receiveEnergy(simulatedOutputAmount, true);
							
							zpmEnergy.extractLongEnergy(simulatedReceiveAmount, false);
							otherEnergy.receiveEnergy(simulatedReceiveAmount, false);
						}
					});
				}
			});
		}
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
	
	@Override
	public boolean hasPermissions(Player player, boolean sendMessage)
	{
		if(isProtected() && !player.hasPermissions(CommonPermissionConfig.protected_zpm_hub_permissions.get()))
		{
			if(sendMessage)
				player.displayClientMessage(Component.translatable("block.sgjourney.protected_permissions").withStyle(ChatFormatting.DARK_RED), true);
			
			return false;
		}
		
		return true;
	}
}
