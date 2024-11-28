package net.povstalec.sgjourney.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundInterfaceUpdatePacket(BlockPos blockPos, long energy) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundInterfaceUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_interface_update"));
    
    public static final StreamCodec<ByteBuf, ClientboundInterfaceUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ClientboundInterfaceUpdatePacket::blockPos,
            ByteBufCodecs.VAR_LONG, ClientboundInterfaceUpdatePacket::energy,
            ClientboundInterfaceUpdatePacket::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundInterfaceUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
        	ClientAccess.updateInterface(packet.blockPos, packet.energy);
        });
    }
}


