package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransportRingsEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AncientTransportRingsEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.GoauldTransportRingsEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import org.jetbrains.annotations.NotNull;

import java.util.Set;

public abstract class TransportRingsMenu<T extends AbstractTransportRingsEntity<?>> extends EnergyBlockMenu<T>
{
	protected int largeControlCrystalIndex;
	protected int[] crystalSlotIndex = new int[8];
	protected int energySlotIndex;
	
	public TransportRingsMenu(MenuType<?> type, int containerId, Inventory inventory, T blockEntity)
	{
		super(type, containerId, inventory, blockEntity);
		
		checkContainerSize(inventory, 9);
		addPlayerInventory(inventory, 8, 84);
		addPlayerHotbar(inventory, 8, 142);
		
		this.largeControlCrystalIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalItemHandler, 0, 80, 35)).index;
		
		this.crystalSlotIndex[0] = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalItemHandler, 1, 80, 17)).index;
		this.crystalSlotIndex[1] = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalItemHandler, 2, 98, 17)).index;
		this.crystalSlotIndex[2] = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalItemHandler, 3, 98, 35)).index;
		this.crystalSlotIndex[3] = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalItemHandler, 4, 98, 53)).index;
		this.crystalSlotIndex[4] = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalItemHandler, 5, 80, 53)).index;
		this.crystalSlotIndex[5] = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalItemHandler, 6, 62, 53)).index;
		this.crystalSlotIndex[6] = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalItemHandler, 7, 62, 35)).index;
		this.crystalSlotIndex[7] = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.crystalItemHandler, 8, 62, 17)).index;
		
		this.energySlotIndex = this.addBlockEntitySlot(new SlotItemHandler(this.blockEntity.energyItemHandler, 0, 142, 17)).index;
	}
	
	@Override
	protected boolean moveItemStackToBlockEntity(ItemStack sourceStack)
	{
		// Try moving energy stack to the energy slot
		if(sourceStack.getCapability(Capabilities.EnergyStorage.ITEM) != null && moveItemStackTo(sourceStack, energySlotIndex, energySlotIndex + 1, false))
			return true;
		
		return moveItemStackToBlockEntity(sourceStack, 0, blockEntityInventorySlotCount(), false);
	}
	
	public int getTransferEfficiency()
	{
		return this.blockEntity.getTransferEfficiency();
	}
	
	public long getTotalEnergyStored()
	{
		return this.blockEntity.getTotalEnergyStored();
	}
	
	public long getTotalEnergyCapacity()
	{
		return this.blockEntity.getTotalEnergyCapacity();
	}
	
	public double getTransportRange()
	{
		return Math.round(this.blockEntity.maxTransportRange());
	}
	
	public double getEnergyReach()
	{
		return Math.round(this.blockEntity.energyReach());
	}
	
	public boolean allowInterdimensionalTransport()
	{
		return this.blockEntity.allowInterdimensionalTransport();
	}
	
	public Set<Integer> getNetworks()
	{
		return this.blockEntity.getCachedNetworks();
	}
	
	public boolean hasNetworkRestrictions()
	{
		return this.blockEntity.hasNetworkRestrictions();
	}
	
	
	
	public static class Ancient extends TransportRingsMenu<AncientTransportRingsEntity>
	{
		public Ancient(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (AncientTransportRingsEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
		}
		
		public Ancient(int containerId, Inventory inventory, AncientTransportRingsEntity blockEntity)
		{
			super(MenuInit.ANCIENT_TRANSPORT_RINGS.get(), containerId, inventory, blockEntity);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.ANCIENT_TRANSPORT_RINGS.get());
		}
	}
	
	public static class Goauld extends TransportRingsMenu<GoauldTransportRingsEntity>
	{
		public Goauld(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (GoauldTransportRingsEntity) inventory.player.level().getBlockEntity(extraData.readBlockPos()));
		}
		
		public Goauld(int containerId, Inventory inventory, GoauldTransportRingsEntity blockEntity)
		{
			super(MenuInit.GOAULD_TRANSPORT_RINGS.get(), containerId, inventory, blockEntity);
		}
		
		@Override
		public boolean stillValid(@NotNull Player player)
		{
			return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()), player, BlockInit.GOAULD_TRANSPORT_RINGS.get());
		}
	}
}
