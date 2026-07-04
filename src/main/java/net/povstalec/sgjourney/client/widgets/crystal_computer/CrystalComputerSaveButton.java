package net.povstalec.sgjourney.client.widgets.crystal_computer;

import net.minecraft.network.chat.Component;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.SGJourneyButton;

public class CrystalComputerSaveButton extends SGJourneyButton
{
	public static final int LARGE_BUTTON_WIDTH = 158;
	public static final int SMALL_BUTTON_WIDTH = 76;
	public static final int BUTTON_HEIGHT = 20;
	
	public CrystalComputerSaveButton(int x, int y, int width, int height, int xOffset, Component text, Component tooltip, boolean active, OnPress press)
	{
		super(StargateJourney.sgjourneyLocation("textures/gui/pocket_crystal_computer_widgets.png"), x, y, width, height, xOffset, 0, text, tooltip, press);
		
		this.active = active;
	}
	
	public static CrystalComputerSaveButton big(int x, int y, Component text, Component tooltip, boolean active, OnPress press)
	{
		return new CrystalComputerSaveButton(x, y, LARGE_BUTTON_WIDTH, BUTTON_HEIGHT, 0, text, tooltip, active, press);
	}
	
	public static CrystalComputerSaveButton small(int x, int y, Component text, Component tooltip, boolean active, OnPress press)
	{
		return new CrystalComputerSaveButton(x, y, SMALL_BUTTON_WIDTH, BUTTON_HEIGHT, LARGE_BUTTON_WIDTH, text, tooltip, active, press);
	}
}
