package net.povstalec.sgjourney.common.events.custom;

import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.MinecraftForge;
import net.povstalec.sgjourney.common.stargate.Address;
import net.povstalec.sgjourney.common.stargate.Connection;
import net.povstalec.sgjourney.common.stargate.Stargate;

public class SGJourneyEvents
{
	public static boolean onStargateDial(MinecraftServer server, Stargate stargate, Address.Immutable address, boolean doKawoosh)
    {
        return MinecraftForge.EVENT_BUS.post(new StargateEvent.Dial(server, stargate, address, doKawoosh));
    }
	
	public static boolean onStargateConnect(MinecraftServer server, Stargate stargate, Stargate connectedStargate, Connection.Type connectionType, Address.Type addressType, boolean doKawoosh)
    {
        return MinecraftForge.EVENT_BUS.post(new StargateEvent.Connect(server, stargate, connectedStargate, connectionType, addressType, doKawoosh));
    }
	
	public static boolean onWormholeTravel(MinecraftServer server, Stargate stargate, Stargate connectedStargate, Connection.Type connectionType,
			Entity traveler, Stargate.WormholeTravel wormholeTravel)
    {
        return MinecraftForge.EVENT_BUS.post(new StargateEvent.WormholeTravel(server, stargate, connectedStargate, connectionType, traveler, wormholeTravel));
    }
}
