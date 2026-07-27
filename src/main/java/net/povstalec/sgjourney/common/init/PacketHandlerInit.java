package net.povstalec.sgjourney.common.init;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.povstalec.sgjourney.common.packets.*;

public final class PacketHandlerInit
{
	//============================================================================================
	//****************************************Registering*****************************************
	//============================================================================================
	
	@SubscribeEvent
	public static void registerPackets(final RegisterPayloadHandlersEvent event)
	{
		// Sets the current network version
		final PayloadRegistrar registrar = event.registrar("1");
		
		//============================================================================================
		//****************************************Client-bound****************************************
		//============================================================================================
		
		// Player
		registrar.playToClient(
				ClientboundUpdatePlayerGravityPacket.TYPE,
				ClientboundUpdatePlayerGravityPacket.STREAM_CODEC,
				ClientboundUpdatePlayerGravityPacket::handle);
		
		// Screen opening
		registrar.playToClient(
				ClientboundDialerOpenScreenPacket.TYPE,
				ClientboundDialerOpenScreenPacket.STREAM_CODEC,
				ClientboundDialerOpenScreenPacket::handle);
		
		registrar.playToClient(
				ClientboundGDOOpenScreenPacket.TYPE,
				ClientboundGDOOpenScreenPacket.STREAM_CODEC,
				ClientboundGDOOpenScreenPacket::handle);
		
		registrar.playToClient(
				ClientboundCrystalComputerOpenMainScreenPacket.TYPE,
				ClientboundCrystalComputerOpenMainScreenPacket.STREAM_CODEC,
				ClientboundCrystalComputerOpenMainScreenPacket::handle);
		
		registrar.playToClient(
				ClientboundCrystalComputerOpenSaveScreenPacket.TYPE,
				ClientboundCrystalComputerOpenSaveScreenPacket.STREAM_CODEC,
				ClientboundCrystalComputerOpenSaveScreenPacket::handle);
		
		registrar.playToClient(
				ClientboundArcheologistNotebookOpenScreenPacket.TYPE,
				ClientboundArcheologistNotebookOpenScreenPacket.STREAM_CODEC,
				ClientboundArcheologistNotebookOpenScreenPacket::handle);
		
		// Alien Tech
		
		// Stargates
		registrar.playToClient(
				ClientboundStargateParticleSpawnPacket.TYPE,
				ClientboundStargateParticleSpawnPacket.STREAM_CODEC,
				ClientboundStargateParticleSpawnPacket::handle);
		
		
		//============================================================================================
		//*******************************************Sounds*******************************************
		//============================================================================================
		
		registrar.playToClient(
				ClientBoundSoundPackets.OpenWormhole.TYPE,
				ClientBoundSoundPackets.OpenWormhole.STREAM_CODEC,
				ClientBoundSoundPackets.OpenWormhole::handle);
		
		registrar.playToClient(
				ClientBoundSoundPackets.IdleWormhole.TYPE,
				ClientBoundSoundPackets.IdleWormhole.STREAM_CODEC,
				ClientBoundSoundPackets.IdleWormhole::handle);
		
		registrar.playToClient(
				ClientBoundSoundPackets.CloseWormhole.TYPE,
				ClientBoundSoundPackets.CloseWormhole.STREAM_CODEC,
				ClientBoundSoundPackets.CloseWormhole::handle);
		
		registrar.playToClient(
				ClientBoundSoundPackets.IrisThud.TYPE,
				ClientBoundSoundPackets.IrisThud.STREAM_CODEC,
				ClientBoundSoundPackets.IrisThud::handle);
		
		registrar.playToClient(
				ClientBoundSoundPackets.Chevron.TYPE,
				ClientBoundSoundPackets.Chevron.STREAM_CODEC,
				ClientBoundSoundPackets.Chevron::handle);
		
		registrar.playToClient(
				ClientBoundSoundPackets.Fail.TYPE,
				ClientBoundSoundPackets.Fail.STREAM_CODEC,
				ClientBoundSoundPackets.Fail::handle);
		
		registrar.playToClient(
				ClientBoundSoundPackets.StargateRotation.TYPE,
				ClientBoundSoundPackets.StargateRotation.STREAM_CODEC,
				ClientBoundSoundPackets.StargateRotation::handle);
		
		registrar.playToClient(
				ClientBoundSoundPackets.UniverseStart.TYPE,
				ClientBoundSoundPackets.UniverseStart.STREAM_CODEC,
				ClientBoundSoundPackets.UniverseStart::handle);
		
		registrar.playToClient(
				ClientBoundSoundPackets.RotationStartup.TYPE,
				ClientBoundSoundPackets.RotationStartup.STREAM_CODEC,
				ClientBoundSoundPackets.RotationStartup::handle);
		
		registrar.playToClient(
				ClientBoundSoundPackets.RotationStop.TYPE,
				ClientBoundSoundPackets.RotationStop.STREAM_CODEC,
				ClientBoundSoundPackets.RotationStop::handle);
		
		//============================================================================================
		//****************************************Server-bound****************************************
		//============================================================================================
		
		registrar.playToServer(
				ServerboundDHDUpdatePacket.TYPE,
				ServerboundDHDUpdatePacket.STREAM_CODEC,
				ServerboundDHDUpdatePacket::handle);
		
		registrar.playToServer(
				ServerboundRingPanelUpdatePacket.TYPE,
				ServerboundRingPanelUpdatePacket.STREAM_CODEC,
				ServerboundRingPanelUpdatePacket::handle);
		
		registrar.playToServer(
				ServerboundInterfaceUpdatePacket.TYPE,
				ServerboundInterfaceUpdatePacket.STREAM_CODEC,
				ServerboundInterfaceUpdatePacket::handle);
		
		registrar.playToServer(
				ServerboundGDOUpdatePacket.TYPE,
				ServerboundGDOUpdatePacket.STREAM_CODEC,
				ServerboundGDOUpdatePacket::handle);
		
		registrar.playToServer(
				ServerboundCrystalComputerUpdatePacket.TYPE,
				ServerboundCrystalComputerUpdatePacket.STREAM_CODEC,
				ServerboundCrystalComputerUpdatePacket::handle);
		
		registrar.playToServer(
				ServerboundTransceiverUpdatePacket.TYPE,
				ServerboundTransceiverUpdatePacket.STREAM_CODEC,
				ServerboundTransceiverUpdatePacket::handle);
		
		registrar.playToServer(
				ServerboundLiquidizerUpdatePacket.TYPE,
				ServerboundLiquidizerUpdatePacket.STREAM_CODEC,
				ServerboundLiquidizerUpdatePacket::handle);
		
		registrar.playToServer(
				ServerboundCrystallizerUpdatePacket.TYPE,
				ServerboundCrystallizerUpdatePacket.STREAM_CODEC,
				ServerboundCrystallizerUpdatePacket::handle);
	}
}
