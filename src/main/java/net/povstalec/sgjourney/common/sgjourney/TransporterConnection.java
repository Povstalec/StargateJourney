package net.povstalec.sgjourney.common.sgjourney;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

public class TransporterConnection
{
	public static final String ID = "id";
	public static final String TRANSPORTER_A = "transporter_a";
	public static final String TRANSPORTER_B = "transporter_b";
	public static final String CONNECTION_TIME = "connection_time";
	
	public static final int RING_TICKS = 16; // Number of ticks it takes the rings to get into position
	public static final int TRANSPORT_TICKS = 30; // Number of ticks it takes to start transporting
	
	private final UUID uuid;
	private final Transporter transporterA;
	private final Transporter transporterB;
	
	private final int timeOffsetA; // Time it takes before A can activate
	private final int timeOffsetB ; // Time it takes before B can activate
	
	private final int transportStartTicks;
	
	protected int connectionTime;
	
	private TransporterConnection(MinecraftServer server, UUID uuid, Transporter transporterA, Transporter transporterB, int connectionTime)
	{
		this.uuid = uuid;
		this.transporterA = transporterA;
		this.transporterB = transporterB;
		
		this.connectionTime = connectionTime;
		
		this.timeOffsetA = transporterA.getTimeOffset(server);
		this.timeOffsetB = transporterB.getTimeOffset(server);
		
		this.transportStartTicks = timeOffsetA > timeOffsetB ? timeOffsetA : timeOffsetB;
	}
	
	private TransporterConnection(MinecraftServer server, UUID uuid, Transporter transporterA, Transporter transporterB)
	{
		this(server, uuid, transporterA, transporterB, 0);
	}
	
	public enum Type
	{
		DIMENSIONAL, // Within one dimension
		SYSTEM_WIDE, // Within one system, across two dimensions
		INTERSTELLAR, // Across two solar systems, presumably through the Stargate
		INTERGALACTIC // Across two galaxies, presumably through the Stargate
	}
	
	@Nullable
	public static final TransporterConnection create(MinecraftServer server, Transporter transporterA, Transporter transporterB)
	{
		if(transporterA != null && transporterB != null)
		{
			UUID uuid = UUID.randomUUID();
			
			transporterA.connect(server, uuid);
			transporterB.connect(server, uuid);
			
			return new TransporterConnection(server, uuid, transporterA, transporterB);
		}
		
		return null;
	}
	
	
	
	private int absDifference()
	{
		return Math.abs(timeOffsetA - timeOffsetB);
	}
	
	public final void tick(MinecraftServer server)
	{
		int halfTransportTicks = transportStartTicks + RING_TICKS + TRANSPORT_TICKS;
		if(connectionTime >= 2 * halfTransportTicks || !isTransporterValid(server, transporterA) || !isTransporterValid(server, transporterB))
		{
			terminate(server);
			return;
		}
		
		if(connectionTime == halfTransportTicks)
			transport(server);
		
		updateTransporterTicks(server, transporterA, timeOffsetA);
		updateTransporterTicks(server, transporterB, timeOffsetB);
		
		increaseTicks();
	}
	
	private void updateTransporterTicks(MinecraftServer server, Transporter transporter, int timeOffset)
	{
		if(timeOffset == transportStartTicks)
			transporter.updateTicks(server, connectionTime);
		else
		{
			int ticks = connectionTime - (transportStartTicks - timeOffset);
			if(ticks >= 0)
				transporter.updateTicks(server, ticks);
		}
	}
	
	private void increaseTicks()
	{
		this.connectionTime++;
	}
	
	private void transport(MinecraftServer server)
	{
		// Get all potential travelers that can be transported before the transport starts, so that you don't end up transporting A -> B and then immediately B -> A
		List<Entity> travelersA = transporterA.entitiesToTransport(server);
		List<Entity> travelersB = transporterB.entitiesToTransport(server);
		// Attempt transporting all potential travelers
		transporterA.transportTravelers(server, this, transporterB, travelersA);
		transporterB.transportTravelers(server, this, transporterA, travelersB);
	}
	
	public final void terminate(MinecraftServer server)
	{
		if(this.transporterA != null)
			this.transporterA.reset(server);
		
		if(this.transporterB != null)
			this.transporterB.reset(server);
		
		TransporterNetwork.get(server).removeConnection(this.uuid);
	}
	
	public final boolean isTransporterValid(MinecraftServer server, Transporter transporter)
	{
		if(transporter == null)
		{
			StargateJourney.LOGGER.error("Transporter does not exist");
			return false;
		}
		
		return transporter.isConnected(server);
	}
	
	
	
	public UUID getID()
	{
		return this.uuid;
	}
	
	//============================================================================================
	//*************************************Saving and Loading*************************************
	//============================================================================================
	
	public CompoundTag serialize()
	{
		CompoundTag tag = new CompoundTag();
		
		tag.put(TRANSPORTER_A, serializeTransporter(this.transporterA));
		tag.put(TRANSPORTER_B, serializeTransporter(this.transporterB));
		
		tag.putInt(CONNECTION_TIME, this.connectionTime);
		
		return tag;
	}
	
	protected CompoundTag serializeTransporter(Transporter transporter)
	{
		CompoundTag tag = new CompoundTag();
		
		tag.putString(ID, transporter.getID().toString());
		
		return tag;
	}
	
	@Nullable
	public static TransporterConnection deserialize(MinecraftServer server, UUID uuid, CompoundTag tag)
	{
		Transporter transporterA = deserializeTransporter(server, tag.getCompound(TRANSPORTER_A));
		Transporter transporterB = deserializeTransporter(server, tag.getCompound(TRANSPORTER_B));
		int connectionTime = tag.getInt(CONNECTION_TIME);
		
		return new TransporterConnection(server, uuid, transporterA, transporterB, connectionTime);
	}
	
	private static Transporter deserializeTransporter(MinecraftServer server, CompoundTag transporterInfo)
	{
		return BlockEntityList.get(server).getTransporter(new TransporterID.Immutable(transporterInfo.getString(ID)));
	}
}
