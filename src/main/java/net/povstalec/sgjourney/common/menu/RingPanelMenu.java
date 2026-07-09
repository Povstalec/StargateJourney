package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.block_entities.transporter_controller.RingPanelEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.misc.TransporterControllerButton;
import net.povstalec.sgjourney.common.packets.ServerboundRingPanelUpdatePacket;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Set;

public abstract class RingPanelMenu extends InventoryMenu<RingPanelEntity>
{
	public RingPanelMenu(@Nullable MenuType<?> type, int containerId, Inventory inventory, RingPanelEntity entity)
    {
        super(type, containerId, inventory, entity);
		
		checkContainerSize(inventory, 6);
		addPlayerInventory(inventory, 8, 140);
		addPlayerHotbar(inventory, 8, 198);
    }
	
	public long getTransporterEnergy()
	{
		return this.blockEntity.getTransporterEnergy();
	}
	
	public long getEnergy()
	{
		return this.blockEntity.energyStorage.getTrueEnergyStored();
	}
	
	public long getMaxEnergy()
	{
		return this.blockEntity.energyStorage.getTrueMaxEnergyStored();
	}
	
	public long getEnergyTarget()
	{
		return this.blockEntity.getEnergyTarget();
	}
	
	public long getTransportRange()
	{
		return Math.round(this.blockEntity.transporterCache.returnCachedOrDefault(AbstractTransporterEntity::maxTransportRange, 0D));
	}
	
	public long getEnergyReach()
	{
		return Math.round(this.blockEntity.transporterCache.returnCachedOrDefault(AbstractTransporterEntity::energyReach, 0D));
	}
	
	public boolean allowInterdimensionalTransport()
	{
		return this.blockEntity.transporterCache.returnCachedOrDefault(AbstractTransporterEntity::allowInterdimensionalTransport, false);
	}
	
	public long maxEnergyDeplete()
	{
		return this.blockEntity.maxEnergyTransfer();
	}
	
	public int getMaxDistance()
	{
		return this.blockEntity.getMaxConnectionDistance();
	}
	
	public TransporterControllerButton<?> getButtonAt(int index)
	{
		return this.blockEntity.getButtonAt(index);
	}
    
    public void pressButton(int index)
    {
    	PacketHandlerInit.INSTANCE.sendToServer(new ServerboundRingPanelUpdatePacket(this.blockEntity.getBlockPos(), index));
    }
	
	public boolean hasItem(int slot)
	{
		if(slot < 0 || slot > 6)
			return false;
		
		if(slot == 6)
		{
			IItemHandler cap = this.blockEntity.getEnergyItemHandler().resolve().orElse(null);
			
			if(cap != null)
				return !cap.getStackInSlot(0).isEmpty();
		}
		else
		{
			IItemHandler cap = this.blockEntity.getCrystalItemHandler().resolve().orElse(null);
			
			if(cap != null)
				return !cap.getStackInSlot(slot).isEmpty();
			
		}
		
		return false;
	}
	
	public Set<Integer> getNetworks()
	{
		return this.blockEntity.getTransporterNetworks();
	}
	
	public boolean hasNetworkRestrictions()
	{
		return this.blockEntity.transporterCache.returnCachedOrDefault(AbstractTransporterEntity::hasNetworkRestrictions, false);
	}
	
    @Override
    public boolean stillValid(@NotNull Player player)
	{
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, BlockInit.GOAULD_RING_PANEL.get());
    }
	
	public static class Protected extends RingPanelMenu
	{
		public Protected(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (RingPanelEntity) inventory.player.level.getBlockEntity(extraData.readBlockPos()));
		}
		
		public Protected(int containerId, Inventory inventory, RingPanelEntity entity)
		{
			super(MenuInit.RING_PANEL_PROTECTED.get(), containerId, inventory, entity);
		}
	}
	
	public static class Unprotected extends RingPanelMenu
	{
		public Unprotected(int containerId, Inventory inventory, FriendlyByteBuf extraData)
		{
			this(containerId, inventory, (RingPanelEntity) inventory.player.level.getBlockEntity(extraData.readBlockPos()));
		}
		
		public Unprotected(int containerId, Inventory inventory, RingPanelEntity entity)
		{
			super(MenuInit.RING_PANEL_UNPROTECTED.get(), containerId, inventory, entity);
			
			this.blockEntity.getCrystalItemHandler().ifPresent(handler ->
			{
				this.addBlockEntitySlot(new SlotItemHandler(handler, 0, 5, 36));
				this.addBlockEntitySlot(new SlotItemHandler(handler, 1, 23, 36));
				this.addBlockEntitySlot(new SlotItemHandler(handler, 2, 5, 54));
				this.addBlockEntitySlot(new SlotItemHandler(handler, 3, 23, 54));
				this.addBlockEntitySlot(new SlotItemHandler(handler, 4, 5, 72));
				this.addBlockEntitySlot(new SlotItemHandler(handler, 5, 23, 72));
			});
			
			this.blockEntity.getEnergyItemHandler().ifPresent(handler ->
			{
				this.addBlockEntitySlot(new SlotItemHandler(handler, 0, 137, 30));
			});
		}
	}
}
