package net.povstalec.sgjourney.common.packets;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundAutoDialerOpenScreenPacket(InteractionHand interactionHand) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ClientboundAutoDialerOpenScreenPacket> TYPE =
		new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_auto_dialer_open_screen"));
	
	public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundAutoDialerOpenScreenPacket> STREAM_CODEC = StreamCodec.composite(
		NeoForgeStreamCodecs.enumCodec(InteractionHand.class), ClientboundAutoDialerOpenScreenPacket::interactionHand,
		ClientboundAutoDialerOpenScreenPacket::new
	);
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	public static void handle(ClientboundAutoDialerOpenScreenPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() -> ClientAccess.openAutoDialerScreen(packet.interactionHand));
	}
}


