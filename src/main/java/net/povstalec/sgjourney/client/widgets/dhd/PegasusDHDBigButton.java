package net.povstalec.sgjourney.client.widgets.dhd;

import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.menu.dhd.IDHDMenu;
import net.povstalec.sgjourney.common.menu.dhd.PegasusDHDMenu;

public abstract class PegasusDHDBigButton<M extends IDHDMenu> extends DHDBigButton<M>
{
	public PegasusDHDBigButton(int x, int y, M menu, OnPress press)
	{
		super(x, y, menu, press, StargateJourney.sgjourneyLocation("textures/gui/dhd/pegasus/pegasus_dhd_big_blue_button.png"));
	}
	
	
	
	public static class Dialing extends PegasusDHDBigButton<PegasusDHDMenu>
	{
		public Dialing(int x, int y, PegasusDHDMenu menu, OnPress press)
		{
			super(x, y, menu, press);
		}
	}
}
