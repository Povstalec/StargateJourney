package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;
import net.povstalec.sgjourney.common.block_entities.transporter.RingPanelEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.packets.ServerboundRingPanelUpdatePacket;

public class RingPanelMenu extends InventoryMenu
{
    private final RingPanelEntity blockEntity;
    private final Level level;
    
    public RingPanelMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData)
    {
        this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public RingPanelMenu(int containerId, Inventory inventory, BlockEntity entity)
    {
        super(MenuInit.RING_PANEL.get(), containerId);
        checkContainerSize(inventory, 6);
        blockEntity = ((RingPanelEntity) entity);
        this.level = inventory.player.level();
        
        addPlayerInventory(inventory, 8, 140);
        addPlayerHotbar(inventory, 8, 198);
        
        this.blockEntity.getCrystalItemHandler().ifPresent(handler ->
        {
            this.addSlot(new SlotItemHandler(handler, 0, 5, 36));
            this.addSlot(new SlotItemHandler(handler, 1, 23, 36));
            this.addSlot(new SlotItemHandler(handler, 2, 5, 54));
            this.addSlot(new SlotItemHandler(handler, 3, 23, 54));
            this.addSlot(new SlotItemHandler(handler, 4, 5, 72));
            this.addSlot(new SlotItemHandler(handler, 5, 23, 72));
        });
		
		this.blockEntity.getEnergyItemHandler().ifPresent(handler ->
		{
			this.addSlot(new SlotItemHandler(handler, 0, 137, 36));
		});
    }
	
	public long getEnergy()
	{
		return this.blockEntity.getEnergyStored();
	}
	
	public long getMaxEnergy()
	{
		return this.blockEntity.getEnergyCapacity();
	}
	
	public RingPanelEntity.Button getButtonAt(int index)
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
	
    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
                player, BlockInit.GOAULD_RING_PANEL.get());
    }
	
	@Override
	protected int blockEntitySlotCount()
	{
		return 6;
	}
}
