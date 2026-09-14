package net.povstalec.sgjourney.common.sgjourney.transporter.transport_rings.ancient;

import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.common.sgjourney.transporter.transport_rings.SGJourneyTransportRings;
import net.povstalec.sgjourney.common.sgjourney.transporter.TransporterType;

public abstract class AncientTransportRings extends SGJourneyTransportRings
{
	public AncientTransportRings(TransporterType<?> type, MinecraftServer server)
	{
		super(type, server);
	}
}
