package net.povstalec.sgjourney.common.sgjourney;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.TransporterNetwork;

public class TransporterConnection
{
	private final UUID uuid;
	private final Transporter transporterA;
	private final Transporter transporterB;
	
	private final int timeOffsetA; // Time it takes before A can activate
	private final int timeOffsetB ; // Time it takes before B can activate
	
	private final int transportStartTicks;
	
	protected int connectionTime = 0;
	
	private TransporterConnection(MinecraftServer server, UUID uuid, Transporter transporterA, Transporter transporterB)
	{
		this.uuid = uuid;
		this.transporterA = transporterA;
		this.transporterB = transporterB;
		
		this.timeOffsetA = transporterA.getTimeOffset(server);
		this.timeOffsetB = transporterB.getTimeOffset(server);
		
		this.transportStartTicks = timeOffsetA > timeOffsetB ? timeOffsetA : timeOffsetB;
	}
	
	@Nullable
	public static final TransporterConnection create(MinecraftServer server, Transporter transporterA, Transporter transporterB)
	{
		if(transporterA != null && transporterB != null)
			return new TransporterConnection(server, UUID.randomUUID(), transporterA, transporterB);
		
		return null;
	}
	
	
	
	private int absDifference()
	{
		return Math.abs(timeOffsetA - timeOffsetB);
	}
	
	public final void tick(MinecraftServer server)
	{
		if(!isTransporterValid(server, this.transporterA) || !isTransporterValid(server, this.transporterB))
		{
			terminate(server);
			return;
		}
		
		if(connectionTime == transportStartTicks)
			transport(server);
		
		increaseTicks();
		
		if(connectionTime >= 2 * transportStartTicks)
			terminate(server);
	}
	
	private void increaseTicks()
	{
		this.connectionTime++;
	}
	
	private void transport(MinecraftServer server)
	{
		BlockPos transportPosA = transporterA.transportPos(server);
		BlockPos transportPosB = transporterB.transportPos(server);
		
		if(transportPosA == null || transportPosB == null)
		{
			terminate(server);
			return;
		}
		
		List<Entity> entitiesA = transporterA.entitiesToTransport(server);
		List<Entity> entitiesB = transporterB.entitiesToTransport(server);
		
		transportEntities(entitiesA, transportPosA, transportPosB);
		transportEntities(entitiesB, transportPosB, transportPosA);
	}
	
	private static void transportEntities(List<Entity> entities, BlockPos from, BlockPos to)
	{
		for(Entity entity : entities)
		{
			double xOffset = entity.getX() - from.getX();
			double yOffset = entity.getY() - from.getY();
			double zOffset = entity.getZ() - from.getZ();
			
			entity.teleportTo((to.getX() + xOffset), (to.getY() + yOffset), (to.getZ() + zOffset));
		}
	}
	
	public final void terminate(MinecraftServer server)
	{
		//TODO reset Transporter blocks
		
		TransporterNetwork.get(server).terminateConnection(uuid);
	}
	
	public final boolean isTransporterValid(MinecraftServer server, Transporter transporter)
	{
		if(transporter == null)
		{
			StargateJourney.LOGGER.error("Transporter does not exist");
			return false;
		}
		
		if(transporter.getTransporterEntity(server) != null)
		{
			if(transporter.getTransporterEntity(server).isConnected())
				return true;

			StargateJourney.LOGGER.info("Transporter is not connected");
		}
		else
			StargateJourney.LOGGER.info("Transporter not found");
		
		return false;
	}
	
	
	
	public UUID getID()
	{
		return this.uuid;
	}
}
