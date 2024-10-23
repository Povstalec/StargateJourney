package net.povstalec.sgjourney.client.screens;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.DHDBigButton;
import net.povstalec.sgjourney.client.widgets.PegasusDHDSymbolButton;
import net.povstalec.sgjourney.common.menu.AbstractDHDMenu;

public class PegasusDHDScreen extends AbstractDHDScreen
{
	private static final ResourceLocation TEXTURE = StargateJourney.sgjourneyLocation("textures/gui/dhd/pegasus/pegasus_dhd_background.png");
	
	public PegasusDHDScreen(AbstractDHDMenu pMenu, Inventory pPlayerInventory, Component pTitle)
	{
		super(pMenu, pPlayerInventory, pTitle, TEXTURE);
	}
	
	@Override
	public void init()
	{
		int x = (width - imageWidth) / 2;
		int y = (height - imageHeight) / 2;
		super.init();
		this.addRenderableWidget(new DHDBigButton.Pegasus(x + 69, y + 69, menu, (n) -> {menu.engageChevron(0); this.onClose();}));
		
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 8, y + 68, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_1));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 14, y + 43, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_2));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 27, y + 23, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_3));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 50, y + 11, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_4));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 75, y + 8, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_5));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 103, y + 9, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_6));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 122, y + 16, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_7));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 140, y + 32, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_8));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 152, y + 56, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_9));
		
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 158, y + 82, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_10));
		
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 152, y + 108, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_11));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 140, y + 127, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_12));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 122, y + 143, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_13));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 103, y + 154, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_14));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 75, y + 158, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_15));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 50, y + 150, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_16));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 27, y + 135, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_17));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 14, y + 118, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_18));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 8, y + 97, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_19));
		
		
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 35, y + 77, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_20));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 39, y + 59, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_21));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 49, y + 46, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_22));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 64, y + 38, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_23));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 82, y + 35, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_24));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 99, y + 35, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_25));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 110, y + 41, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_26));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 119, y + 52, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_27));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 126, y + 68, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_28));
		
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 129, y + 87, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_29));
		
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 126, y + 102, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_30));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 119, y + 113, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_31));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 110, y + 121, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_32));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 99, y + 127, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_33));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 82, y + 128, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_34));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 64, y + 124, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_35));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 49, y + 117, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_36));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 39, y + 108, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_37));
		this.addRenderableWidget(new PegasusDHDSymbolButton(x + 35, y + 97, width, height, menu, PegasusDHDSymbolButton.PegasusButton.BUTTON_38));
	}
	
}
