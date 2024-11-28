package net.povstalec.sgjourney.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundNaquadahGeneratorUpdatePacket(BlockPos blockPos, int reactionProgress, long energy) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundNaquadahGeneratorUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_naquadah_generator_update"));
    
    public static final StreamCodec<ByteBuf, ClientboundNaquadahGeneratorUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ClientboundNaquadahGeneratorUpdatePacket::blockPos,
            ByteBufCodecs.VAR_INT, ClientboundNaquadahGeneratorUpdatePacket::reactionProgress,
            ByteBufCodecs.VAR_LONG, ClientboundNaquadahGeneratorUpdatePacket::energy,
            ClientboundNaquadahGeneratorUpdatePacket::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundNaquadahGeneratorUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
        	ClientAccess.updateNaquadahGenerator(packet.blockPos, packet.reactionProgress, packet.energy);
        });
    }
}


