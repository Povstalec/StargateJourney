package net.povstalec.sgjourney.common.block_entities.tech;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.povstalec.sgjourney.common.block_entities.ProtectedBlockEntity;
import net.povstalec.sgjourney.common.config.CommonPermissionConfig;
import org.jetbrains.annotations.NotNull;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.Containers;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.energy.IEnergyStorage;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.povstalec.sgjourney.common.block_entities.EnergyBlockEntity;
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.capabilities.ZeroPointEnergy;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;

public class ZPMHubEntity extends EnergyBlockEntity implements ProtectedBlockEntity
{
	private static final long maxTransfer = CommonZPMConfig.zpm_hub_max_transfer.get();
	private static final long maxEnergyDisplayed = CommonZPMConfig.zpm_energy_per_level_of_entropy.get();
	
	private final ItemStackHandler itemHandler = createHandler();
	private final LazyOptional<IItemHandler> lazyItemHandler = LazyOptional.of(() -> itemHandler);
	private LazyOptional<IEnergyStorage> lazyEnergyHandler = LazyOptional.empty();
	
	protected boolean isProtected = false;
	
	public ZPMHubEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.ZPM_HUB.get(), pos, state);
	}
	
	@Override
	public void invalidateCaps()
	{
		super.invalidateCaps();
		lazyEnergyHandler.invalidate();
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
					switch(slot)
					{
					case 0:
						return stack.getItem() == ItemInit.ZPM.get();
					default: 
						return false;
					}
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
					{
						return stack;
					}
					
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
	public boolean isCorrectEnergySide(Direction side)
	{
		return side == Direction.UP;
	}
	
	protected boolean receivesEnergy()
	{
		return false;
	}
	
	@Override
	public long capacity()
	{
		return maxEnergyDisplayed;
	}

	@Override
	public long maxReceive()
	{
		return 0;
	}

	@Override
	public long maxExtract()
	{
		return maxTransfer;
	}
	
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
							long simulatedOutputAmount = zpmEnergy.extractLongEnergy(this.maxExtract(), true);
							long simulatedReceiveAmount = sgjourneyEnergy.receiveZeroPointEnergy(simulatedOutputAmount, true);
							zpmEnergy.extractLongEnergy(simulatedReceiveAmount, false);
							sgjourneyEnergy.receiveZeroPointEnergy(simulatedReceiveAmount, false);
						}
						else if(CommonZPMConfig.other_mods_use_zero_point_energy.get())
						{
							int simulatedOutputAmount = zpmEnergy.extractEnergy(SGJourneyEnergy.regularEnergy(this.maxExtract()), true);
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
	
	//============================================================================================
	//******************************************Ticking*******************************************
	//============================================================================================
	
	public static void tick(Level level, BlockPos pos, BlockState state, ZPMHubEntity hub)
	{
		if(level.isClientSide())
			return;
		
		hub.outputEnergy(Direction.DOWN);

		//ItemStack stack = hub.itemHandler.getStackInSlot(0);
		//long energy = ZeroPointModule.isNearingEntropy(stack) ? ZeroPointModule.getEnergyInLevel(stack) : ZeroPointModule.getMaxEnergy();
		//hub.setEnergy(energy);
		
		//PacketHandlerInit.INSTANCE.send(PacketDistributor.TRACKING_CHUNK.with(() -> level.getChunkAt(hub.worldPosition)), new ClientboundNaquadahGeneratorUpdatePacket(hub.worldPosition, hub.getReactionProgress(), hub.getEnergy()));
	}
}
