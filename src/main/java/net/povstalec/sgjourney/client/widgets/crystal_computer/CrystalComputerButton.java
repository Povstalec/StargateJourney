package net.povstalec.sgjourney.client.widgets.crystal_computer;

import net.minecraft.network.chat.Component;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.widgets.SGJourneyButton;

public class CrystalComputerButton extends SGJourneyButton
{
	public static final int ENTRY_BUTTON_WIDTH = 110;
	public static final int LARGE_BUTTON_WIDTH = 158;
	public static final int SMALL_BUTTON_WIDTH = 112;
	public static final int PAGE_BUTTON_WIDTH = 32;
	
	public CrystalComputerButton(int x, int y, int width, int height, int xOffset, int yOffset, boolean active, Component component, Component tooltip, OnPress press)
	{
		super(StargateJourney.sgjourneyLocation("textures/gui/pocket_crystal_computer_widgets.png"), x, y, width, height, xOffset, yOffset, component, tooltip, press);
		
		this.active = active;
	}
	
	public static CrystalComputerButton mainScreenButton(int x, int y, Component component, Component tooltip, OnPress press)
	{
		return new CrystalComputerButton(x, y, 26, 26, 0, 156, true, component, tooltip, press);
	}
	
	public static CrystalComputerButton switchTargetButton(int x, int y, Component component, Component tooltip, OnPress press)
	{
		return new CrystalComputerButton(x, y, 26, 14, 26, 156, true, component, tooltip, press);
	}
	
	
	
	public static CrystalComputerButton entryButton(int x, int y, boolean active, Component component, Component tooltip, OnPress press)
	{
		return new CrystalComputerButton(x, y, ENTRY_BUTTON_WIDTH, 16, 0, 0, active, component, tooltip, press);
	}
	
	public static CrystalComputerButton copyButton(int x, int y, boolean active, Component tooltip, OnPress press)
	{
		return new CrystalComputerButton(x, y, 16, 16, ENTRY_BUTTON_WIDTH, 0, active, Component.empty(), tooltip, press);
	}
	
	public static CrystalComputerButton pasteButton(int x, int y, boolean active, Component tooltip, OnPress press)
	{
		return new CrystalComputerButton(x, y, 16, 16, ENTRY_BUTTON_WIDTH + 16, 0, active, Component.empty(), tooltip, press);
	}
	
	public static CrystalComputerButton deleteButton(int x, int y, boolean active, Component tooltip, OnPress press)
	{
		return new CrystalComputerButton(x, y, 16, 16, ENTRY_BUTTON_WIDTH + 32, 0, active, Component.empty(), tooltip, press);
	}
	
	public static CrystalComputerButton moveDownButton(int x, int y, boolean active, Component tooltip, OnPress press)
	{
		return new CrystalComputerButton(x, y, 16, 8, ENTRY_BUTTON_WIDTH + 48, 0, active, Component.empty(), tooltip, press);
	}
	
	public static CrystalComputerButton moveUpButton(int x, int y, boolean active, Component tooltip, OnPress press)
	{
		return new CrystalComputerButton(x, y, 16, 8, ENTRY_BUTTON_WIDTH + 64, 0, active, Component.empty(), tooltip, press);
	}
	
	
	public static CrystalComputerButton pageBackButton(int x, int y, boolean active, Component tooltip, OnPress press)
	{
		return new CrystalComputerButton(x, y, PAGE_BUTTON_WIDTH, 16, 0, 48, active, Component.empty(), tooltip, press);
	}
	
	public static CrystalComputerButton smallButton(int x, int y, boolean active, Component component, Component tooltip, OnPress press)
	{
		return new CrystalComputerButton(x, y, SMALL_BUTTON_WIDTH, 16, PAGE_BUTTON_WIDTH, 48, active, component, tooltip, press);
	}
	
	public static CrystalComputerButton pageForwardButton(int x, int y, boolean active, Component tooltip, OnPress press)
	{
		return new CrystalComputerButton(x, y, PAGE_BUTTON_WIDTH, 16, PAGE_BUTTON_WIDTH + SMALL_BUTTON_WIDTH, 48, active, Component.empty(), tooltip, press);
	}

	
	
	public static CrystalComputerButton largeButton(int x, int y, boolean active, Component component, Component tooltip, OnPress press)
	{
		return new CrystalComputerButton(x, y, LARGE_BUTTON_WIDTH, 20, 0, 96, active, component, tooltip, press);
	}
}
