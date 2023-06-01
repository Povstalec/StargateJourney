package net.povstalec.sgjourney.client.screens;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.DHDBigButton;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;

public class MilkyWayDHDScreen extends AbstractDHDScreen
{
	public MilkyWayDHDScreen(AbstractDHDMenu pMenu, Inventory pPlayerInventory, Component pTitle)
	{
		super(pMenu, pPlayerInventory, pTitle);
	}
	
	@Override
	public void init()
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		super.init();
		this.addRenderableWidget(new DHDBigButton(x + 72, y + 48, (n) -> {menu.engageChevron(0); this.onClose();}, new ResourceLocation(StargateJourney.MODID, "textures/gui/milky_way_dhd_widgets.png")));
	}
	
}
