package net.povstalec.sgjourney.common.events.custom;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.Event;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;

public class ConnectionEvent extends Event
{
	private final MinecraftServer server;
	private final StargateConnection connection;
	
	public ConnectionEvent(MinecraftServer server, StargateConnection connection)
	{
		this.server = server;
		this.connection = connection;
	}
	
	public StargateConnection getStargateConnection()
	{
		return this.connection;
	}
	
	public MinecraftServer getServer()
	{
		return this.server;
	}
	
	/**
	 * Fired right after a Connection is successfully established (not cancelable)
	 * @author Povstalec
	 *
	 */
	public static class Establish extends ConnectionEvent
	{
		public Establish(MinecraftServer server, StargateConnection connection)
		{
			super(server, connection);
		}
	}
	
	/**
	 * Fired right before a Connection is successfully terminated (not cancelable)
	 * @author Povstalec
	 *
	 */
	public static class Terminate extends ConnectionEvent
	{
		public Terminate(MinecraftServer server, StargateConnection connection)
		{
			super(server, connection);
		}
	}
}
