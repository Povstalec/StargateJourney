package net.povstalec.sgjourney.client.screens.dhd;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.dhd.GenericDHDSymbolButton.DefaultButton;
import net.povstalec.sgjourney.client.widgets.dhd.UniverseDHDBigButton;
import net.povstalec.sgjourney.client.widgets.dhd.UniverseDHDSymbolButton;
import net.povstalec.sgjourney.common.menu.dhd.UniverseDHDMenu;

public class UniverseDHDScreen extends AbstractDHDScreen<UniverseDHDMenu>
{
	public UniverseDHDScreen(UniverseDHDMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title, StargateJourney.sgjourneyLocation("textures/gui/dhd/universe/universe_dhd_background.png"));
	}
	
	@Override
	public void init()
	{
		super.init();
		addRenderableWidget(new UniverseDHDBigButton.Dialing(leftPos + 69, topPos + 69, menu, button ->
		{
			engageStargate();
			onClose();
		}));
		
		DefaultButton defaultButton;
		for(int i = 0; i < 39; i++)
		{
			defaultButton = DefaultButton.values()[i];
			addRenderableWidget(new UniverseDHDSymbolButton.Dialing(leftPos, topPos, menu, width, height, i, UniverseDHDSymbolButton.CANON_SYMBOLS[i], defaultButton,
				button -> encodeSymbol(((UniverseDHDSymbolButton.Dialing) button).getSymbol())));
		}
	}
}
