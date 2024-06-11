package net.povstalec.sgjourney.common.stargate;

import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.server.MinecraftServer;
import net.povstalec.sgjourney.StargateJourney;

public class TransporterConnection
{
	protected final UUID uuid;
	protected final Transporter transporterA;
	protected final Transporter transporterB;
	
	private TransporterConnection(UUID uuid, Transporter transporterA, Transporter transporterB)
	{
		this.uuid = uuid;
		this.transporterA = transporterA;
		this.transporterB = transporterB;
	}
	
	@Nullable
	public static final TransporterConnection create(Transporter transporterA, Transporter transporterB)
	{
		UUID uuid = UUID.randomUUID();
		
		if(transporterA != null && transporterB != null)
			return new TransporterConnection(uuid, transporterA, transporterB);
		
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
