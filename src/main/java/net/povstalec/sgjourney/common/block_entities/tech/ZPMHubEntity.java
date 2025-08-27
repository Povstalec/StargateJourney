package net.povstalec.sgjourney.common.block_entities.tech;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import net.minecraft.core.HolderLookup;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.common.util.Lazy;
import net.neoforged.neoforge.energy.IEnergyStorage;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.ItemStackHandler;
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
import net.povstalec.sgjourney.common.capabilities.SGJourneyEnergy;
import net.povstalec.sgjourney.common.capabilities.ZeroPointEnergy;
import net.povstalec.sgjourney.common.config.CommonZPMConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.ItemInit;

public class ZPMHubEntity extends EnergyBlockEntity implements ProtectedBlockEntity
{
	public static final String INVENTORY = "inventory";
	
	private static final long maxTransfer = CommonZPMConfig.zpm_hub_max_transfer.get();
	private static final long maxEnergyDisplayed = CommonZPMConfig.zpm_energy_per_level_of_entropy.get();
	
	private final ItemStackHandler itemHandler = createHandler();
	private final Lazy<IItemHandler> lazyItemHandler = Lazy.of(() -> itemHandler);
	
	protected boolean isProtected = false;
	
	public ZPMHubEntity(BlockPos pos, BlockState state)
	{
		super(BlockEntityInit.ZPM_HUB.get(), pos, state);
	}
	
	@Override
	public void invalidateCapabilities()
	{
		super.invalidateCapabilities();
		lazyItemHandler.invalidate();
	}
	
	@Override
	public void loadAdditional(CompoundTag nbt, HolderLookup.Provider registries)
	{
		super.loadAdditional(nbt, registries);
		itemHandler.deserializeNBT(registries, nbt.getCompound(INVENTORY));
		
		if(nbt.contains(PROTECTED, CompoundTag.TAG_BYTE))
			isProtected = nbt.getBoolean(PROTECTED);
	}
	
	@Override
	protected void saveAdditional(@NotNull CompoundTag nbt, HolderLookup.Provider registries)
	{
		super.saveAdditional(nbt, registries);
		nbt.put(INVENTORY, itemHandler.serializeNBT(registries));
		
		if(isProtected)
			nbt.putBoolean(PROTECTED, true);
	}
	
	//============================================================================================
	//****************************************Capabilities****************************************
	//============================================================================================
	
	public IItemHandler getItemHandler()
	{
		return lazyItemHandler.get();
	}
	
	@Nullable
	public IItemHandler getItemHandler(Direction side)
	{
		if(!isProtected() || CommonPermissionConfig.protected_inventory_access.get())
			return lazyItemHandler.get();
		
		return null;
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
		return side == Direction.DOWN;
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
			IEnergyStorage itemEnergy = stack.getCapability(Capabilities.EnergyStorage.ITEM);
			if(itemEnergy != null)
			{
				if(itemEnergy instanceof ZeroPointEnergy zpmEnergy)
				{
					BlockEntity blockEntity = level.getBlockEntity(worldPosition.relative(outputDirection));
					
					if(blockEntity == null)
						return;
					
					IEnergyStorage otherEnergy = level.getCapability(Capabilities.EnergyStorage.BLOCK, getBlockPos().relative(outputDirection), outputDirection.getOpposite());
					if(otherEnergy != null)
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
					}
				}
			}
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
