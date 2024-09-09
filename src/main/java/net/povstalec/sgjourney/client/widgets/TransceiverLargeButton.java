package net.povstalec.sgjourney.client.widgets;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.StargateJourney;

public class TransceiverLargeButton extends SGJourneyButton
{
	public TransceiverLargeButton(int x, int y, Component component, Component tooltip, OnPress press)
	{
		super(new ResourceLocation(StargateJourney.MODID, "textures/gui/transceiver/transceiver_widgets.png"), x, y, 20, 20, 16, 0, component, tooltip, press);
	}
}
