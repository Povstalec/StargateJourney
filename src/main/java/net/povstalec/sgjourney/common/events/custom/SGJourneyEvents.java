package net.povstalec.sgjourney.common.events.custom;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

public class SGJourneyEvents
{
	// Stargate
	
	public static boolean onStargateDial(MinecraftServer server, Stargate stargate, Address dialedAddress, boolean doKawoosh)
    {
        return MinecraftForge.EVENT_BUS.post(new StargateEvent.Dial(server, stargate, dialedAddress, doKawoosh));
    }
	
	public static boolean onStargateConnect(MinecraftServer server, Stargate stargate, Stargate connectedStargate, StargateConnection.Type connectionType, Address.Type addressType, boolean doKawoosh)
    {
        return MinecraftForge.EVENT_BUS.post(new StargateEvent.Connect(server, stargate, connectedStargate, connectionType, addressType, doKawoosh));
    }
	
	public static boolean onWormholeTravel(MinecraftServer server, Stargate stargate, Stargate destinationStargate, Entity traveler, StargateInfo.WormholeTravel wormholeTravel)
    {
        return MinecraftForge.EVENT_BUS.post(new StargateEvent.WormholeTravel(server, stargate, destinationStargate, traveler, wormholeTravel));
    }
	
	// Stargate Connection
	
	public static boolean onStargateConnectionEstablished(MinecraftServer server, StargateConnection stargateConnection)
	{
		return MinecraftForge.EVENT_BUS.post(new StargateConnectionEvent.Establish(server, stargateConnection));
	}
	
	public static boolean onStargateConnectionTerminated(MinecraftServer server, StargateConnection stargateConnection, StargateInfo.Feedback feedback)
	{
		return MinecraftForge.EVENT_BUS.post(new StargateConnectionEvent.Terminate(server, stargateConnection, feedback));
	}
	
	// Transporter
	
	public static boolean onTransporterDial(MinecraftServer server, Transporter transporter, TransporterID transporterID)
	{
		return MinecraftForge.EVENT_BUS.post(new TransporterEvent.Dial(server, transporter, transporterID));
	}
	
	public static boolean onTransporterConnect(MinecraftServer server, Transporter transporter, Transporter connectedTransporter, TransporterConnection.Type connectionType)
	{
		return MinecraftForge.EVENT_BUS.post(new TransporterEvent.Connect(server, transporter, connectedTransporter, connectionType));
	}
	
	public static boolean onTransporterTransport(MinecraftServer server, Transporter transporter, Transporter destinationTransporter, Entity traveler)
	{
		return MinecraftForge.EVENT_BUS.post(new TransporterEvent.Transport(server, transporter, destinationTransporter, traveler));
	}
	
	// Transporter Connection
	
	public static boolean onTransporterConnectionEstablished(MinecraftServer server, TransporterConnection transporterConnection)
	{
		return MinecraftForge.EVENT_BUS.post(new TransporterConnectionEvent.Establish(server, transporterConnection));
	}
	
	public static boolean onTransporterConnectionTerminated(MinecraftServer server, TransporterConnection transporterConnection, TransporterInfo.Feedback feedback)
	{
		return MinecraftForge.EVENT_BUS.post(new TransporterConnectionEvent.Terminate(server, transporterConnection, feedback));
	}
}
