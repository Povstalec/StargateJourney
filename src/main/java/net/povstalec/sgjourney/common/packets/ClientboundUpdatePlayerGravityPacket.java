package net.povstalec.sgjourney.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundUpdatePlayerGravityPacket(double gravity) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ClientboundUpdatePlayerGravityPacket> TYPE =
			new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_update_player_gravity"));
	
	public static final StreamCodec<ByteBuf, ClientboundUpdatePlayerGravityPacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.DOUBLE, ClientboundUpdatePlayerGravityPacket::gravity,
			ClientboundUpdatePlayerGravityPacket::new
	);
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	public static void handle(ClientboundUpdatePlayerGravityPacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() -> {
			ClientAccess.updatePlayerGravity(packet.gravity);
		});
	}
}


