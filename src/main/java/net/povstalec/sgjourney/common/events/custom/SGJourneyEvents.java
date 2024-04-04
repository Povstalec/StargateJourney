package net.povstalec.sgjourney.common.events.custom;

import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Connection;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class SGJourneyEvents
{
	public static boolean onStargateDial(MinecraftServer server, Stargate stargate, Address address)
    {
        return MinecraftForge.EVENT_BUS.post(new StargateEvent.Dial(server, stargate, address));
    }
	
	public static boolean onStargateConnect(MinecraftServer server, Stargate stargate, Stargate connectedStargate, Connection.Type connectionType)
    {
        return MinecraftForge.EVENT_BUS.post(new StargateEvent.Connect(server, stargate, connectedStargate, connectionType));
    }
	
	/*public static boolean onWormhole(MinecraftServer server, Stargate stargate, Stargate connectedStargate, Connection.Type connectionType,
			Entity traveler, Vec3 momentum, Stargate.WormholeTravel twoWayWormhole)
    {
        return MinecraftForge.EVENT_BUS.post(new StargateEvent.Connect(server, stargate, connectedStargate, connectionType));
    }*/
}
