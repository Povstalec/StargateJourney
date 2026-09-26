package net.povstalec.sgjourney.client.screens.dhd;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.dhd.GenericDHDSymbolButton.DefaultButton;
import net.povstalec.sgjourney.client.widgets.dhd.MilkyWayDHDBigButton;
import net.povstalec.sgjourney.client.widgets.dhd.MilkyWayDHDSymbolButton;
import net.povstalec.sgjourney.common.menu.dhd.MilkyWayDHDMenu;

public class MilkyWayDHDScreen extends AbstractDHDScreen<MilkyWayDHDMenu>
{
	public MilkyWayDHDScreen(MilkyWayDHDMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title, StargateJourney.sgjourneyLocation("textures/gui/dhd/milky_way/milky_way_dhd_background.png"));
	}
	
	@Override
	public void init()
	{
		super.init();
		addRenderableWidget(new MilkyWayDHDBigButton.Dialing(leftPos + 69, topPos + 69, menu, button ->
		{
			engageStargate();
			onClose();
		}));
		
		DefaultButton defaultButton;
		for(int i = 0; i < 39; i++)
		{
			defaultButton = DefaultButton.values()[i];
			addRenderableWidget(new MilkyWayDHDSymbolButton.Dialing(leftPos, topPos, menu, width, height, i, MilkyWayDHDSymbolButton.CANON_SYMBOLS[i], defaultButton,
				button -> encodeSymbol(((MilkyWayDHDSymbolButton.Dialing) button).getSymbol())));
		}
	}
}
