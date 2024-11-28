package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundCartoucheUpdatePacket(BlockPos blockPos, String symbols, int[] address) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundCartoucheUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_cartouche_update"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCartoucheUpdatePacket> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ClientboundCartoucheUpdatePacket>()
    {
        public ClientboundCartoucheUpdatePacket decode(RegistryFriendlyByteBuf buf)
        {
            return new ClientboundCartoucheUpdatePacket(FriendlyByteBuf.readBlockPos(buf), buf.readUtf(), buf.readVarIntArray());
        }
        
        public void encode(RegistryFriendlyByteBuf buf, ClientboundCartoucheUpdatePacket packet)
        {
            FriendlyByteBuf.writeBlockPos(buf, packet.blockPos);
            buf.writeUtf(packet.symbols);
            buf.writeVarIntArray(packet.address);
        }
    };
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundCartoucheUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
        	ClientAccess.updateCartouche(packet.blockPos, packet.symbols, packet.address);
        });
    }
}


