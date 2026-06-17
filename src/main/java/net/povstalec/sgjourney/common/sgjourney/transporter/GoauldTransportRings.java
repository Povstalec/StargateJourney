package net.povstalec.sgjourney.common.sgjourney.transporter;

import net.minecraft.server.MinecraftServer;

public abstract class GoauldTransportRings extends SGJourneyTransportRings
{
	public GoauldTransportRings(TransporterType<?> type, MinecraftServer server)
	{
		super(type, server);
	}
}
