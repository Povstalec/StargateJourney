package net.povstalec.sgjourney.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundCrystalComputerOpenMainScreenPacket(InteractionHand interactionHand) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ClientboundCrystalComputerOpenMainScreenPacket> TYPE =
			new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_crystal_computer_open_main_screen"));
	
	public static final StreamCodec<FriendlyByteBuf, ClientboundCrystalComputerOpenMainScreenPacket> STREAM_CODEC = StreamCodec.composite(
			NeoForgeStreamCodecs.enumCodec(InteractionHand.class), ClientboundCrystalComputerOpenMainScreenPacket::interactionHand,
			ClientboundCrystalComputerOpenMainScreenPacket::new
	);
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	public static void handle(ClientboundCrystalComputerOpenMainScreenPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() -> {
			ClientAccess.openCrystalComputerMainScreen(packet.interactionHand);
		});
	}
}


