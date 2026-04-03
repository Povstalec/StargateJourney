package net.povstalec.sgjourney.client.screens;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.PegasusDHDSymbolButton;
import net.povstalec.sgjourney.client.widgets.DHDBigButton;
import net.povstalec.sgjourney.client.widgets.GenericDHDSymbolButton;
import net.povstalec.sgjourney.common.menu.PegasusDHDMenu;

public class PegasusDHDScreen extends AbstractDHDScreen<PegasusDHDMenu>
{
	private static final ResourceLocation TEXTURE = new ResourceLocation(StargateJourney.MODID, "textures/gui/dhd/pegasus/pegasus_dhd_background.png");
	
	public PegasusDHDScreen(PegasusDHDMenu pMenu, Inventory pPlayerInventory, Component pTitle)
	{
		super(pMenu, pPlayerInventory, pTitle, TEXTURE);
	}
	
	@Override
	public void init()
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		super.init();
		this.addRenderableWidget(new DHDBigButton.Pegasus(x + 69, y + 69, menu, (n) -> {menu.engageStargate(); this.onClose();}));
		// Outer Buttons
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 8, y + 83, menu, width, height, 0, 8, GenericDHDSymbolButton.DefaultButton.BUTTON_0));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 10, y + 58, menu, width, height, 1, 34, GenericDHDSymbolButton.DefaultButton.BUTTON_1));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 18, y + 35, menu, width, height, 2, 13, GenericDHDSymbolButton.DefaultButton.BUTTON_2));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 34, y + 18, menu, width, height, 3, 1, GenericDHDSymbolButton.DefaultButton.BUTTON_3));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 57, y + 10, menu, width, height, 4, 16, GenericDHDSymbolButton.DefaultButton.BUTTON_4));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 83, y + 8, menu, width, height, 5, 19, GenericDHDSymbolButton.DefaultButton.BUTTON_5));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 107, y + 10, menu, width, height, 6, 5, GenericDHDSymbolButton.DefaultButton.BUTTON_6));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 126, y + 18, menu, width, height, 7, 11, GenericDHDSymbolButton.DefaultButton.BUTTON_7));
		
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 142, y + 35, menu, width, height, 8, 18, GenericDHDSymbolButton.DefaultButton.BUTTON_8));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 153, y + 58, menu, width, height, 9, 17, GenericDHDSymbolButton.DefaultButton.BUTTON_9));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 159, y + 83, menu, width, height, 10, 10, GenericDHDSymbolButton.DefaultButton.BUTTON_10));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 153, y + 107, menu, width, height, 11, 31, GenericDHDSymbolButton.DefaultButton.BUTTON_11));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 142, y + 125, menu, width, height, 12, 0, GenericDHDSymbolButton.DefaultButton.BUTTON_12));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 126, y + 141, menu, width, height, 13, 6, GenericDHDSymbolButton.DefaultButton.BUTTON_13));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 107, y + 153, menu, width, height, 14, 20, GenericDHDSymbolButton.DefaultButton.BUTTON_14));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 83, y + 159, menu, width, height, 15, 33, GenericDHDSymbolButton.DefaultButton.BUTTON_15));
		
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 57, y + 153, menu, width, height, 16, 2, GenericDHDSymbolButton.DefaultButton.BUTTON_16));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 34, y + 141, menu, width, height, 17, 38, GenericDHDSymbolButton.DefaultButton.BUTTON_17));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 18, y + 125, menu, width, height, 18, 21, GenericDHDSymbolButton.DefaultButton.BUTTON_18));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 10, y + 107, menu, width, height, 19, 12, GenericDHDSymbolButton.DefaultButton.BUTTON_19));
		// Inner Buttons
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 35, y + 73, menu, width, height, 20, 14, GenericDHDSymbolButton.DefaultButton.BUTTON_20));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 41, y + 55, menu, width, height, 21, 29, GenericDHDSymbolButton.DefaultButton.BUTTON_21));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 52, y + 43, menu, width, height, 22, 36, GenericDHDSymbolButton.DefaultButton.BUTTON_22));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 68, y + 36, menu, width, height, 23, 4, GenericDHDSymbolButton.DefaultButton.BUTTON_23));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 87, y + 35, menu, width, height, 24, 28, GenericDHDSymbolButton.DefaultButton.BUTTON_24));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 102, y + 36, menu, width, height, 25, 35, GenericDHDSymbolButton.DefaultButton.BUTTON_25));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 113, y + 43, menu, width, height, 26, 7, GenericDHDSymbolButton.DefaultButton.BUTTON_26));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 121, y + 55, menu, width, height, 27, 32, GenericDHDSymbolButton.DefaultButton.BUTTON_27));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 127, y + 73, menu, width, height, 28, 24, GenericDHDSymbolButton.DefaultButton.BUTTON_28));
		
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 128, y + 92, menu, width, height, 29, 22, GenericDHDSymbolButton.DefaultButton.BUTTON_29));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 124, y + 106, menu, width, height, 30, 27, GenericDHDSymbolButton.DefaultButton.BUTTON_30));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 117, y + 115, menu, width, height, 31, 26, GenericDHDSymbolButton.DefaultButton.BUTTON_31));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 108, y + 123, menu, width, height, 32, 23, GenericDHDSymbolButton.DefaultButton.BUTTON_32));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 97, y + 128, menu, width, height, 33, 25, GenericDHDSymbolButton.DefaultButton.BUTTON_33));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 77, y + 128, menu, width, height, 34, 15, GenericDHDSymbolButton.DefaultButton.BUTTON_34));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 59, y + 123, menu, width, height, 35, 37, GenericDHDSymbolButton.DefaultButton.BUTTON_35));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 46, y + 115, menu, width, height, 36, 30, GenericDHDSymbolButton.DefaultButton.BUTTON_36));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 38, y + 106, menu, width, height, 37, 3, GenericDHDSymbolButton.DefaultButton.BUTTON_37));
		
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 35, y + 92, menu, width, height, 38, 9, GenericDHDSymbolButton.DefaultButton.BUTTON_38));
	}
	
}
