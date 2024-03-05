package net.povstalec.sgjourney.client.screens;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.client.widgets.DHDBigButton;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;

public class MilkyWayDHDScreen extends AbstractDHDScreen
{
	public MilkyWayDHDScreen(AbstractDHDMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title);
	}
	
	@Override
	public void init()
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		super.init();
		this.addRenderableWidget(new DHDBigButton.MilkyWay(x + 69, y + 69, menu, (n) -> {menu.engageChevron(0); this.onClose();}));
	}
	
}
