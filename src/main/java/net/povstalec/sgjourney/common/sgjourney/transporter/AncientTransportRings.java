package net.povstalec.sgjourney.common.sgjourney.transporter;

import net.minecraft.server.MinecraftServer;

public abstract class AncientTransportRings extends SGJourneyTransportRings
{
	public AncientTransportRings(TransporterType<?> type, MinecraftServer server)
	{
		super(type, server);
	}
}
