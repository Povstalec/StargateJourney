package net.povstalec.sgjourney.client.screens.dhd;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.dhd.AndromedaDHDBigButton;
import net.povstalec.sgjourney.client.widgets.dhd.AndromedaDHDSymbolButton;
import net.povstalec.sgjourney.client.widgets.dhd.GenericDHDSymbolButton.DefaultButton;
import net.povstalec.sgjourney.common.menu.dhd.AndromedaDHDMenu;

public class AndromedaDHDScreen extends AbstractDHDScreen<AndromedaDHDMenu>
{
	public AndromedaDHDScreen(AndromedaDHDMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title, StargateJourney.sgjourneyLocation("textures/gui/dhd/andromeda/andromeda_dhd_background.png"));
	}
	
	@Override
	public void init()
	{
		super.init();
		addRenderableWidget(new AndromedaDHDBigButton.Dialing(leftPos + 69, topPos + 69, menu, button ->
		{
			engageStargate();
			onClose();
		}));
		
		DefaultButton defaultButton;
		for(int i = 0; i < 39; i++)
		{
			defaultButton = DefaultButton.values()[i];
			addRenderableWidget(new AndromedaDHDSymbolButton.Dialing(leftPos, topPos, menu, width, height, i, AndromedaDHDSymbolButton.CANON_SYMBOLS[i], defaultButton,
				button -> encodeSymbol(((AndromedaDHDSymbolButton.Dialing) button).getSymbol())));
		}
	}
}
