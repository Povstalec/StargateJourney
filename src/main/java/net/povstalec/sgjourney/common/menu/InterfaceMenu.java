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
import net.povstalec.sgjourney.common.block_entities.tech.AbstractInterfaceEntity;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.MenuInit;

public class InterfaceMenu extends AbstractContainerMenu
{
    private final AbstractInterfaceEntity interfaceEntity;
    private final Level level;
    
    public InterfaceMenu(int containerId, Inventory inv, FriendlyByteBuf extraData)
    {
        this(containerId, inv, inv.player.level.getBlockEntity(extraData.readBlockPos()));
    }

    public InterfaceMenu(int containerId, Inventory inv, BlockEntity entity)
    {
        super(MenuInit.INTERFACE.get(), containerId);
        interfaceEntity = ((AbstractInterfaceEntity) entity);
        this.level = inv.player.level;
    }
    
    public long getEnergy()
    {
    	return this.interfaceEntity.getEnergyStored();
    }
    
    public long getMaxEnergy()
    {
    	return this.interfaceEntity.getEnergyCapacity();
    }
	
	@Override
	public boolean stillValid(Player player)
	{
		BlockPos pos = interfaceEntity.getBlockPos();
		return stillValid(ContainerLevelAccess.create(level, pos), player, BlockInit.BASIC_INTERFACE.get())
				|| stillValid(ContainerLevelAccess.create(level, pos), player, BlockInit.CRYSTAL_INTERFACE.get())
				|| stillValid(ContainerLevelAccess.create(level, pos), player, BlockInit.ADVANCED_CRYSTAL_INTERFACE.get());
	}

	@Override
	public ItemStack quickMoveStack(Player player, int index)
	{
		return ItemStack.EMPTY;
	}
	
}
