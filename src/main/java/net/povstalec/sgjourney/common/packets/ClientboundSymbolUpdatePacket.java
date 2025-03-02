package net.povstalec.sgjourney.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundSymbolUpdatePacket(BlockPos blockPos, int symbolNumber, ResourceLocation pointOfOrigin, ResourceLocation symbols) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundSymbolUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_symbol_update"));
    
    public static final StreamCodec<ByteBuf, ClientboundSymbolUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ClientboundSymbolUpdatePacket::blockPos,
            ByteBufCodecs.VAR_INT, ClientboundSymbolUpdatePacket::symbolNumber,
            ResourceLocation.STREAM_CODEC, ClientboundSymbolUpdatePacket::pointOfOrigin,
            ResourceLocation.STREAM_CODEC, ClientboundSymbolUpdatePacket::symbols,
            ClientboundSymbolUpdatePacket::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
    
    public static void handle(ClientboundSymbolUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
        	ClientAccess.updateSymbol(packet.blockPos, packet.symbolNumber, packet.pointOfOrigin, packet.symbols);
        });
    }
}


