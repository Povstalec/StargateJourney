package net.povstalec.sgjourney.client.widgets.crystal_computer;

import net.minecraft.network.chat.Component;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.SGJourneyButton;

public class CrystalComputerPhysicalButton extends SGJourneyButton
{
	public CrystalComputerPhysicalButton(int x, int y, int width, int height, int xOffset, int yOffset, Component component, Component tooltip, OnPress press)
	{
		super(StargateJourney.sgjourneyLocation("textures/gui/pocket_crystal_computer_widgets.png"), x, y, width, height, xOffset, yOffset, component, tooltip, press);
	}
	
	public static CrystalComputerPhysicalButton mainScreenButton(int x, int y, Component component, Component tooltip, OnPress press)
	{
		return new CrystalComputerPhysicalButton(x, y, 26, 26, 0, 60, component, tooltip, press);
	}
	
	public static CrystalComputerPhysicalButton switchTargetButton(int x, int y, Component component, Component tooltip, OnPress press)
	{
		return new CrystalComputerPhysicalButton(x, y, 26, 14, 26, 60, component, tooltip, press);
	}
}
