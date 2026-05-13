package net.povstalec.sgjourney.common.sgjourney;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.Tag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.BlockEntityList;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.events.custom.SGJourneyEvents;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

public class TransporterConnection
{
	public static final String EVENT_DISCONNECTED = "stargate_disconnected";
	
	public static final String ID = "id";
	public static final String TRANSPORTER_A = "transporter_a";
	public static final String TRANSPORTER_B = "transporter_b";
	public static final String CONNECTION_TIME = "connection_time";
	public static final String RELAY_ID = "relay_id";
	
	private final UUID uuid;
	private final Transporter transporterA;
	private final Transporter transporterB;
	
	private final int timeOffsetA; // Time it takes before A can activate
	private final int timeOffsetB ; // Time it takes before B can activate
	
	private final int transportStartTicks;
	
	protected int connectionTime;
	
	@Nullable
	protected UUID relayID; // UUID of the Stargate connection that is used to relay this connection
	
	private TransporterConnection(MinecraftServer server, UUID uuid, Transporter transporterA, Transporter transporterB, int connectionTime, @Nullable UUID relayID)
	{
		this.uuid = uuid;
		this.transporterA = transporterA;
		this.transporterB = transporterB;
		
		this.connectionTime = connectionTime;
		
		this.timeOffsetA = transporterA.getTimeUntilTransport(server);
		this.timeOffsetB = transporterB.getTimeUntilTransport(server);
		
		this.transportStartTicks = Math.max(timeOffsetA, timeOffsetB);
		
		this.relayID = relayID; //TODO
	}
	
	private TransporterConnection(MinecraftServer server, UUID uuid, Transporter transporterA, Transporter transporterB)
	{
		this(server, uuid, transporterA, transporterB, 0, null);
	}
	
	public enum Type
	{
		DIMENSIONAL(false), // Within one dimension
		SYSTEM_WIDE(false), // Within one system, across two dimensions
		
		RELAYED_DIMENSIONAL(true), // Within one dimension, relayed through a Stargate
		RELAYED_SYSTEM_WIDE(true), // Within one system, relayed through a Stargate
		RELAYED_INTERSTELLAR(true), // Across two solar systems, relayed through a Stargate
		RELAYED_INTERGALACTIC(true); // Across two galaxies, relayed through a Stargate
		
		public final boolean isRelayed;
		
		Type(boolean isRelayed)
		{
			this.isRelayed = isRelayed;
		}
	}
	
	public static TransporterConnection.Type getType(MinecraftServer server, Transporter transporterA, Transporter transporterB)
	{
		AddressRegion regionA = transporterA.getAddressRegion(server);
		AddressRegion regionB = transporterB.getAddressRegion(server);
		
		if(regionA != null && regionB != null)
		{
			if(regionA.equals(regionB))
			{
				ResourceKey<Level> dimensionA = transporterA.getDimension();
				ResourceKey<Level> dimensionB = transporterB.getDimension();
				
				if(dimensionA != null && dimensionA.equals(dimensionB))
					return Type.DIMENSIONAL;
				else
					return Type.SYSTEM_WIDE;
			}
			
			if(regionA.findCommonGalaxy(server, regionB) != null)
				return Type.RELAYED_INTERSTELLAR;
		}
		
		return Type.RELAYED_INTERGALACTIC;
	}
	
	@Nullable
	public static TransporterConnection create(MinecraftServer server, Transporter transporterA, Transporter transporterB)
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
	
	
	
	public void tick(MinecraftServer server)
	{
		if(!isTransporterValid(server, transporterA) || !isTransporterValid(server, transporterB))
		{
			terminate(server, TransporterInfo.Feedback.COULD_NOT_REACH_TARGET_TRANSPORTER);
			return;
		}
		
		if(connectionTime == transportStartTicks)
			transport(server);
		
		updateTransporterTicks(server, transporterA, timeOffsetA);
		updateTransporterTicks(server, transporterB, timeOffsetB);
		
		increaseTicks();
		
		if(connectionTime >= 2 * transportStartTicks)
			terminate(server, TransporterInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT);
	}
	
	private void updateTransporterTicks(MinecraftServer server, Transporter transporter, int transporterTimeOffset)
	{
		if(transporterTimeOffset == transportStartTicks)
			transporter.updateTicks(server, transporterTimeOffset, connectionTime);
		else
		{
			int ticks = connectionTime - (transportStartTicks - transporterTimeOffset);
			transporter.updateTicks(server, transporterTimeOffset, Math.max(ticks, -1));
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
	
	public void terminate(MinecraftServer server, TransporterInfo.Feedback feedback)
	{
		SGJourneyEvents.onTransporterConnectionTerminated(server, this, feedback);
		
		if(this.transporterA != null)
		{
			this.transporterA.updateInterfaceBlocks(server, null, EVENT_DISCONNECTED, feedback.getCode(), true); // true: Initiated the Transport
			this.transporterA.resetTransporter(server, feedback);
		}
		
		if(this.transporterB != null)
		{
			this.transporterB.updateInterfaceBlocks(server, null, EVENT_DISCONNECTED, feedback.getCode(), false); // false: Did not initiate the Transport
			this.transporterB.resetTransporter(server, feedback);
		}
		
		TransporterNetwork.get(server).removeConnection(this.uuid);
	}
	
	public boolean isTransporterValid(MinecraftServer server, Transporter transporter)
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
	
	public UUID getRelayID()
	{
		return this.relayID;
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
		
		try
		{
			if(tag.contains(RELAY_ID, Tag.TAG_STRING))
				return new TransporterConnection(server, uuid, transporterA, transporterB, connectionTime, UUID.fromString(tag.getString(RELAY_ID)));
		}
		catch(IllegalArgumentException e)
		{
			StargateJourney.LOGGER.error(e.toString());
		}
		
		return new TransporterConnection(server, uuid, transporterA, transporterB, connectionTime, null);
	}
	
	private static Transporter deserializeTransporter(MinecraftServer server, CompoundTag transporterInfo)
	{
		return BlockEntityList.get(server).getTransporter(new TransporterID.Immutable(transporterInfo.getString(ID)));
	}
}
