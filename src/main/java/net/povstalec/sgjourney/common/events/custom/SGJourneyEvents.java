package net.povstalec.sgjourney.common.events.custom;

import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

import javax.annotation.Nullable;

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
	
	public static void onStargateConnectionEstablished(MinecraftServer server, StargateConnection stargateConnection)
	{
		MinecraftForge.EVENT_BUS.post(new StargateConnectionEvent.Establish(server, stargateConnection));
	}
	
	public static void onStargateConnectionTerminated(MinecraftServer server, StargateConnection stargateConnection, StargateInfo.Feedback feedback)
	{
		MinecraftForge.EVENT_BUS.post(new StargateConnectionEvent.Terminate(server, stargateConnection, feedback));
	}
	
	// Transporter
	
	public static boolean onTransporterDialID(MinecraftServer server, Transporter transporter, TransporterID transporterID)
	{
		return MinecraftForge.EVENT_BUS.post(new TransporterEvent.DialID(server, transporter, transporterID));
	}
	
	public static boolean onTransporterDialCoords(MinecraftServer server, Transporter transporter, Vec3i coords)
	{
		return MinecraftForge.EVENT_BUS.post(new TransporterEvent.DialCoords(server, transporter, coords));
	}
	
	public static boolean onTransporterConnect(MinecraftServer server, Transporter transporter, Transporter connectedTransporter, @Nullable TransporterConnection.Type connectionType)
	{
		return MinecraftForge.EVENT_BUS.post(new TransporterEvent.Connect(server, transporter, connectedTransporter, connectionType));
	}
	
	public static boolean onTransporterTransport(MinecraftServer server, Transporter transporter, Transporter destinationTransporter, Entity traveler)
	{
		return MinecraftForge.EVENT_BUS.post(new TransporterEvent.Transport(server, transporter, destinationTransporter, traveler));
	}
	
	// Transporter Connection
	
	public static void onTransporterConnectionEstablished(MinecraftServer server, TransporterConnection transporterConnection)
	{
		MinecraftForge.EVENT_BUS.post(new TransporterConnectionEvent.Establish(server, transporterConnection));
	}
	
	public static void onTransporterConnectionTerminated(MinecraftServer server, TransporterConnection transporterConnection, TransporterInfo.Feedback feedback)
	{
		MinecraftForge.EVENT_BUS.post(new TransporterConnectionEvent.Terminate(server, transporterConnection, feedback));
	}
}
