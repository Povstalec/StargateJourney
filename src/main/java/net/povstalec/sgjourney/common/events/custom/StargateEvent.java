package net.povstalec.sgjourney.common.events.custom;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.povstalec.sgjourney.common.sgjourney.Address;
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
	 * Fired when a Stargate attempts to dial a certain Address (cancelable)
	 * @author Povstalec
	 *
	 */
	@Cancelable
	public static class Dial extends StargateEvent
	{
		private final Address.Immutable dialedAddress;
		private final Address.Immutable dialingAddress;
		private final boolean doKawoosh;
		
		public Dial(MinecraftServer server, Stargate stargate, Address.Immutable dialedAddress, Address.Immutable dialingAddress, boolean doKawoosh)
		{
			super(server, stargate);
			this.dialedAddress = dialedAddress.copy();
			this.dialingAddress = dialingAddress.copy();
			this.doKawoosh = doKawoosh;
		}
		
		public Address.Immutable getDialedAddress()
		{
			return dialedAddress;
		}
		
		public Address.Immutable getDialingAddress()
		{
			return dialingAddress;
		}
		
		public boolean doKawoosh()
		{
			return this.doKawoosh;
		}
	}
	
	
	
	/**
	 * Fired when a Stargate attempts to form a connection with another Stargate (cancelable)
	 * !!!NOTE That it does NOT reset the Stargate or actually change its feedback when cancelled!!!
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
		
		public Connect(MinecraftServer server, Stargate stargate, Stargate connectedStargate, StargateConnection.Type connectionType, Address.Type addressType, boolean doKawoosh)
		{
			super(server, stargate);

			this.connectedStargate = connectedStargate;
			this.connectionType = connectionType;
			this.addressType = addressType;
			this.doKawoosh = doKawoosh;
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
	}
	
	
	
	/**
	 * Fired when a an Entity goes through the Wormhole (cancelable)
	 * @author Povstalec
	 *
	 */
	@Cancelable
	public static class WormholeTravel extends StargateEvent
	{
		private final Stargate connectedStargate;
		private final StargateConnection.Type connectionType;
		private final Entity traveler;
		private final StargateInfo.WormholeTravel wormholeTravel;

		public WormholeTravel(MinecraftServer server, Stargate stargate, Stargate connectedStargate,
							  StargateConnection.Type connectionType, Entity traveler, StargateInfo.WormholeTravel wormholeTravel)
		{
			super(server, stargate);
			
			this.connectedStargate = connectedStargate;
			this.connectionType = connectionType;
			this.traveler = traveler;
			this.wormholeTravel = wormholeTravel;
		}
		
		public Stargate getConnectedStargate()
		{
			return this.connectedStargate;
		}
		
		public StargateConnection.Type getConnectionType()
		{
			return this.connectionType;
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
}
