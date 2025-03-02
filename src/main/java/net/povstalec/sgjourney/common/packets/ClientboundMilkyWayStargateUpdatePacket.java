package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundMilkyWayStargateUpdatePacket(BlockPos pos, boolean isChevronOpen) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundMilkyWayStargateUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_milky_way_stargate_update"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundMilkyWayStargateUpdatePacket> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ClientboundMilkyWayStargateUpdatePacket>()
    {
        public ClientboundMilkyWayStargateUpdatePacket decode(RegistryFriendlyByteBuf buf)
        {
            return new ClientboundMilkyWayStargateUpdatePacket(FriendlyByteBuf.readBlockPos(buf), buf.readBoolean());
        }
        
        public void encode(RegistryFriendlyByteBuf buf, ClientboundMilkyWayStargateUpdatePacket packet)
        {
            FriendlyByteBuf.writeBlockPos(buf, packet.pos);
            buf.writeBoolean(packet.isChevronOpen);
            
        }
    };
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundMilkyWayStargateUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> ClientAccess.updateMilkyWayStargate(packet.pos, packet.isChevronOpen));
    }
}


