package net.povstalec.sgjourney.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundTransceiverUpdatePacket(BlockPos blockPos, boolean editingFrequency, String idc, int frequency) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundTransceiverUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_transceiver_update"));
    
    public static final StreamCodec<ByteBuf, ClientboundTransceiverUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ClientboundTransceiverUpdatePacket::blockPos,
            ByteBufCodecs.BOOL, ClientboundTransceiverUpdatePacket::editingFrequency,
            ByteBufCodecs.STRING_UTF8, ClientboundTransceiverUpdatePacket::idc,
            ByteBufCodecs.VAR_INT, ClientboundTransceiverUpdatePacket::frequency,
            ClientboundTransceiverUpdatePacket::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundTransceiverUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
        	ClientAccess.updateTransceiver(packet.blockPos, packet.editingFrequency, packet.frequency, packet.idc);
        });
    }
}


