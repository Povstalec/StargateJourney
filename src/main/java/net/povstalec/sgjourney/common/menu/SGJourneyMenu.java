package net.povstalec.sgjourney.common.menu;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;

public abstract class SGJourneyMenu<T extends BlockEntity> extends AbstractContainerMenu
{
	public final Level level;
	public final T blockEntity;
	
	public SGJourneyMenu(MenuType<?> menu, int containerId, Inventory inventory, T blockEntity)
	{
		super(menu, containerId);
		this.blockEntity = blockEntity;
		this.level = inventory.player.level();
	}
}
