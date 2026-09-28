package net.povstalec.sgjourney.common.menu.dhd;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.menu.SGJourneyMenu;
import org.jetbrains.annotations.NotNull;

public abstract class AbstractDHDMenu<T extends AbstractDHDEntity> extends SGJourneyMenu<T> implements IDHDMenu
{
    public AbstractDHDMenu(MenuType<?> menu, int containerId, Inventory inventory, T blockEntity)
    {
        super(menu, containerId, inventory, blockEntity);
        checkContainerSize(inventory, 9);
    }
	
	@Override
	public AbstractDHDEntity getDHD()
	{
		return blockEntity;
	}
    
    @Override
    public @NotNull ItemStack quickMoveStack(@NotNull Player player, int index)
    {
    	return ItemStack.EMPTY;
    }
}
