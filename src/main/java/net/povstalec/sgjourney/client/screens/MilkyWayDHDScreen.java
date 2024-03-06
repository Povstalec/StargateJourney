package net.povstalec.sgjourney.client.screens;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.DHDBigButton;
import net.povstalec.sgjourney.client.widgets.MilkyWayDHDSymbolButton;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;

public class MilkyWayDHDScreen extends AbstractDHDScreen
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd/milky_way/milky_way_dhd_background.png");
	
	public MilkyWayDHDScreen(AbstractDHDMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title, TEXTURE);
	}
	
	@Override
	public void init()
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		super.init();
		this.addRenderableWidget(new DHDBigButton.MilkyWay(x + 69, y + 69, menu, (n) -> {menu.engageChevron(0); this.onClose();}));
		
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 8, y + 68, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_1));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 14, y + 43, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_2));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 27, y + 23, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_3));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 50, y + 11, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_4));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 75, y + 8, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_5));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 103, y + 9, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_6));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 122, y + 16, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_7));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 140, y + 32, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_8));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 152, y + 56, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_9));
		
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 158, y + 82, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_10));
		
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 152, y + 108, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_11));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 140, y + 127, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_12));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 122, y + 143, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_13));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 103, y + 154, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_14));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 75, y + 158, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_15));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 50, y + 150, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_16));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 27, y + 135, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_17));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 14, y + 118, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_18));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 8, y + 97, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_19));
		
		
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 35, y + 77, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_20));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 39, y + 59, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_21));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 49, y + 46, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_22));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 64, y + 38, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_23));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 82, y + 35, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_24));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 99, y + 35, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_25));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 110, y + 41, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_26));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 119, y + 52, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_27));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 126, y + 68, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_28));
		
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 129, y + 87, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_29));
		
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 126, y + 102, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_30));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 119, y + 113, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_31));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 110, y + 121, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_32));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 99, y + 127, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_33));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 82, y + 128, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_34));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 64, y + 124, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_35));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 49, y + 117, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_36));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 39, y + 108, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_37));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 35, y + 97, width, height, menu, MilkyWayDHDSymbolButton.MilkyWayButton.BUTTON_38));
	}
	
}
