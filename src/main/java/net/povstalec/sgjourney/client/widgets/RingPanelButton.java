package net.povstalec.sgjourney.client.widgets;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;

public class RingPanelButton extends SGJourneyButton
{
	public RingPanelButton(int x, int y, Component message, Component tooltip, OnPress press)
	{
		super(new ResourceLocation(StargateJourney.MODID, "textures/gui/widgets.png"), x, y, 32, 16, message, tooltip, press);
	}
}
