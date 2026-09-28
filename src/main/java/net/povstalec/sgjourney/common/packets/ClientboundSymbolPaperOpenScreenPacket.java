package net.povstalec.sgjourney.common.packets;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundSymbolPaperOpenScreenPacket(InteractionHand interactionHand) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ClientboundSymbolPaperOpenScreenPacket> TYPE =
		new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_symbol_paper_open_screen"));
	
	public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundSymbolPaperOpenScreenPacket> STREAM_CODEC = StreamCodec.composite(
		NeoForgeStreamCodecs.enumCodec(InteractionHand.class), ClientboundSymbolPaperOpenScreenPacket::interactionHand,
		ClientboundSymbolPaperOpenScreenPacket::new
	);
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	public static void handle(ClientboundSymbolPaperOpenScreenPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() -> ClientAccess.openSymbolPaperScreen(packet.interactionHand));
	}
}


