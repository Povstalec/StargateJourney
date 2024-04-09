package net.povstalec.sgjourney.common.events.custom;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.Event;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Connection;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class StargateEvent extends Event
{
	//TODO Add these events to the mod

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
	 * Fired when a Stargate attempts to dial a certain Address
	 * @author Povstalec
	 *
	 */
	public static class Dial extends StargateEvent
	{
		Address.Immutable dialedAddress;
		
		public Dial(MinecraftServer server, Stargate stargate, Address.Immutable dialedAddress)
		{
			super(server, stargate);
			this.dialedAddress = dialedAddress.copy();
		}
		
		public Address.Immutable getDialedAddress()
		{
			return dialedAddress;
		}
	}
	
	
	/**
	 * Fired when a Stargate attempts to form a connection with another Stargate
	 * @author Povstalec
	 *
	 */
	public static class Connect extends StargateEvent
	{
		private final Connection.Type connectionType;
		private final Stargate connectedStargate;
		
		public Connect(MinecraftServer server, Stargate stargate, Stargate connectedStargate, Connection.Type connectionType)
		{
			super(server, stargate);

			this.connectedStargate = connectedStargate;
			this.connectionType = connectionType;
		}
		
		public Stargate getConnectedStargate()
		{
			return this.connectedStargate;
		}
		
		public Connection.Type getConnectionType()
		{
			return this.connectionType;
		}
	}
}
