package net.povstalec.sgjourney.client.screens;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.DHDBigButton;
import net.povstalec.sgjourney.client.widgets.GenericDHDSymbolButton.DefaultButton;
import net.povstalec.sgjourney.client.widgets.MilkyWayDHDSymbolButton;
import net.povstalec.sgjourney.common.menu.MilkyWayDHDMenu;

public class MilkyWayDHDScreen extends AbstractDHDScreen<MilkyWayDHDMenu>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd/milky_way/milky_way_dhd_background.png");
	
	public MilkyWayDHDScreen(MilkyWayDHDMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title, TEXTURE);
	}
	
	@Override
	public void init()
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		super.init();
		this.addRenderableWidget(new DHDBigButton.MilkyWay(x + 69, y + 69, menu, (n) -> {menu.engageStargate(); this.onClose();}));
		// Outer Buttons
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 8, y + 83, menu, width, height, 0, 0, DefaultButton.BUTTON_0));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 10, y + 58, menu, width, height, 1, 12, DefaultButton.BUTTON_1));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 18, y + 35, menu, width, height, 2, 18, DefaultButton.BUTTON_2));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 34, y + 18, menu, width, height, 3, 21, DefaultButton.BUTTON_3));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 57, y + 10, menu, width, height, 4, 6, DefaultButton.BUTTON_4));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 83, y + 8, menu, width, height, 5, 37, DefaultButton.BUTTON_5));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 107, y + 10, menu, width, height, 6, 5, DefaultButton.BUTTON_6));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 126, y + 18, menu, width, height, 7, 28, DefaultButton.BUTTON_7));
		
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 142, y + 35, menu, width, height, 8, 23, DefaultButton.BUTTON_8));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 153, y + 58, menu, width, height, 9, 33, DefaultButton.BUTTON_9));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 159, y + 83, menu, width, height, 10, 11, DefaultButton.BUTTON_10));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 153, y + 107, menu, width, height, 11, 36, DefaultButton.BUTTON_11));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 142, y + 125, menu, width, height, 12, 10, DefaultButton.BUTTON_12));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 126, y + 141, menu, width, height, 13, 20, DefaultButton.BUTTON_13));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 107, y + 153, menu, width, height, 14, 2, DefaultButton.BUTTON_14));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 83, y + 159, menu, width, height, 15, 3, DefaultButton.BUTTON_15));
		
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 57, y + 153, menu, width, height, 16, 19, DefaultButton.BUTTON_16));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 34, y + 141, menu, width, height, 17, 8, DefaultButton.BUTTON_17));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 18, y + 125, menu, width, height, 18, 4, DefaultButton.BUTTON_18));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 10, y + 107, menu, width, height, 19, 31, DefaultButton.BUTTON_19));
		// Inner Buttons
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 35, y + 73, menu, width, height, 20, 14, DefaultButton.BUTTON_20));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 41, y + 55, menu, width, height, 21, 34, DefaultButton.BUTTON_21));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 52, y + 43, menu, width, height, 22, 29, DefaultButton.BUTTON_22));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 68, y + 36, menu, width, height, 23, 15, DefaultButton.BUTTON_23));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 87, y + 35, menu, width, height, 24, 27, DefaultButton.BUTTON_24));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 102, y + 36, menu, width, height, 25, 9, DefaultButton.BUTTON_25));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 113, y + 43, menu, width, height, 26, 32, DefaultButton.BUTTON_26));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 121, y + 55, menu, width, height, 27, 38, DefaultButton.BUTTON_27));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 127, y + 73, menu, width, height, 28, 25, DefaultButton.BUTTON_28));
		
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 128, y + 92, menu, width, height, 29, 22, DefaultButton.BUTTON_29));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 124, y + 106, menu, width, height, 30, 17, DefaultButton.BUTTON_30));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 117, y + 115, menu, width, height, 31, 13, DefaultButton.BUTTON_31));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 108, y + 123, menu, width, height, 32, 16, DefaultButton.BUTTON_32));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 97, y + 128, menu, width, height, 33, 1, DefaultButton.BUTTON_33));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 77, y + 128, menu, width, height, 34, 24, DefaultButton.BUTTON_34));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 59, y + 123, menu, width, height, 35, 35, DefaultButton.BUTTON_35));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 46, y + 115, menu, width, height, 36, 7, DefaultButton.BUTTON_36));
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 38, y + 106, menu, width, height, 37, 26, DefaultButton.BUTTON_37));
		
		this.addRenderableWidget(new MilkyWayDHDSymbolButton(x + 35, y + 92, menu, width, height, 38, 30, DefaultButton.BUTTON_38));
	}
	
}
