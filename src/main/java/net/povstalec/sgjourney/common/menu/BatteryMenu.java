package net.povstalec.sgjourney.common.menu;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.common.block_entities.tech.BatteryBlockEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public class BatteryMenu extends AbstractContainerMenu
{
    private final BatteryBlockEntity naquadahBattery;
    private final Level level;
    
    public BatteryMenu(int containerId, Inventory playerInventory, FriendlyByteBuf extraData)
    {
        this(containerId, playerInventory, playerInventory.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public BatteryMenu(int containerId, Inventory inv, BlockEntity entity)
    {
        super(MenuInit.NAQUADAH_BATTERY.get(), containerId);
		naquadahBattery = ((BatteryBlockEntity) entity);
        this.level = inv.player.level;
    }
    
    public long getEnergy()
    {
    	return naquadahBattery.getEnergyStored();
    }
    
    public long getMaxEnergy()
    {
    	return naquadahBattery.getEnergyCapacity();
    }
	
	@Override
	public boolean stillValid(Player player)
	{
		BlockPos pos = naquadahBattery.getBlockPos();
		return stillValid(ContainerLevelAccess.create(level, pos), player, BlockInit.LARGE_NAQUADAH_BATTERY.get());
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index)
	{
		return ItemStack.EMPTY;
	}
	
}
