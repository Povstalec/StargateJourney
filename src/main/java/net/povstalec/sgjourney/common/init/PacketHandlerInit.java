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
		
		// Player
		INSTANCE.messageBuilder(ClientboundUpdatePlayerGravityPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientboundUpdatePlayerGravityPacket::encode)
				.decoder(ClientboundUpdatePlayerGravityPacket::new)
				.consumer(ClientboundUpdatePlayerGravityPacket::handle)
				.add();
		
		// Screen opening
		INSTANCE.messageBuilder(ClientboundDialerOpenScreenPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientboundDialerOpenScreenPacket::encode)
				.decoder(ClientboundDialerOpenScreenPacket::new)
				.consumer(ClientboundDialerOpenScreenPacket::handle)
				.add();
		
		INSTANCE.messageBuilder(ClientboundGDOOpenScreenPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientboundGDOOpenScreenPacket::encode)
				.decoder(ClientboundGDOOpenScreenPacket::new)
				.consumer(ClientboundGDOOpenScreenPacket::handle)
				.add();
		
		INSTANCE.messageBuilder(ClientboundCrystalComputerOpenMainScreenPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientboundCrystalComputerOpenMainScreenPacket::encode)
				.decoder(ClientboundCrystalComputerOpenMainScreenPacket::new)
				.consumer(ClientboundCrystalComputerOpenMainScreenPacket::handle)
				.add();
		
		INSTANCE.messageBuilder(ClientboundCrystalComputerOpenSaveScreenPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientboundCrystalComputerOpenSaveScreenPacket::encode)
				.decoder(ClientboundCrystalComputerOpenSaveScreenPacket::new)
				.consumer(ClientboundCrystalComputerOpenSaveScreenPacket::handle)
				.add();
		
		INSTANCE.messageBuilder(ClientboundArcheologistNotebookOpenScreenPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientboundArcheologistNotebookOpenScreenPacket::encode)
				.decoder(ClientboundArcheologistNotebookOpenScreenPacket::new)
				.consumer(ClientboundArcheologistNotebookOpenScreenPacket::handle)
				.add();
		
		// Alien Tech
		
		// Stargates
		INSTANCE.messageBuilder(ClientboundStargateParticleSpawnPacket.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientboundStargateParticleSpawnPacket::encode)
				.decoder(ClientboundStargateParticleSpawnPacket::new)
				.consumer(ClientboundStargateParticleSpawnPacket::handle)
				.add();
		
		//============================================================================================
		//*******************************************Sounds*******************************************
		//============================================================================================
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.OpenWormhole.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientBoundSoundPackets.OpenWormhole::encode)
				.decoder(ClientBoundSoundPackets.OpenWormhole::new)
				.consumer(ClientBoundSoundPackets.OpenWormhole::handle)
				.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.IdleWormhole.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientBoundSoundPackets.IdleWormhole::encode)
				.decoder(ClientBoundSoundPackets.IdleWormhole::new)
				.consumer(ClientBoundSoundPackets.IdleWormhole::handle)
				.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.CloseWormhole.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientBoundSoundPackets.CloseWormhole::encode)
				.decoder(ClientBoundSoundPackets.CloseWormhole::new)
				.consumer(ClientBoundSoundPackets.CloseWormhole::handle)
				.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.IrisThud.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientBoundSoundPackets.IrisThud::encode)
				.decoder(ClientBoundSoundPackets.IrisThud::new)
				.consumer(ClientBoundSoundPackets.IrisThud::handle)
				.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.Chevron.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientBoundSoundPackets.Chevron::encode)
				.decoder(ClientBoundSoundPackets.Chevron::new)
				.consumer(ClientBoundSoundPackets.Chevron::handle)
				.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.Fail.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientBoundSoundPackets.Fail::encode)
				.decoder(ClientBoundSoundPackets.Fail::new)
				.consumer(ClientBoundSoundPackets.Fail::handle)
				.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.StargateRotation.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientBoundSoundPackets.StargateRotation::encode)
				.decoder(ClientBoundSoundPackets.StargateRotation::new)
				.consumer(ClientBoundSoundPackets.StargateRotation::handle)
				.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.UniverseStart.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientBoundSoundPackets.UniverseStart::encode)
				.decoder(ClientBoundSoundPackets.UniverseStart::new)
				.consumer(ClientBoundSoundPackets.UniverseStart::handle)
				.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.RotationStartup.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientBoundSoundPackets.RotationStartup::encode)
				.decoder(ClientBoundSoundPackets.RotationStartup::new)
				.consumer(ClientBoundSoundPackets.RotationStartup::handle)
				.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.RotationStop.class, index++, NetworkDirection.PLAY_TO_CLIENT)
				.encoder(ClientBoundSoundPackets.RotationStop::encode)
				.decoder(ClientBoundSoundPackets.RotationStop::new)
				.consumer(ClientBoundSoundPackets.RotationStop::handle)
				.add();
		
		INSTANCE.messageBuilder(ClientBoundSoundPackets.TransportRingsTransport.class, index++, NetworkDirection.PLAY_TO_CLIENT)
			.encoder(ClientBoundSoundPackets.TransportRingsTransport::encode)
			.decoder(ClientBoundSoundPackets.TransportRingsTransport::new)
			.consumer(ClientBoundSoundPackets.TransportRingsTransport::handle)
			.add();
		
		//============================================================================================
		//****************************************Server-bound****************************************
		//============================================================================================
		
		INSTANCE.messageBuilder(ServerboundDHDUpdatePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ServerboundDHDUpdatePacket::encode)
				.decoder(ServerboundDHDUpdatePacket::new)
				.consumer(ServerboundDHDUpdatePacket::handle)
				.add();
		
		INSTANCE.messageBuilder(ServerboundRingPanelUpdatePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ServerboundRingPanelUpdatePacket::encode)
				.decoder(ServerboundRingPanelUpdatePacket::new)
				.consumer(ServerboundRingPanelUpdatePacket::handle)
				.add();
		
		INSTANCE.messageBuilder(ServerboundInterfaceUpdatePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ServerboundInterfaceUpdatePacket::encode)
				.decoder(ServerboundInterfaceUpdatePacket::new)
				.consumer(ServerboundInterfaceUpdatePacket::handle)
				.add();
		
		INSTANCE.messageBuilder(ServerboundGDOUpdatePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ServerboundGDOUpdatePacket::encode)
				.decoder(ServerboundGDOUpdatePacket::new)
				.consumer(ServerboundGDOUpdatePacket::handle)
				.add();
		
		INSTANCE.messageBuilder(ServerboundCrystalComputerUpdatePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ServerboundCrystalComputerUpdatePacket::encode)
				.decoder(ServerboundCrystalComputerUpdatePacket::new)
				.consumer(ServerboundCrystalComputerUpdatePacket::handle)
				.add();
		
		INSTANCE.messageBuilder(ServerboundTransceiverUpdatePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ServerboundTransceiverUpdatePacket::encode)
				.decoder(ServerboundTransceiverUpdatePacket::new)
				.consumer(ServerboundTransceiverUpdatePacket::handle)
				.add();
		
		INSTANCE.messageBuilder(ServerboundLiquidizerUpdatePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ServerboundLiquidizerUpdatePacket::encode)
				.decoder(ServerboundLiquidizerUpdatePacket::new)
				.consumer(ServerboundLiquidizerUpdatePacket::handle)
				.add();
		
		INSTANCE.messageBuilder(ServerboundCrystallizerUpdatePacket.class, index++, NetworkDirection.PLAY_TO_SERVER)
				.encoder(ServerboundCrystallizerUpdatePacket::encode)
				.decoder(ServerboundCrystallizerUpdatePacket::new)
				.consumer(ServerboundCrystallizerUpdatePacket::handle)
				.add();
	}
}
