package net.povstalec.sgjourney.common.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.packets.ClientboundCartoucheUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundCrystallizerUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundDHDUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundDialerUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundInterfaceUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundMilkyWayStargateUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundNaquadahGeneratorUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundNaquadahLiquidizerUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundPegasusStargateUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundRingPanelUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundRingsUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundStargateParticleSpawnPacket;
import net.povstalec.sgjourney.common.packets.ClientboundStargateStateUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundStargateUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundSymbolUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundUniverseStargateUpdatePacket;
import net.povstalec.sgjourney.common.packets.ServerboundDHDUpdatePacket;
import net.povstalec.sgjourney.common.packets.ServerboundRingPanelUpdatePacket;

public final class PacketHandlerInit
{
	private static final String PROTOCOL_VERSION = "1";
	
	public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(new ResourceLocation(StargateJourney.MODID, "main_network"), 
			() -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
	
	private PacketHandlerInit(){}
	
	public static void register()
	{
		int index = 0;
		
		//============================================================================================
		//****************************************Client-bound****************************************
		//============================================================================================
		
		// Alien Tech
		INSTANCE.messageBuilder(ClientboundDialerUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundDialerUpdatePacket::encode)
		.decoder(ClientboundDialerUpdatePacket::new)
		.consumerMainThread(ClientboundDialerUpdatePacket::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientboundInterfaceUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundInterfaceUpdatePacket::encode)
		.decoder(ClientboundInterfaceUpdatePacket::new)
		.consumerMainThread(ClientboundInterfaceUpdatePacket::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientboundRingsUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundRingsUpdatePacket::encode)
		.decoder(ClientboundRingsUpdatePacket::new)
		.consumerMainThread(ClientboundRingsUpdatePacket::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientboundRingsUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundRingsUpdatePacket::encode)
		.decoder(ClientboundRingsUpdatePacket::new)
		.consumerMainThread(ClientboundRingsUpdatePacket::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientboundRingPanelUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundRingPanelUpdatePacket::encode)
		.decoder(ClientboundRingPanelUpdatePacket::new)
		.consumerMainThread(ClientboundRingPanelUpdatePacket::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientboundDHDUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundDHDUpdatePacket::encode)
		.decoder(ClientboundDHDUpdatePacket::new)
		.consumerMainThread(ClientboundDHDUpdatePacket::handle)
		.add();
		
		// Stargates
		INSTANCE.messageBuilder(ClientboundStargateParticleSpawnPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundStargateParticleSpawnPacket::encode)
		.decoder(ClientboundStargateParticleSpawnPacket::new)
		.consumerMainThread(ClientboundStargateParticleSpawnPacket::handle)
		.add();
		INSTANCE.messageBuilder(ClientboundStargateUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundStargateUpdatePacket::encode)
		.decoder(ClientboundStargateUpdatePacket::new)
		.consumerMainThread(ClientboundStargateUpdatePacket::handle)
		.add();
		INSTANCE.messageBuilder(ClientboundStargateStateUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundStargateStateUpdatePacket::encode)
		.decoder(ClientboundStargateStateUpdatePacket::new)
		.consumerMainThread(ClientboundStargateStateUpdatePacket::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientboundUniverseStargateUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundUniverseStargateUpdatePacket::encode)
		.decoder(ClientboundUniverseStargateUpdatePacket::new)
		.consumerMainThread(ClientboundUniverseStargateUpdatePacket::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientboundMilkyWayStargateUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundMilkyWayStargateUpdatePacket::encode)
		.decoder(ClientboundMilkyWayStargateUpdatePacket::new)
		.consumerMainThread(ClientboundMilkyWayStargateUpdatePacket::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientboundPegasusStargateUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundPegasusStargateUpdatePacket::encode)
		.decoder(ClientboundPegasusStargateUpdatePacket::new)
		.consumerMainThread(ClientboundPegasusStargateUpdatePacket::handle)
		.add();
		
		// Misc
		INSTANCE.messageBuilder(ClientboundNaquadahGeneratorUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundNaquadahGeneratorUpdatePacket::encode)
		.decoder(ClientboundNaquadahGeneratorUpdatePacket::new)
		.consumerMainThread(ClientboundNaquadahGeneratorUpdatePacket::handle)
		.add();

		INSTANCE.messageBuilder(ClientboundNaquadahLiquidizerUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundNaquadahLiquidizerUpdatePacket::encode)
		.decoder(ClientboundNaquadahLiquidizerUpdatePacket::new)
		.consumerMainThread(ClientboundNaquadahLiquidizerUpdatePacket::handle)
		.add();

		INSTANCE.messageBuilder(ClientboundCrystallizerUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundCrystallizerUpdatePacket::encode)
		.decoder(ClientboundCrystallizerUpdatePacket::new)
		.consumerMainThread(ClientboundCrystallizerUpdatePacket::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientboundSymbolUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundSymbolUpdatePacket::encode)
		.decoder(ClientboundSymbolUpdatePacket::new)
		.consumerMainThread(ClientboundSymbolUpdatePacket::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientboundCartoucheUpdatePacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundCartoucheUpdatePacket::encode)
		.decoder(ClientboundCartoucheUpdatePacket::new)
		.consumerMainThread(ClientboundCartoucheUpdatePacket::handle)
		.add();
		
		//============================================================================================
		//*******************************************Sounds*******************************************
		//============================================================================================
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.OpenWormhole.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientBoundSoundPackets.OpenWormhole::encode)
		.decoder(ClientBoundSoundPackets.OpenWormhole::new)
		.consumerMainThread(ClientBoundSoundPackets.OpenWormhole::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.IdleWormhole.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientBoundSoundPackets.IdleWormhole::encode)
		.decoder(ClientBoundSoundPackets.IdleWormhole::new)
		.consumerMainThread(ClientBoundSoundPackets.IdleWormhole::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.CloseWormhole.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientBoundSoundPackets.CloseWormhole::encode)
		.decoder(ClientBoundSoundPackets.CloseWormhole::new)
		.consumerMainThread(ClientBoundSoundPackets.CloseWormhole::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.Chevron.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientBoundSoundPackets.Chevron::encode)
		.decoder(ClientBoundSoundPackets.Chevron::new)
		.consumerMainThread(ClientBoundSoundPackets.Chevron::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.Fail.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientBoundSoundPackets.Fail::encode)
		.decoder(ClientBoundSoundPackets.Fail::new)
		.consumerMainThread(ClientBoundSoundPackets.Fail::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.StargateRotation.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientBoundSoundPackets.StargateRotation::encode)
		.decoder(ClientBoundSoundPackets.StargateRotation::new)
		.consumerMainThread(ClientBoundSoundPackets.StargateRotation::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.UniverseStart.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientBoundSoundPackets.UniverseStart::encode)
		.decoder(ClientBoundSoundPackets.UniverseStart::new)
		.consumerMainThread(ClientBoundSoundPackets.UniverseStart::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.MilkyWayBuildup.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientBoundSoundPackets.MilkyWayBuildup::encode)
		.decoder(ClientBoundSoundPackets.MilkyWayBuildup::new)
		.consumerMainThread(ClientBoundSoundPackets.MilkyWayBuildup::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.MilkyWayStop.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientBoundSoundPackets.MilkyWayStop::encode)
		.decoder(ClientBoundSoundPackets.MilkyWayStop::new)
		.consumerMainThread(ClientBoundSoundPackets.MilkyWayStop::handle)
		.add();
		
		//============================================================================================
		//****************************************Server-bound****************************************
		//============================================================================================
		
		INSTANCE.messageBuilder(ServerboundDHDUpdatePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
		.encoder(ServerboundDHDUpdatePacket::encode)
		.decoder(ServerboundDHDUpdatePacket::new)
		.consumerMainThread(ServerboundDHDUpdatePacket::handle)
		.add();
		
		INSTANCE.messageBuilder(ServerboundRingPanelUpdatePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
		.encoder(ServerboundRingPanelUpdatePacket::encode)
		.decoder(ServerboundRingPanelUpdatePacket::new)
		.consumerMainThread(ServerboundRingPanelUpdatePacket::handle)
		.add();
	}
}
