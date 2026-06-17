package net.povstalec.sgjourney.common.sgjourney.transporter;

import net.minecraft.server.MinecraftServer;

public abstract class SGJourneyTransportRings extends SGJourneyTransporter
{
	public static final double INNER_RADIUS = 2;
	
	public SGJourneyTransportRings(TransporterType<?> type, MinecraftServer server)
	{
		super(type, server);
	}
	
	@Override
	public double getInnerRadius()
	{
		return INNER_RADIUS;
	}
}
