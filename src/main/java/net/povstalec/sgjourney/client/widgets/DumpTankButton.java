package net.povstalec.sgjourney.client.widgets;

import net.minecraft.network.chat.TextComponent;
import net.minecraft.network.chat.TranslatableComponent;
import net.povstalec.sgjourney.StargateJourney;

public class DumpTankButton extends SGJourneyButton
{
	public DumpTankButton(int x, int y, OnPress press)
	{
		super(StargateJourney.sgjourneyLocation("textures/gui/widgets.png"), x, y, 10, 10, TextComponent.EMPTY, new TranslatableComponent("tooltip.sgjourney.dump_tank"), press);
	}
}
