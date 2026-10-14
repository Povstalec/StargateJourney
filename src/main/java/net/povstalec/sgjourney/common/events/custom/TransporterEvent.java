package net.povstalec.sgjourney.common.events.custom;

import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.neoforged.bus.api.Event;
import net.neoforged.bus.api.ICancellableEvent;
import net.povstalec.sgjourney.common.sgjourney.TransporterConnection;
import net.povstalec.sgjourney.common.sgjourney.TransporterID;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

import javax.annotation.Nullable;

public class TransporterEvent extends Event
{
	private final MinecraftServer server;
	private final Transporter transporter;
	
	public TransporterEvent(MinecraftServer server, Transporter transporter)
	{
		this.server = server;
		this.transporter = transporter;
	}
	
	public Transporter getTransporter()
	{
		return this.transporter;
	}
	
	public MinecraftServer getServer()
	{
		return this.server;
	}
	
	
	
	/**
	 * Fired when a Transporter attempts to connect using a certain Transporter ID.
	 * This event is {@linkplain ICancellableEvent cancellable}.
	 * If this event is canceled, the Transporter does not connect to its target.
	 * @author Povstalec
	 *
	 */
	public static class DialID extends TransporterEvent implements ICancellableEvent
	{
		private final TransporterID transporterID;
		
		public DialID(MinecraftServer server, Transporter dialingTransporter, TransporterID transporterID)
		{
			super(server, dialingTransporter);
			this.transporterID = transporterID.clone();
		}
		
		public TransporterID getTransporterID()
		{
			return transporterID;
		}
	}
	
	
	
	/**
	 * Fired when a Transporter attempts to connect using coordinates.
	 * This event is {@linkplain ICancellableEvent cancellable}.
	 * If this event is canceled, the Transporter does not connect to its target.
	 * @author Povstalec
	 *
	 */
	public static class DialCoords extends TransporterEvent implements ICancellableEvent
	{
		private final Vec3i coords;
		
		public DialCoords(MinecraftServer server, Transporter dialingTransporter, Vec3i coords)
		{
			super(server, dialingTransporter);
			this.coords = coords;
		}
		
		public Vec3i getCoords()
		{
			return coords;
		}
	}
	
	
	
	/**
	 * Fired when a Transporter attempts to form a connection with another Transporter.
	 * This event is {@linkplain ICancellableEvent cancellable}.
	 * If this event is canceled, the does not form.
	 * !!!NOTE That it does NOT reset the Transporter or actually change its feedback when canceled!!!
	 * @author Povstalec
	 *
	 */
	public static class Connect extends TransporterEvent implements ICancellableEvent
	{
		@Nullable
		private final TransporterConnection.Type connectionType;
		private final Transporter connectedTransporter;
		
		public Connect(MinecraftServer server, Transporter dialingTransporter, Transporter connectedTransporter, @Nullable TransporterConnection.Type connectionType)
		{
			super(server, dialingTransporter);

			this.connectedTransporter = connectedTransporter;
			this.connectionType = connectionType;
		}
		
		public Transporter getConnectedTransporter()
		{
			return this.connectedTransporter;
		}
		
		/**
		 * @return Connection type of the potential connection, or null if the connection is definitely invalid
		 * (like when a Transporter attempts to connect to a Transporter in another Address Region without relaying the connection through a Stargate)
		 */
		@Nullable
		public TransporterConnection.Type getConnectionType()
		{
			return this.connectionType;
		}
	}
	
	
	
	/**
	 * Fired when the Transporter starts transporting everything inside its area of influence.
	 * This event is {@linkplain ICancellableEvent cancellable}.
	 * If this event is canceled, the Transporter does not transport any Entities.
	 * @author Povstalec
	 *
	 */
	public static class TransporterTransport extends TransporterEvent implements ICancellableEvent
	{
		private final Transporter destinationTransporter;
		
		public TransporterTransport(MinecraftServer server, Transporter initialTransporter, Transporter destinationTransporter)
		{
			super(server, initialTransporter);
			
			this.destinationTransporter = destinationTransporter;
		}
		
		public Transporter getDestinationTransporter()
		{
			return this.destinationTransporter;
		}
	}
	
	
	
	/**
	 * Fired when an Entity gets transported by the Transporter, before the receiving Transporter receives the traveller.
	 * This event is {@linkplain ICancellableEvent cancellable}.
	 * If this event is canceled, the Transporter does not transport the traveler Entity.
	 * @author Povstalec
	 *
	 */
	public static class TravellerTransport extends TransporterEvent implements ICancellableEvent
	{
		private final Transporter destinationTransporter;
		private final Entity traveler;

		public TravellerTransport(MinecraftServer server, Transporter sendingTransporter, Transporter receivingTransporter, Entity traveler)
		{
			super(server, sendingTransporter);
			
			this.destinationTransporter = receivingTransporter;
			this.traveler = traveler;
		}
		
		public Transporter getDestinationTransporter()
		{
			return this.destinationTransporter;
		}
		
		public Entity getTraveler()
		{
			return this.traveler;
		}
	}
	
	
	
	/**
	 * Fired when an Entity is being teleported during transport.
	 * This event is {@linkplain ICancellableEvent cancellable}}.
	 * If this event is canceled, the Transporter does not transport the traveler Entity.
	 * <br>
	 * This event is intended to be used in cases, where custom teleportation code is required to transport some traveler Entity.
	 * @author Povstalec
	 *
	 */
	public static class EntityTransport extends TransporterEvent implements ICancellableEvent
	{
		private final ServerLevel destinationLevel;
		private final TransporterConnection connection;
		private final Transporter destinationTransporter;
		private Entity traveler;
		private final Vec3 destinationPosition;
		private final Vec3 destinationMomentum;
		private final Vec3 destinationLookAngle;
		
		public EntityTransport(ServerLevel destinationLevel, TransporterConnection connection, Transporter sendingTransporter, Transporter receivingTransporter, Entity traveler,
		                       Vec3 destinationPosition, Vec3 destinationMomentum, Vec3 destinationLookAngle)
		{
			super(destinationLevel.getServer(), sendingTransporter);
			
			this.destinationLevel = destinationLevel;
			this.connection = connection;
			this.destinationTransporter = receivingTransporter;
			this.traveler = traveler;
			this.destinationPosition = destinationPosition;
			this.destinationMomentum = destinationMomentum;
			this.destinationLookAngle = destinationLookAngle;
		}
		
		public ServerLevel getDestinationLevel()
		{
			return destinationLevel;
		}
		
		public TransporterConnection getConnection()
		{
			return connection;
		}
		
		public Transporter getDestinationTransporter()
		{
			return this.destinationTransporter;
		}
		
		public void setTraveler(Entity traveler)
		{
			this.traveler = traveler;
		}
		
		public Entity getTraveler()
		{
			return this.traveler;
		}
		
		public Vec3 getDestinationPosition()
		{
			return destinationPosition;
		}
		
		public Vec3 getDestinationMomentum()
		{
			return destinationMomentum;
		}
		
		public Vec3 getDestinationLookAngle()
		{
			return destinationLookAngle;
		}
	}
}
