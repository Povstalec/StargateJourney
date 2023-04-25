package net.povstalec.sgjourney.common.menu;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.common.block_entities.BasicInterfaceEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public class BasicInterfaceMenu extends AbstractContainerMenu
{
    private final BasicInterfaceEntity blockEntity;
    private final Level level;
    
    public BasicInterfaceMenu(int containerId, Inventory inv, FriendlyByteBuf extraData) {
        this(containerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public BasicInterfaceMenu(int containerId, Inventory inv, BlockEntity entity) {
        super(MenuInit.BASIC_INTERFACE.get(), containerId);
        blockEntity = ((BasicInterfaceEntity) entity);
        this.level = inv.player.level;
    }
    
    public long getEnergy()
    {
    	return this.blockEntity.getEnergyStored();
    }
    
    public long getMaxEnergy()
    {
    	return this.blockEntity.getCapacity();
    }
	
	@Override
	public boolean stillValid(Player player) {
		return stillValid(ContainerLevelAccess.create(level, blockEntity.getBlockPos()),
				player, BlockInit.BASIC_INTERFACE.get());
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index)
	{
		return null;
	}
	
}
