package net.povstalec.sgjourney.client.screens.dhd;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.dhd.GenericDHDSymbolButton;
import net.povstalec.sgjourney.client.widgets.dhd.PegasusDHDBigButton;
import net.povstalec.sgjourney.client.widgets.dhd.PegasusDHDSymbolButton;
import net.povstalec.sgjourney.common.menu.dhd.PegasusDHDMenu;

public class PegasusDHDScreen extends AbstractDHDScreen<PegasusDHDMenu>
{
	public PegasusDHDScreen(PegasusDHDMenu pMenu, Inventory pPlayerInventory, Component pTitle)
	{
		super(pMenu, pPlayerInventory, pTitle, StargateJourney.sgjourneyLocation("textures/gui/dhd/pegasus/pegasus_dhd_background.png"));
	}
	
	@Override
	public void init()
	{
		super.init();
		addRenderableWidget(new PegasusDHDBigButton.Dialing(leftPos + 69, topPos + 69, menu, button ->
		{
			engageStargate();
			onClose();
		}));
		
		GenericDHDSymbolButton.DefaultButton defaultButton;
		for(int i = 0; i < 39; i++)
		{
			defaultButton = GenericDHDSymbolButton.DefaultButton.values()[i];
			addRenderableWidget(new PegasusDHDSymbolButton.Dialing(leftPos, topPos, menu, width, height, i, PegasusDHDSymbolButton.CANON_SYMBOLS[i], defaultButton,
				button -> encodeSymbol(((PegasusDHDSymbolButton.Dialing) button).getSymbol())));
		}
	}
}
