package net.povstalec.sgjourney.client.widgets;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;

public class GDOLargeButton extends SGJourneyButton
{
	public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/gui/gdo/gdo_widgets.png");
	
    public GDOLargeButton(int x, int y, Component component, OnPress press)
	{
		super(new ResourceLocation(StargateJourney.MODID, "textures/gui/gdo/gdo_widgets.png"), x, y, 29, 28, 16, 0, component, press);
	}
}
