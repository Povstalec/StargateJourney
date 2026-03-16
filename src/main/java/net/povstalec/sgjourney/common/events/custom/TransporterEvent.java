package net.povstalec.sgjourney.common.events.custom;

import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.eventbus.api.Cancelable;
import net.minecraftforge.eventbus.api.Event;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

@Cancelable
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
	 * Fired when a Transporter attempts to connect using a certain Transporter ID (cancelable)
	 * @author Povstalec
	 *
	 */
	@Cancelable
	public static class DialID extends TransporterEvent
	{
		private final TransporterID transporterID;
		
		public DialID(MinecraftServer server, Transporter transporter, TransporterID transporterID)
		{
			super(server, transporter);
			this.transporterID = transporterID.clone();
		}
		
		public TransporterID getTransporterID()
		{
			return transporterID;
		}
	}
	
	
	
	/**
	 * Fired when a Transporter attempts to connect using coordinates (cancelable)
	 * @author Povstalec
	 *
	 */
	@Cancelable
	public static class DialCoords extends TransporterEvent
	{
		private final Vec3i coords;
		
		public DialCoords(MinecraftServer server, Transporter transporter, Vec3i coords)
		{
			super(server, transporter);
			this.coords = coords;
		}
		
		public Vec3i getCoords()
		{
			return coords;
		}
	}
	
	
	
	/**
	 * Fired when a Transporter attempts to form a connection with another Transporter (cancelable)
	 * !!!NOTE That it does NOT reset the Transporter or actually change its feedback when canceled!!!
	 * @author Povstalec
	 *
	 */
	@Cancelable
	public static class Connect extends TransporterEvent
	{
		private final TransporterConnection.Type connectionType;
		private final Transporter connectedTransporter;
		
		public Connect(MinecraftServer server, Transporter transporter, Transporter connectedTransporter, TransporterConnection.Type connectionType)
		{
			super(server, transporter);

			this.connectedTransporter = connectedTransporter;
			this.connectionType = connectionType;
		}
		
		public Transporter getConnectedTransporter()
		{
			return this.connectedTransporter;
		}
		
		public TransporterConnection.Type getConnectionType()
		{
			return this.connectionType;
		}
	}
	
	
	
	/**
	 * Fired when an Entity gets transported by the Transporter (cancelable)
	 * @author Povstalec
	 *
	 */
	@Cancelable
	public static class Transport extends TransporterEvent
	{
		private final Transporter destinationTransporter;
		private final Entity traveler;

		public Transport(MinecraftServer server, Transporter transporter, Transporter destinationTransporter, Entity traveler)
		{
			super(server, transporter);
			
			this.destinationTransporter = destinationTransporter;
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
}
