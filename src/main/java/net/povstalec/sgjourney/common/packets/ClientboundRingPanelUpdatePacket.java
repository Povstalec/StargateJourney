package net.povstalec.sgjourney.common.packets;

import java.util.List;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundRingPanelUpdatePacket(BlockPos blockPos, List<BlockPos> ringsPos, List<Component> ringsName) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundRingPanelUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_ring_panel_update"));
    
    public static final StreamCodec<ByteBuf, ClientboundRingPanelUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ClientboundRingPanelUpdatePacket::blockPos,
            BlockPos.STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundRingPanelUpdatePacket::ringsPos,
            ComponentSerialization.TRUSTED_CONTEXT_FREE_STREAM_CODEC.apply(ByteBufCodecs.list()), ClientboundRingPanelUpdatePacket::ringsName,
            ClientboundRingPanelUpdatePacket::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundRingPanelUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
        	ClientAccess.updateRingPanel(packet.blockPos, packet.ringsPos, packet.ringsName);
        });
    }
}


