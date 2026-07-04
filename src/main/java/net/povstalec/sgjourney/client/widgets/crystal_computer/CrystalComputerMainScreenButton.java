package net.povstalec.sgjourney.client.widgets.crystal_computer;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.SGJourneyButton;

public class CrystalComputerMainScreenButton extends SGJourneyButton
{
	public CrystalComputerMainScreenButton(int x, int y, Component component, Component tooltip, OnPress press)
	{
		super(StargateJourney.sgjourneyLocation("textures/gui/pocket_crystal_computer_widgets.png"), x, y, 20, 20,
				CrystalComputerSaveButton.LARGE_BUTTON_WIDTH + CrystalComputerSaveButton.SMALL_BUTTON_WIDTH, 0, component, tooltip, press);
	}
}
