package net.povstalec.sgjourney.client.widgets.dhd;

import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.menu.dhd.IDHDMenu;
import net.povstalec.sgjourney.common.menu.dhd.MilkyWayDHDMenu;
import net.povstalec.sgjourney.common.menu.graver.DHDEngravingMenu;

public abstract class MilkyWayDHDBigButton<M extends IDHDMenu> extends DHDBigButton<M>
{
	public MilkyWayDHDBigButton(int x, int y, M menu, OnPress press)
	{
		super(x, y, menu, press, StargateJourney.sgjourneyLocation("textures/gui/dhd/milky_way/milky_way_dhd_big_red_button.png"));
	}
	
	
	
	public static class Dialing extends MilkyWayDHDBigButton<MilkyWayDHDMenu>
	{
		public Dialing(int x, int y, MilkyWayDHDMenu menu, OnPress press)
		{
			super(x, y, menu, press);
		}
	}
	
	
	
	public static class Engraving extends MilkyWayDHDBigButton<DHDEngravingMenu.MilkyWay>
	{
		public Engraving(int x, int y, DHDEngravingMenu.MilkyWay menu, OnPress press)
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
