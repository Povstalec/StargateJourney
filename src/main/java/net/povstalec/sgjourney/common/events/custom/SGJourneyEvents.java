package net.povstalec.sgjourney.common.events.custom;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.neoforged.neoforge.common.NeoForge;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Stargate;
import net.povstalec.sgjourney.common.stargate.StargateConnection;

public class SGJourneyEvents
{
	public static boolean onStargateDial(MinecraftServer server, Stargate stargate, Address.Immutable dialedAddress, Address.Immutable dialingAddress, boolean doKawoosh)
    {
		return NeoForge.EVENT_BUS.post(new StargateEvent.Dial(server, stargate, dialedAddress, dialingAddress, doKawoosh)).isCanceled();
    }
	
	public static boolean onStargateConnect(MinecraftServer server, Stargate stargate, Stargate connectedStargate, StargateConnection.Type connectionType, Address.Type addressType, boolean doKawoosh)
    {
        return NeoForge.EVENT_BUS.post(new StargateEvent.Connect(server, stargate, connectedStargate, connectionType, addressType, doKawoosh)).isCanceled();
    }
	
	public static boolean onWormholeTravel(MinecraftServer server, Stargate stargate, Stargate connectedStargate, StargateConnection.Type connectionType,
			Entity traveler, Stargate.WormholeTravel wormholeTravel)
    {
        return NeoForge.EVENT_BUS.post(new StargateEvent.WormholeTravel(server, stargate, connectedStargate, connectionType, traveler, wormholeTravel)).isCanceled();
    }
}
