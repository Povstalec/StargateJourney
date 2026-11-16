package net.povstalec.sgjourney.client.widgets;

import net.minecraft.network.chat.Component;
import net.povstalec.sgjourney.StargateJourney;

public class DumpTankButton extends SGJourneyButton
{
	public DumpTankButton(int x, int y, OnPress press)
	{
		super(StargateJourney.sgjourneyLocation("textures/gui/widgets.png"), x, y, 10, 10, Component.empty(), Component.translatable("tooltip.sgjourney.dump_tank"), press);
	}
}
