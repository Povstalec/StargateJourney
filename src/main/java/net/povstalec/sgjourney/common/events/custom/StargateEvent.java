package net.povstalec.sgjourney.common.events.custom;

import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.Dialing;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

@Cancelable
public class StargateEvent extends Event
{
	private final MinecraftServer server;
	private final Stargate stargate;
	
	public StargateEvent(MinecraftServer server, Stargate stargate)
	{
		this.server = server;
		this.stargate = stargate;
	}
	
	public Stargate getStargate()
	{
		return this.stargate;
	}
	
	public MinecraftServer getServer()
	{
		return this.server;
	}
	
	
	
	/**
	 * Fired when a Stargate attempts to dial a certain Address (cancelable).
	 * This event is {@link Cancelable}.
	 * If this event is canceled, the Stargate does not dial.
	 * !!!NOTE That it does NOT reset the Stargate or actually change its feedback when canceled!!!
	 * @author Povstalec
	 *
	 */
	@Cancelable
	public static class Dial extends StargateEvent
	{
		private final Address dialedAddress;
		private final boolean doKawoosh;
		private final Dialing.Action action;
		
		public Dial(MinecraftServer server, Stargate dialingStargate, Address dialedAddress, boolean doKawoosh, Dialing.Action action)
		{
			super(server, dialingStargate);
			this.dialedAddress = dialedAddress.clone();
			this.doKawoosh = doKawoosh;
			this.action = action;
		}
		
		public Address getDialedAddress()
		{
			return dialedAddress;
		}
		
		public boolean doKawoosh()
		{
			return this.doKawoosh;
		}
		
		public Dialing.Action action()
		{
			return this.action;
		}
	}
	
	
	
	/**
	 * Fired when a Stargate attempts to form a connection with another Stargate.
	 * This event is {@link Cancelable}.
	 * If this event is canceled, the does not form.
	 * !!!NOTE That it does NOT reset the Stargate or actually change its feedback when canceled!!!
	 * @author Povstalec
	 *
	 */
	@Cancelable
	public static class Connect extends StargateEvent
	{
		private final StargateConnection.Type connectionType;
		private final Stargate connectedStargate;
		private final Address.Type addressType;
		private final boolean doKawoosh;
		private final Dialing.Action action;
		
		public Connect(MinecraftServer server, Stargate dialingStargate, Stargate connectedStargate, StargateConnection.Type connectionType, Address.Type addressType, boolean doKawoosh, Dialing.Action action)
		{
			super(server, dialingStargate);

			this.connectedStargate = connectedStargate;
			this.connectionType = connectionType;
			this.addressType = addressType;
			this.doKawoosh = doKawoosh;
			this.action = action;
		}
		
		public Stargate getConnectedStargate()
		{
			return this.connectedStargate;
		}
		
		public StargateConnection.Type getConnectionType()
		{
			return this.connectionType;
		}
		
		public Address.Type getAddressType()
		{
			return this.addressType;
		}
		
		public boolean doKawoosh()
		{
			return this.doKawoosh;
		}
		
		public Dialing.Action action()
		{
			return this.action;
		}
	}
	
	
	
	/**
	 * Fired when an Entity goes through the Wormhole, before the destination Stargate receives the traveller.
	 * This event is {@link Cancelable}.
	 * If this event is canceled, the Stargate does not transport the traveler Entity.
	 * @author Povstalec
	 *
	 */
	@Cancelable
	public static class WormholeTravel extends StargateEvent
	{
		private final StargateConnection connection;
		private final Stargate destinationStargate;
		private final Entity traveler;
		private final StargateInfo.WormholeTravel wormholeTravel;

		public WormholeTravel(MinecraftServer server, StargateConnection connection, Stargate initialStargate, Stargate destinationStargate, Entity traveler, StargateInfo.WormholeTravel wormholeTravel)
		{
			super(server, initialStargate);
			
			this.connection = connection;
			this.destinationStargate = destinationStargate;
			this.traveler = traveler;
			this.wormholeTravel = wormholeTravel;
		}
		
		public StargateConnection getConnection()
		{
			return connection;
		}
		
		public Stargate getDestinationStargate()
		{
			return this.destinationStargate;
		}
		
		public Entity getTraveler()
		{
			return this.traveler;
		}
		
		public StargateInfo.WormholeTravel getWormholeTravel()
		{
			return this.wormholeTravel;
		}
	}
	
	
	
	/**
	 * Fired when an Entity is being teleported after going through the wormhole.
	 * This event is {@link Cancelable}.
	 * If this event is canceled, the Stargate does not transport the traveler Entity.
	 * <br>
	 * This event is intended to be used in cases, where custom teleportation code is required to transport some traveler Entity.
	 * @author Povstalec
	 *
	 */
	@Cancelable
	public static class EntityTransport extends StargateEvent
	{
		private final ServerLevel destinationLevel;
		private final StargateConnection connection;
		private final Stargate destinationStargate;
		private Entity traveler;
		private final Vec3 destinationPosition;
		private final Vec3 destinationMomentum;
		private final Vec3 destinationLookAngle;
		
		public EntityTransport(ServerLevel destinationLevel, StargateConnection connection, Stargate initialStargate, Stargate destinationStargate, Entity traveler,
		                       Vec3 destinationPosition, Vec3 destinationMomentum, Vec3 destinationLookAngle)
		{
			super(destinationLevel.getServer(), initialStargate);
			
			this.destinationLevel = destinationLevel;
			this.connection = connection;
			this.destinationStargate = destinationStargate;
			this.traveler = traveler;
			this.destinationPosition = destinationPosition;
			this.destinationMomentum = destinationMomentum;
			this.destinationLookAngle = destinationLookAngle;
		}
		
		public ServerLevel getDestinationLevel()
		{
			return destinationLevel;
		}
		
		public StargateConnection getConnection()
		{
			return connection;
		}
		
		public Stargate getDestinationStargate()
		{
			return this.destinationStargate;
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
