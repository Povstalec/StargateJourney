package net.povstalec.sgjourney.client.widgets;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;

public class GDOButton extends SGJourneyButton
{
	public static final ResourceLocation WIDGETS_LOCATION = new ResourceLocation(StargateJourney.MODID, "textures/gui/gdo/gdo_widgets.png");
	
    public GDOButton(int x, int y, Component component, Component tooltip, OnPress press)
	{
		super(new ResourceLocation(StargateJourney.MODID, "textures/gui/gdo/gdo_widgets.png"), x, y, 16, 10, component, tooltip, press);
	}

    public GDOButton(int x, int y, Component component, OnPress press)
	{
		this(x, y, component, component, press);
	}
}
