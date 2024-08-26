package net.povstalec.sgjourney.common.stargate;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.StargateJourney;

public class TransporterConnection
{
	private final UUID uuid;
	private final Transporter transporterA;
	private final Transporter transporterB;
	
	private final int timeOffsetA; // Time it takes before A can activate
	private final int timeOffsetB ; // Time it takes before B can activate
	
	private TransporterConnection(MinecraftServer server, UUID uuid, Transporter transporterA, Transporter transporterB)
	{
		this.uuid = uuid;
		this.transporterA = transporterA;
		this.transporterB = transporterB;
		
		this.timeOffsetA = transporterA.getTimeOffset(server);
		this.timeOffsetB = transporterB.getTimeOffset(server);
	}
	
	@Nullable
	public static final TransporterConnection create(MinecraftServer server, Transporter transporterA, Transporter transporterB)
	{
		UUID uuid = UUID.randomUUID();
		
		if(transporterA != null && transporterB != null)
			return new TransporterConnection(server, uuid, transporterA, transporterB);
		
		return null;
	}
	
	public final void tick(MinecraftServer server)
	{
		if(!isTransporterValid(server, this.transporterA) || !isTransporterValid(server, this.transporterB))
		{
			//TODO
			return;
		}
	}
	
	public final boolean isTransporterValid(MinecraftServer server, Transporter transporter)
	{
		if(transporter == null)
		{
			StargateJourney.LOGGER.error("Transporter does not exist");
			return false;
		}
		
		if(transporter.getTransporterEntity(server).isPresent())
		{
			if(transporter.getTransporterEntity(server).get().isConnected())
				return true;

			StargateJourney.LOGGER.info("Transporter is not connected");
		}
		else
			StargateJourney.LOGGER.info("Transporter not found");
		
		return false;
	}
}
