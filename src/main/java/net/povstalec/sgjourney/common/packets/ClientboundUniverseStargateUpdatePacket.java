package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundUniverseStargateUpdatePacket(BlockPos blockPos, int symbolBuffer, int[] addressBuffer) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundUniverseStargateUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_universe_stargate_update"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundUniverseStargateUpdatePacket> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ClientboundUniverseStargateUpdatePacket>()
    {
        public ClientboundUniverseStargateUpdatePacket decode(RegistryFriendlyByteBuf buf)
        {
            return new ClientboundUniverseStargateUpdatePacket(FriendlyByteBuf.readBlockPos(buf), buf.readInt(), buf.readVarIntArray());
        }
        
        public void encode(RegistryFriendlyByteBuf buf, ClientboundUniverseStargateUpdatePacket packet)
        {
            FriendlyByteBuf.writeBlockPos(buf, packet.blockPos);
            buf.writeInt(packet.symbolBuffer);
            buf.writeVarIntArray(packet.addressBuffer);
            
        }
    };
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundUniverseStargateUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> ClientAccess.updateUniverseStargate(packet.blockPos, packet.symbolBuffer, packet.addressBuffer));
    }
}


