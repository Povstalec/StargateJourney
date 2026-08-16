package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundCrystalComputerOpenSaveScreenPacket(InteractionHand interactionHand, BlockPos clickedPos) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundCrystalComputerOpenSaveScreenPacket> TYPE =
			new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_crystal_computer_open_save_screen"));
	
	public static final StreamCodec<FriendlyByteBuf, ClientboundCrystalComputerOpenSaveScreenPacket> STREAM_CODEC = StreamCodec.composite(
			NeoForgeStreamCodecs.enumCodec(InteractionHand.class), ClientboundCrystalComputerOpenSaveScreenPacket::interactionHand,
			BlockPos.STREAM_CODEC, ClientboundCrystalComputerOpenSaveScreenPacket::clickedPos,
			ClientboundCrystalComputerOpenSaveScreenPacket::new
	);
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	public static void handle(ClientboundCrystalComputerOpenSaveScreenPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() -> {
			ClientAccess.openCrystalComputerSaveScreen(packet.interactionHand, packet.clickedPos);
		});
	}
}


