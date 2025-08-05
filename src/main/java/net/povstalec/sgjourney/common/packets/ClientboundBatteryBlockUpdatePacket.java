package net.povstalec.sgjourney.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundBatteryBlockUpdatePacket(BlockPos blockPos, long energy) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundBatteryBlockUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_battery_block_update"));
    
    public static final StreamCodec<ByteBuf, ClientboundBatteryBlockUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ClientboundBatteryBlockUpdatePacket::blockPos,
            ByteBufCodecs.VAR_LONG, ClientboundBatteryBlockUpdatePacket::energy,
            ClientboundBatteryBlockUpdatePacket::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
    
    public static void handle(ClientboundBatteryBlockUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
            ClientAccess.updateBatteryBlock(packet.blockPos, packet.energy);
        });
    }
}


