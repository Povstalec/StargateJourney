package net.povstalec.sgjourney.common.sgjourney.transporter;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.phys.Vec3;

import javax.annotation.Nullable;

public abstract class SGJourneyTransportRings extends SGJourneyTransporter
{
	public static final double INNER_RADIUS = 2;
	
	public SGJourneyTransportRings(TransporterType<?> type, MinecraftServer server)
	{
		super(type, server);
	}
	
	@Override
	public @Nullable Vec3 getForward()
	{
		return FORWARD;
	}
	
	@Override
	public @Nullable Vec3 getUp()
	{
		return UP;
	}
	
	@Override
	public @Nullable Vec3 getRight()
	{
		return RIGHT;
	}
	
	@Override
	public double getInnerRadius()
	{
		return INNER_RADIUS;
	}
}
