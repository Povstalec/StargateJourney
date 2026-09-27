package net.povstalec.sgjourney.client.screens.dhd;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.dhd.ClassicDHDBigButton;
import net.povstalec.sgjourney.client.widgets.dhd.ClassicDHDSymbolButton;
import net.povstalec.sgjourney.client.widgets.dhd.GenericDHDSymbolButton;
import net.povstalec.sgjourney.common.menu.ClassicDHDMenu;

public class ClassicDHDScreen extends AbstractDHDScreen<ClassicDHDMenu>
{
	public ClassicDHDScreen(ClassicDHDMenu menu, Inventory playerInventory, Component title)
	{
		super(menu, playerInventory, title, StargateJourney.sgjourneyLocation("textures/gui/dhd/classic/classic_dhd_background.png"));
	}
	
	@Override
	public void init()
	{
		super.init();
		addRenderableWidget(new ClassicDHDBigButton.Dialing(leftPos + 69, topPos + 69, menu, button ->
		{
			engageStargate();
			onClose();
		}));
		
		GenericDHDSymbolButton.DefaultButton defaultButton;
		for(int i = 0; i < 39; i++)
		{
			defaultButton = GenericDHDSymbolButton.DefaultButton.values()[i];
			addRenderableWidget(new ClassicDHDSymbolButton.Dialing(leftPos, topPos, menu, width, height, i, ClassicDHDSymbolButton.CANON_SYMBOLS[i], defaultButton,
				button -> encodeSymbol(((ClassicDHDSymbolButton.Dialing) button).getSymbol())));
		}
	}
}
