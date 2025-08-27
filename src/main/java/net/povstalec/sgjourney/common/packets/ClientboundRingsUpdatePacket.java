package net.povstalec.sgjourney.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundRingsUpdatePacket(BlockPos blockPos, int emptySpace, int transportHeight, int progress) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundRingsUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_transport_rings_update"));
    
    public static final StreamCodec<ByteBuf, ClientboundRingsUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ClientboundRingsUpdatePacket::blockPos,
            ByteBufCodecs.VAR_INT, ClientboundRingsUpdatePacket::emptySpace,
            ByteBufCodecs.VAR_INT, ClientboundRingsUpdatePacket::transportHeight,
            ByteBufCodecs.VAR_INT, ClientboundRingsUpdatePacket::progress,
            ClientboundRingsUpdatePacket::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundRingsUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
        	ClientAccess.updateRings(packet.blockPos, packet.emptySpace, packet.transportHeight, packet.progress);
        });
    }
}


