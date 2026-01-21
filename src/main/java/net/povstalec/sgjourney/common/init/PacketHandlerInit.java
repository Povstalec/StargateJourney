package net.povstalec.sgjourney.common.init;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.packets.*;

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
		
		// Screen opening
		INSTANCE.messageBuilder(ClientboundDialerOpenScreenPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundDialerOpenScreenPacket::encode)
		.decoder(ClientboundDialerOpenScreenPacket::new)
		.consumerMainThread(ClientboundDialerOpenScreenPacket::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientboundGDOOpenScreenPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundGDOOpenScreenPacket::encode)
		.decoder(ClientboundGDOOpenScreenPacket::new)
		.consumerMainThread(ClientboundGDOOpenScreenPacket::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientboundArcheologistNotebookOpenScreenPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundArcheologistNotebookOpenScreenPacket::encode)
		.decoder(ClientboundArcheologistNotebookOpenScreenPacket::new)
		.consumerMainThread(ClientboundArcheologistNotebookOpenScreenPacket::handle)
		.add();
		
		// Alien Tech
		
		// Stargates
		INSTANCE.messageBuilder(ClientboundStargateParticleSpawnPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientboundStargateParticleSpawnPacket::encode)
		.decoder(ClientboundStargateParticleSpawnPacket::new)
		.consumerMainThread(ClientboundStargateParticleSpawnPacket::handle)
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
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.IrisThud.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientBoundSoundPackets.IrisThud::encode)
		.decoder(ClientBoundSoundPackets.IrisThud::new)
		.consumerMainThread(ClientBoundSoundPackets.IrisThud::handle)
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
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.RotationStartup.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientBoundSoundPackets.RotationStartup::encode)
		.decoder(ClientBoundSoundPackets.RotationStartup::new)
		.consumerMainThread(ClientBoundSoundPackets.RotationStartup::handle)
		.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.RotationStop.class, index++, NetworkDirection.PLAY_TO_CLIENT)
		.encoder(ClientBoundSoundPackets.RotationStop::encode)
		.decoder(ClientBoundSoundPackets.RotationStop::new)
		.consumerMainThread(ClientBoundSoundPackets.RotationStop::handle)
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
		
		INSTANCE.messageBuilder(ServerboundInterfaceUpdatePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
		.encoder(ServerboundInterfaceUpdatePacket::encode)
		.decoder(ServerboundInterfaceUpdatePacket::new)
		.consumerMainThread(ServerboundInterfaceUpdatePacket::handle)
		.add();
		
		INSTANCE.messageBuilder(ServerboundGDOUpdatePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
		.encoder(ServerboundGDOUpdatePacket::encode)
		.decoder(ServerboundGDOUpdatePacket::new)
		.consumerMainThread(ServerboundGDOUpdatePacket::handle)
		.add();
		
		INSTANCE.messageBuilder(ServerboundTransceiverUpdatePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
		.encoder(ServerboundTransceiverUpdatePacket::encode)
		.decoder(ServerboundTransceiverUpdatePacket::new)
		.consumerMainThread(ServerboundTransceiverUpdatePacket::handle)
		.add();
	}
}
