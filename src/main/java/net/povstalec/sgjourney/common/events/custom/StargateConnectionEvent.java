package net.povstalec.sgjourney.common.events.custom;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.eventbus.api.Event;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;

public class StargateConnectionEvent extends Event
{
	private final MinecraftServer server;
	private final StargateConnection connection;
	
	public StargateConnectionEvent(MinecraftServer server, StargateConnection connection)
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
	 * Fired right after a Stargate Connection is successfully established (not cancelable)
	 * @author Povstalec
	 *
	 */
	public static class Establish extends StargateConnectionEvent
	{
		public Establish(MinecraftServer server, StargateConnection connection)
		{
			super(server, connection);
		}
	}
	
	/**
	 * Fired right before a Stargate Connection is successfully terminated (not cancelable)
	 * @author Povstalec
	 *
	 */
	public static class Terminate extends StargateConnectionEvent
	{
		private final StargateInfo.Feedback feedback;
		
		public Terminate(MinecraftServer server, StargateConnection connection, StargateInfo.Feedback feedback)
		{
			super(server, connection);
			
			this.feedback = feedback;
		}
		
		public StargateInfo.Feedback getFeedback()
		{
			return this.feedback;
		}
	}
}
