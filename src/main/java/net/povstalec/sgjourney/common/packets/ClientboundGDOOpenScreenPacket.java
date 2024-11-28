package net.povstalec.sgjourney.common.packets;

import java.util.UUID;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundGDOOpenScreenPacket(UUID playerId, boolean mainHand, String idc, int frequency) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundGDOOpenScreenPacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_gdo_open_screen"));
    
    public static final StreamCodec<ByteBuf, ClientboundGDOOpenScreenPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, ClientboundGDOOpenScreenPacket::playerId,
            ByteBufCodecs.BOOL, ClientboundGDOOpenScreenPacket::mainHand,
            ByteBufCodecs.STRING_UTF8, ClientboundGDOOpenScreenPacket::idc,
            ByteBufCodecs.VAR_INT, ClientboundGDOOpenScreenPacket::frequency,
            ClientboundGDOOpenScreenPacket::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundGDOOpenScreenPacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
        	ClientAccess.openGDOScreen(packet.playerId, packet.mainHand, packet.idc, packet.frequency);
        });
    }
}


