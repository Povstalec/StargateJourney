package net.povstalec.sgjourney.client.widgets.dhd;

import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.menu.dhd.AndromedaDHDMenu;
import net.povstalec.sgjourney.common.menu.dhd.IDHDMenu;
import net.povstalec.sgjourney.common.menu.graver.DHDEngravingMenu;

public abstract class AndromedaDHDBigButton<M extends IDHDMenu> extends DHDBigButton<M>
{
	public AndromedaDHDBigButton(int x, int y, M menu, OnPress press)
	{
		super(x, y, menu, press, StargateJourney.sgjourneyLocation("textures/gui/dhd/andromeda/andromeda_dhd_big_green_button.png"));
	}
	
	
	
	public static class Dialing extends AndromedaDHDBigButton<AndromedaDHDMenu>
	{
		public Dialing(int x, int y, AndromedaDHDMenu menu, OnPress press)
		{
			super(x, y, menu, press);
		}
	}
	
	
	
	public static class Engraving extends AndromedaDHDBigButton<DHDEngravingMenu.Andromeda>
	{
		public Engraving(int x, int y, DHDEngravingMenu.Andromeda menu, OnPress press)
		{
			super(x, y, menu, press);
		}
		
		@Override
		public boolean isOverButton(double mouseX, double mouseY)
		{
			return false;
		}
		
		@Override
		public boolean isMouseOver(double mouseX, double mouseY)
		{
			return false;
		}
		
		@Override
		protected boolean clicked(double mouseX, double mouseY)
		{
			return false;
		}
	}
}
