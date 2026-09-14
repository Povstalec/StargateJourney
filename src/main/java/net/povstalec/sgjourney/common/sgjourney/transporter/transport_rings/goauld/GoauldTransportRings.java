package net.povstalec.sgjourney.common.sgjourney.transporter.transport_rings.goauld;

import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.common.sgjourney.transporter.transport_rings.SGJourneyTransportRings;
import net.povstalec.sgjourney.common.sgjourney.transporter.TransporterType;

public abstract class GoauldTransportRings extends SGJourneyTransportRings
{
	public GoauldTransportRings(TransporterType<?> type, MinecraftServer server)
	{
		super(type, server);
	}
}
