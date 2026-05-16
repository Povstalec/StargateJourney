package net.povstalec.sgjourney.common.sgjourney;

import java.util.List;
import java.util.UUID;

import javax.annotation.Nullable;

import net.minecraft.core.Vec3i;
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
import net.povstalec.sgjourney.common.misc.Conversion;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

public class TransporterConnection
{
	public static final String EVENT_DISCONNECTED = "stargate_disconnected";
	
	public static final String ID = "id";
	public static final String TRANSPORTER_A = "transporter_a";
	public static final String TRANSPORTER_B = "transporter_b";
	public static final String CONNECTION_TIME = "connection_time";
	public static final String CONNECTION_TYPE = "connection_type";
	public static final String RELAY_ID = "relay_id";
	
	private final UUID uuid;
	protected final TransporterConnection.Type connectionType;
	private final Transporter transporterA;
	private final Transporter transporterB;
	
	private final int timeOffsetA; // Time it takes before A can activate
	private final int timeOffsetB ; // Time it takes before B can activate
	
	private final int transportStartTicks;
	
	protected int connectionTime;
	
	@Nullable
	protected UUID relayID; // UUID of the Stargate connection that is used to relay this connection
	
	private TransporterConnection(MinecraftServer server, UUID uuid, TransporterConnection.Type connectionType, Transporter transporterA, Transporter transporterB, int connectionTime, @Nullable UUID relayID)
	{
		this.uuid = uuid;
		this.connectionType = connectionType;
		this.transporterA = transporterA;
		this.transporterB = transporterB;
		
		this.connectionTime = connectionTime;
		
		this.timeOffsetA = transporterA.getTimeUntilTransport(server);
		this.timeOffsetB = transporterB.getTimeUntilTransport(server);
		
		this.transportStartTicks = Math.max(timeOffsetA, timeOffsetB);
		
		this.relayID = relayID; //TODO
	}
	
	private TransporterConnection(MinecraftServer server, UUID uuid, TransporterConnection.Type connectionType, Transporter transporterA, Transporter transporterB)
	{
		this(server, uuid, connectionType, transporterA, transporterB, 0, null);
	}
	
	public enum Type
	{
		DIMENSIONAL("dimensional", false), // Within one dimension
		SYSTEM_WIDE("system_wide", false), // Within one system, across two dimensions
		
		RELAYED_DIMENSIONAL("relayed_dimensional", true), // Within one dimension, relayed through a Stargate
		RELAYED_SYSTEM_WIDE("relayed_system_wide", true), // Within one system, relayed through a Stargate
		RELAYED_INTERSTELLAR("relayed_interstellar", true), // Across two solar systems, relayed through a Stargate
		RELAYED_INTERGALACTIC("relayed_intergalactic", true); // Across two galaxies, relayed through a Stargate
		
		private final String name;
		public final boolean isRelayed;
		
		Type(String name, boolean isRelayed)
		{
			this.name = name;
			this.isRelayed = isRelayed;
		}
		
		public String getSerializedName()
		{
			return this.name;
		}
		
		public static TransporterConnection.Type fromString(String name)
		{
			return switch(name)
			{
				case "dimensional" -> DIMENSIONAL;
				case "system_wide" -> SYSTEM_WIDE;
				case "relayed_dimensional" -> RELAYED_DIMENSIONAL;
				case "relayed_system_wide" -> RELAYED_SYSTEM_WIDE;
				case "relayed_interstellar" -> RELAYED_INTERSTELLAR;
				case "relayed_intergalactic" -> RELAYED_INTERGALACTIC;
				default -> null;
			};
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
	public static TransporterConnection create(MinecraftServer server, TransporterConnection.Type connectionType, Transporter transporterA, Transporter transporterB)
	{
		if(transporterA != null && transporterB != null)
		{
			UUID uuid = UUID.randomUUID();
			
			transporterA.connect(server, uuid);
			transporterB.connect(server, uuid);
			
			return new TransporterConnection(server, uuid, connectionType, transporterA, transporterB);
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
	
	//============================================================================================
	//************************************Getters and Setters*************************************
	//============================================================================================
	
	public UUID getID()
	{
		return this.uuid;
	}
	
	public TransporterConnection.Type getConnectionType()
	{
		return this.connectionType;
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
		TransporterConnection.Type connectionType = TransporterConnection.Type.valueOf(tag.getString(CONNECTION_TYPE));
		Transporter transporterA = deserializeTransporter(server, tag.getCompound(TRANSPORTER_A));
		Transporter transporterB = deserializeTransporter(server, tag.getCompound(TRANSPORTER_B));
		int connectionTime = tag.getInt(CONNECTION_TIME);
		
		try
		{
			if(tag.contains(RELAY_ID, Tag.TAG_STRING))
				return new TransporterConnection(server, uuid, connectionType, transporterA, transporterB, connectionTime, UUID.fromString(tag.getString(RELAY_ID)));
		}
		catch(IllegalArgumentException e)
		{
			StargateJourney.LOGGER.error(e.toString());
		}
		
		return new TransporterConnection(server, uuid, connectionType, transporterA, transporterB, connectionTime, null);
	}
	
	private static Transporter deserializeTransporter(MinecraftServer server, CompoundTag transporterInfo)
	{
		return BlockEntityList.get(server).getTransporter(new TransporterID.Immutable(transporterInfo.getString(ID)));
	}
	
	
	
	public static class IDResult
	{
		public static final String FEEDBACK = "feedback";
		public static final String TRANSPORTER_ID = TransporterID.TRANSPORTER_ID;
		
		protected TransporterID transporterID;
		protected TransporterInfo.Feedback feedback;
		
		public IDResult() {}
		
		public IDResult(TransporterID transporterID, TransporterInfo.Feedback feedback)
		{
			this.transporterID = transporterID;
			this.feedback = feedback;
		}
		
		public TransporterID transporterID()
		{
			return transporterID;
		}
		
		public TransporterInfo.Feedback feedback()
		{
			return feedback;
		}
		
		public CompoundTag save()
		{
			CompoundTag tag = new CompoundTag();
			
			transporterID.saveToCompoundTag(tag, TRANSPORTER_ID);
			tag.putInt(FEEDBACK, feedback.ordinal());
			
			return tag;
		}
		
		public void load(CompoundTag tag)
		{
			transporterID = new TransporterID.Immutable(tag.getIntArray(TRANSPORTER_ID));
			feedback = TransporterInfo.Feedback.fromOrdinal(tag.getInt(FEEDBACK));
		}
	}
	
	public static class CoordsResult
	{
		public static final String FEEDBACK = "feedback";
		public static final String COORDS = "coords";
		
		protected Vec3i coords;
		protected TransporterInfo.Feedback feedback;
		
		public CoordsResult() {}
		
		public CoordsResult(Vec3i coords, TransporterInfo.Feedback feedback)
		{
			this.coords = coords;
			this.feedback = feedback;
		}
		
		public Vec3i coords()
		{
			return coords;
		}
		
		public TransporterInfo.Feedback feedback()
		{
			return feedback;
		}
		
		public CompoundTag save()
		{
			CompoundTag tag = new CompoundTag();
			
			tag.putIntArray(COORDS, Conversion.vecToIntArray(coords));
			tag.putInt(FEEDBACK, feedback.ordinal());
			
			return tag;
		}
		
		public void load(CompoundTag tag)
		{
			coords = Conversion.intArrayToVec(tag.getIntArray(COORDS));
			feedback = TransporterInfo.Feedback.fromOrdinal(tag.getInt(FEEDBACK));
		}
	}
}
