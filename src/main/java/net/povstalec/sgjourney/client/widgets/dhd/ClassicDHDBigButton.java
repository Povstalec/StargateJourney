package net.povstalec.sgjourney.client.widgets.dhd;

import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.menu.ClassicDHDMenu;
import net.povstalec.sgjourney.common.menu.dhd.IDHDMenu;
import net.povstalec.sgjourney.common.menu.graver.DHDEngravingMenu;

public abstract class ClassicDHDBigButton<M extends IDHDMenu> extends DHDBigButton<M>
{
	public ClassicDHDBigButton(int x, int y, M menu, OnPress press)
	{
		super(x, y, menu, press, StargateJourney.sgjourneyLocation("textures/gui/dhd/classic/classic_dhd_big_orange_button.png"));
	}
	
	
	
	public static class Dialing extends ClassicDHDBigButton<ClassicDHDMenu>
	{
		public Dialing(int x, int y, ClassicDHDMenu menu, OnPress press)
		{
			super(x, y, menu, press);
		}
	}
	
	
	
	public static class Engraving extends ClassicDHDBigButton<DHDEngravingMenu.Classic>
	{
		public Engraving(int x, int y, DHDEngravingMenu.Classic menu, OnPress press)
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
