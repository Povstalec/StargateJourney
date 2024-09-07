package net.povstalec.sgjourney.client.widgets;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;

public class GDOButton extends SGJourneyButton
{
	public GDOButton(int x, int y, Component component, OnPress press)
	{
		super(new ResourceLocation(StargateJourney.MODID, "textures/gui/gdo/gdo_widgets.png"), x, y, 16, 10, component, press);
	}
}
