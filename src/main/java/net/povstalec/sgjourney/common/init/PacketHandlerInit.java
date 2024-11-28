package net.povstalec.sgjourney.common.init;

import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import net.povstalec.sgjourney.common.packets.ClientBoundSoundPackets;
import net.povstalec.sgjourney.common.packets.ClientboundCartoucheUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundCrystallizerUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundDHDUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundDialerOpenScreenPacket;
import net.povstalec.sgjourney.common.packets.ClientboundGDOOpenScreenPacket;
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
import net.povstalec.sgjourney.common.packets.ClientboundTransceiverUpdatePacket;
import net.povstalec.sgjourney.common.packets.ClientboundUniverseStargateUpdatePacket;
import net.povstalec.sgjourney.common.packets.ServerboundDHDUpdatePacket;
import net.povstalec.sgjourney.common.packets.ServerboundGDOUpdatePacket;
import net.povstalec.sgjourney.common.packets.ServerboundRingPanelUpdatePacket;
import net.povstalec.sgjourney.common.packets.ServerboundTransceiverUpdatePacket;

public final class PacketHandlerInit
{
	//============================================================================================
	//****************************************Registering*****************************************
	//============================================================================================
	
	@SubscribeEvent
	public static void register(final RegisterPayloadHandlersEvent event)
	{
		// Sets the current network version
		final PayloadRegistrar registrar = event.registrar("1");
		
		//============================================================================================
		//****************************************Client-bound****************************************
		//============================================================================================
		
		// Screen opening
		registrar.playToClient(
				ClientboundDialerOpenScreenPacket.TYPE,
				ClientboundDialerOpenScreenPacket.STREAM_CODEC,
				(packet, context) -> ClientboundDialerOpenScreenPacket.handle(packet, context));
		
		registrar.playToClient(
				ClientboundGDOOpenScreenPacket.TYPE,
				ClientboundGDOOpenScreenPacket.STREAM_CODEC,
				(packet, context) -> ClientboundGDOOpenScreenPacket.handle(packet, context));
		
		
		// Tech
		registrar.playToClient(
				ClientboundTransceiverUpdatePacket.TYPE,
				ClientboundTransceiverUpdatePacket.STREAM_CODEC,
				(packet, context) -> ClientboundTransceiverUpdatePacket.handle(packet, context));
		
		registrar.playToClient(
				ClientboundInterfaceUpdatePacket.TYPE,
				ClientboundInterfaceUpdatePacket.STREAM_CODEC,
				(packet, context) -> ClientboundInterfaceUpdatePacket.handle(packet, context));
		
		registrar.playToClient(
				ClientboundRingsUpdatePacket.TYPE,
				ClientboundRingsUpdatePacket.STREAM_CODEC,
				(packet, context) -> ClientboundRingsUpdatePacket.handle(packet, context));
		
		registrar.playToClient(
				ClientboundRingPanelUpdatePacket.TYPE,
				ClientboundRingPanelUpdatePacket.STREAM_CODEC,
				(packet, context) -> ClientboundRingPanelUpdatePacket.handle(packet, context));
		
		registrar.playToClient(
				ClientboundDHDUpdatePacket.TYPE,
				ClientboundDHDUpdatePacket.STREAM_CODEC,
				(packet, context) -> ClientboundDHDUpdatePacket.handle(packet, context));
		
		
		//Stargate Info
		registrar.playToClient(
				ClientboundStargateParticleSpawnPacket.TYPE,
				ClientboundStargateParticleSpawnPacket.STREAM_CODEC,
				(packet, context) -> ClientboundStargateParticleSpawnPacket.handle(packet, context));
		
		registrar.playToClient(
				ClientboundStargateUpdatePacket.TYPE,
				ClientboundStargateUpdatePacket.STREAM_CODEC,
				(packet, context) -> ClientboundStargateUpdatePacket.handle(packet, context));
		
		registrar.playToClient(
				ClientboundStargateStateUpdatePacket.TYPE,
				ClientboundStargateStateUpdatePacket.STREAM_CODEC,
				(packet, context) -> ClientboundStargateStateUpdatePacket.handle(packet, context));
		
		
		// Stargate Type Info
		registrar.playToClient(
				ClientboundUniverseStargateUpdatePacket.TYPE,
				ClientboundUniverseStargateUpdatePacket.STREAM_CODEC,
				(packet, context) -> ClientboundUniverseStargateUpdatePacket.handle(packet, context));
		
		registrar.playToClient(
				ClientboundMilkyWayStargateUpdatePacket.TYPE,
				ClientboundMilkyWayStargateUpdatePacket.STREAM_CODEC,
				(packet, context) -> ClientboundMilkyWayStargateUpdatePacket.handle(packet, context));
		
		registrar.playToClient(
				ClientboundPegasusStargateUpdatePacket.TYPE,
				ClientboundPegasusStargateUpdatePacket.STREAM_CODEC,
				(packet, context) -> ClientboundPegasusStargateUpdatePacket.handle(packet, context));
		
		
		// Misc
		registrar.playToClient(
				ClientboundNaquadahGeneratorUpdatePacket.TYPE,
				ClientboundNaquadahGeneratorUpdatePacket.STREAM_CODEC,
				(packet, context) -> ClientboundNaquadahGeneratorUpdatePacket.handle(packet, context));
		
		registrar.playToClient(
				ClientboundNaquadahLiquidizerUpdatePacket.TYPE,
				ClientboundNaquadahLiquidizerUpdatePacket.STREAM_CODEC,
				(packet, context) -> ClientboundNaquadahLiquidizerUpdatePacket.handle(packet, context));
		
		registrar.playToClient(
				ClientboundCrystallizerUpdatePacket.TYPE,
				ClientboundCrystallizerUpdatePacket.STREAM_CODEC,
				(packet, context) -> ClientboundCrystallizerUpdatePacket.handle(packet, context));
		
		registrar.playToClient(
				ClientboundSymbolUpdatePacket.TYPE,
				ClientboundSymbolUpdatePacket.STREAM_CODEC,
				(packet, context) -> ClientboundSymbolUpdatePacket.handle(packet, context));
		
		registrar.playToClient(
				ClientboundCartoucheUpdatePacket.TYPE,
				ClientboundCartoucheUpdatePacket.STREAM_CODEC,
				(packet, context) -> ClientboundCartoucheUpdatePacket.handle(packet, context));
		
		
		// Sounds
		registrar.playToClient(
				ClientBoundSoundPackets.OpenWormhole.TYPE,
				ClientBoundSoundPackets.OpenWormhole.STREAM_CODEC,
				(packet, context) -> ClientBoundSoundPackets.OpenWormhole.handle(packet, context));
		
		registrar.playToClient(
				ClientBoundSoundPackets.IdleWormhole.TYPE,
				ClientBoundSoundPackets.IdleWormhole.STREAM_CODEC,
				(packet, context) -> ClientBoundSoundPackets.IdleWormhole.handle(packet, context));
		
		registrar.playToClient(
				ClientBoundSoundPackets.CloseWormhole.TYPE,
				ClientBoundSoundPackets.CloseWormhole.STREAM_CODEC,
				(packet, context) -> ClientBoundSoundPackets.CloseWormhole.handle(packet, context));
		
		registrar.playToClient(
				ClientBoundSoundPackets.IrisThud.TYPE,
				ClientBoundSoundPackets.IrisThud.STREAM_CODEC,
				(packet, context) -> ClientBoundSoundPackets.IrisThud.handle(packet, context));
		
		registrar.playToClient(
				ClientBoundSoundPackets.Chevron.TYPE,
				ClientBoundSoundPackets.Chevron.STREAM_CODEC,
				(packet, context) -> ClientBoundSoundPackets.Chevron.handle(packet, context));
		
		registrar.playToClient(
				ClientBoundSoundPackets.Fail.TYPE,
				ClientBoundSoundPackets.Fail.STREAM_CODEC,
				(packet, context) -> ClientBoundSoundPackets.Fail.handle(packet, context));
		
		registrar.playToClient(
				ClientBoundSoundPackets.StargateRotation.TYPE,
				ClientBoundSoundPackets.StargateRotation.STREAM_CODEC,
				(packet, context) -> ClientBoundSoundPackets.StargateRotation.handle(packet, context));
		
		registrar.playToClient(
				ClientBoundSoundPackets.UniverseStart.TYPE,
				ClientBoundSoundPackets.UniverseStart.STREAM_CODEC,
				(packet, context) -> ClientBoundSoundPackets.UniverseStart.handle(packet, context));
		
		registrar.playToClient(
				ClientBoundSoundPackets.MilkyWayBuildup.TYPE,
				ClientBoundSoundPackets.MilkyWayBuildup.STREAM_CODEC,
				(packet, context) -> ClientBoundSoundPackets.MilkyWayBuildup.handle(packet, context));
		
		registrar.playToClient(
				ClientBoundSoundPackets.MilkyWayStop.TYPE,
				ClientBoundSoundPackets.MilkyWayStop.STREAM_CODEC,
				(packet, context) -> ClientBoundSoundPackets.MilkyWayStop.handle(packet, context));
		
		
		
		//============================================================================================
		//****************************************Server-bound****************************************
		//============================================================================================
		
		registrar.playToServer(
				ServerboundDHDUpdatePacket.TYPE,
				ServerboundDHDUpdatePacket.STREAM_CODEC,
				(packet, context) -> ServerboundDHDUpdatePacket.handle(packet, context));
		
		registrar.playToServer(
				ServerboundRingPanelUpdatePacket.TYPE,
				ServerboundRingPanelUpdatePacket.STREAM_CODEC,
				(packet, context) -> ServerboundRingPanelUpdatePacket.handle(packet, context));
		
		registrar.playToServer(
				ServerboundGDOUpdatePacket.TYPE,
				ServerboundGDOUpdatePacket.STREAM_CODEC,
				(packet, context) -> ServerboundGDOUpdatePacket.handle(packet, context));
		
		registrar.playToServer(
				ServerboundTransceiverUpdatePacket.TYPE,
				ServerboundTransceiverUpdatePacket.STREAM_CODEC,
				(packet, context) -> ServerboundTransceiverUpdatePacket.handle(packet, context));
	}
}
