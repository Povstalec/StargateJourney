package net.povstalec.sgjourney.common.events.custom;

import net.minecraft.server.MinecraftServer;
import net.neoforged.bus.api.Event;
import net.povstalec.sgjourney.common.sgjourney.TransporterConnection;
import net.povstalec.sgjourney.common.sgjourney.TransporterInfo;

public class TransporterConnectionEvent extends Event
{
	private final MinecraftServer server;
	private final TransporterConnection connection;
	
	public TransporterConnectionEvent(MinecraftServer server, TransporterConnection connection)
	{
		this.server = server;
		this.connection = connection;
	}
	
	public TransporterConnection getTransporterConnection()
	{
		return this.connection;
	}
	
	public MinecraftServer getServer()
	{
		return this.server;
	}
	
	/**
	 * Fired right after a Transporter Connection is successfully established (not cancelable)
	 * @author Povstalec
	 *
	 */
	public static class Establish extends TransporterConnectionEvent
	{
		public Establish(MinecraftServer server, TransporterConnection connection)
		{
			super(server, connection);
		}
	}
	
	/**
	 * Fired right before a Transporter Connection is successfully terminated (not cancelable)
	 * @author Povstalec
	 *
	 */
	public static class Terminate extends TransporterConnectionEvent
	{
		private final TransporterInfo.Feedback feedback;
		
		public Terminate(MinecraftServer server, TransporterConnection connection, TransporterInfo.Feedback feedback)
		{
			super(server, connection);
			
			this.feedback = feedback;
		}
		
		public TransporterInfo.Feedback getFeedback()
		{
			return this.feedback;
		}
	}
}
