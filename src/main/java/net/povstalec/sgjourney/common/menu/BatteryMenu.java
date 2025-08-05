package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.neoforged.neoforge.items.IItemHandler;
import net.neoforged.neoforge.items.SlotItemHandler;
import net.povstalec.sgjourney.common.block_entities.tech.BatteryBlockEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public class BatteryMenu extends InventoryMenu
{
    private final BatteryBlockEntity battery;
    private final Level level;
    
    public BatteryMenu(int containerId, Inventory inventory, FriendlyByteBuf extraData)
    {
        this(containerId, inventory, inventory.player.level().getBlockEntity(extraData.readBlockPos()));
    }

    public BatteryMenu(int containerId, Inventory inventory, BlockEntity entity)
    {
        super(MenuInit.NAQUADAH_BATTERY.get(), containerId);
		battery = ((BatteryBlockEntity) entity);
        this.level = inventory.player.level();
		
		addPlayerInventory(inventory, 8, 84);
		addPlayerHotbar(inventory, 8, 142);
		
		IItemHandler itemHandler = battery.getItemHandler();
		if(itemHandler != null)
		{
			this.addSlot(new SlotItemHandler(itemHandler, 0, 8, 36));
			this.addSlot(new SlotItemHandler(itemHandler, 1, 152, 36));
		}
    }
    
    public long getEnergy()
    {
    	return battery.getEnergyStored();
    }
    
    public long getMaxEnergy()
    {
    	return battery.getEnergyCapacity();
    }
	
	public boolean hasItem(int slot)
	{
		if(slot < 0 || slot > 8)
			return false;
		
		IItemHandler cap = battery.getItemHandler();
		
		if(cap != null)
			return !cap.getStackInSlot(slot).isEmpty();
		
		return false;
	}
	
	@Override
	public boolean stillValid(Player player)
	{
		return stillValid(ContainerLevelAccess.create(level, battery.getBlockPos()), player, BlockInit.LARGE_NAQUADAH_BATTERY.get());
	}
	
	@Override
	protected int blockEntitySlotCount()
	{
		return 2;
	}
}
