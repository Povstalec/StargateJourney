package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.minecraft.resources.ResourceLocation;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundDHDUpdatePacket(BlockPos blockPos, long energy, ResourceLocation pointOfOrigin, ResourceLocation symbols, int[] address, boolean isCenterButtonEngaged) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundDHDUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_dhd_update"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundDHDUpdatePacket> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ClientboundDHDUpdatePacket>()
    {
        public ClientboundDHDUpdatePacket decode(RegistryFriendlyByteBuf buf)
        {
            return new ClientboundDHDUpdatePacket(FriendlyByteBuf.readBlockPos(buf), buf.readLong(), buf.readResourceLocation(), buf.readResourceLocation(), buf.readVarIntArray(), buf.readBoolean());
        }
        
        public void encode(RegistryFriendlyByteBuf buf, ClientboundDHDUpdatePacket packet)
        {
            FriendlyByteBuf.writeBlockPos(buf, packet.blockPos);
            buf.writeLong(packet.energy);
            buf.writeResourceLocation(packet.symbols);
            buf.writeResourceLocation(packet.symbols);
            buf.writeVarIntArray(packet.address);
            buf.writeBoolean(packet.isCenterButtonEngaged);
            
        }
    };
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundDHDUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
        	ClientAccess.updateDHD(packet.blockPos, packet.energy, packet.pointOfOrigin, packet.symbols, packet.address, packet.isCenterButtonEngaged);
        });
    }
}


