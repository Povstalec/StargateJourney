package net.povstalec.sgjourney.client.widgets.dhd;

import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.menu.dhd.IDHDMenu;
import net.povstalec.sgjourney.common.menu.dhd.UniverseDHDMenu;
import net.povstalec.sgjourney.common.menu.graver.DHDEngravingMenu;

public abstract class UniverseDHDBigButton<M extends IDHDMenu> extends DHDBigButton<M>
{
	public UniverseDHDBigButton(int x, int y, M menu, OnPress press)
	{
		super(x, y, menu, press, StargateJourney.sgjourneyLocation("textures/gui/dhd/universe/universe_dhd_big_white_button.png"));
	}
	
	
	
	public static class Dialing extends UniverseDHDBigButton<UniverseDHDMenu>
	{
		public Dialing(int x, int y, UniverseDHDMenu menu, OnPress press)
		{
			super(x, y, menu, press);
		}
	}
	
	
	
	public static class Engraving extends UniverseDHDBigButton<DHDEngravingMenu.Universe>
	{
		public Engraving(int x, int y, DHDEngravingMenu.Universe menu, OnPress press)
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
