package net.povstalec.sgjourney.common.events.custom;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

public class SGJourneyEvents
{
	// Stargate
	
	public static boolean onStargateDial(MinecraftServer server, Stargate stargate, Address.Immutable dialedAddress, boolean doKawoosh)
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
	
	public static void onConnectionEstablished(MinecraftServer server, StargateConnection stargateConnection)
	{
		NeoForge.EVENT_BUS.post(new ConnectionEvent.Establish(server, stargateConnection));
	}
	
	public static void onConnectionTerminated(MinecraftServer server, StargateConnection stargateConnection, StargateInfo.Feedback feedback)
	{
		NeoForge.EVENT_BUS.post(new ConnectionEvent.Terminate(server, stargateConnection, feedback));
	}
	
	// Transporter
	
	
	
	// Transporter Connection
	
	
}
