package net.povstalec.sgjourney.common.events.custom;

import net.minecraft.core.Vec3i;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.*;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

import javax.annotation.Nullable;

public class SGJourneyEvents
{
	// Stargate
	
	public static boolean onStargateDial(MinecraftServer server, Stargate stargate, Address dialedAddress, boolean doKawoosh)
    {
		return NeoForge.EVENT_BUS.post(new StargateEvent.Dial(server, stargate, dialedAddress, doKawoosh)).isCanceled();
    }
	
	public static boolean onStargateConnect(MinecraftServer server, Stargate stargate, Stargate connectedStargate, StargateConnection.Type connectionType, Address.Type addressType, boolean doKawoosh)
    {
        return NeoForge.EVENT_BUS.post(new StargateEvent.Connect(server, stargate, connectedStargate, connectionType, addressType, doKawoosh)).isCanceled();
    }
	
	public static boolean onWormholeTravel(MinecraftServer server, Stargate stargate, Stargate destinationStargate, Entity traveler, StargateInfo.WormholeTravel wormholeTravel)
    {
        return NeoForge.EVENT_BUS.post(new StargateEvent.WormholeTravel(server, stargate, destinationStargate, traveler, wormholeTravel)).isCanceled();
    }
	
	// Stargate Connection
	
	public static void onStargateConnectionEstablished(MinecraftServer server, StargateConnection stargateConnection)
	{
		NeoForge.EVENT_BUS.post(new StargateConnectionEvent.Establish(server, stargateConnection));
	}
	
	public static void onStargateConnectionTerminated(MinecraftServer server, StargateConnection stargateConnection, StargateInfo.Feedback feedback)
	{
		NeoForge.EVENT_BUS.post(new StargateConnectionEvent.Terminate(server, stargateConnection, feedback));
	}
	
	// Transporter
	
	public static boolean onTransporterDialID(MinecraftServer server, Transporter transporter, TransporterID transporterID)
	{
		return NeoForge.EVENT_BUS.post(new TransporterEvent.DialID(server, transporter, transporterID)).isCanceled();
	}
	
	public static boolean onTransporterDialCoords(MinecraftServer server, Transporter transporter, Vec3i coords)
	{
		return NeoForge.EVENT_BUS.post(new TransporterEvent.DialCoords(server, transporter, coords)).isCanceled();
	}
	
	public static boolean onTransporterConnect(MinecraftServer server, Transporter transporter, Transporter connectedTransporter, @Nullable TransporterConnection.Type connectionType)
	{
		return NeoForge.EVENT_BUS.post(new TransporterEvent.Connect(server, transporter, connectedTransporter, connectionType)).isCanceled();
	}
	
	public static boolean onTransporterTransport(MinecraftServer server, Transporter transporter, Transporter destinationTransporter, Entity traveler)
	{
		return NeoForge.EVENT_BUS.post(new TransporterEvent.Transport(server, transporter, destinationTransporter, traveler)).isCanceled();
	}
	
	// Transporter Connection
	
	public static void onTransporterConnectionEstablished(MinecraftServer server, TransporterConnection transporterConnection)
	{
		NeoForge.EVENT_BUS.post(new TransporterConnectionEvent.Establish(server, transporterConnection));
	}
	
	public static void onTransporterConnectionTerminated(MinecraftServer server, TransporterConnection transporterConnection, TransporterInfo.Feedback feedback)
	{
		NeoForge.EVENT_BUS.post(new TransporterConnectionEvent.Terminate(server, transporterConnection, feedback));
	}
}
