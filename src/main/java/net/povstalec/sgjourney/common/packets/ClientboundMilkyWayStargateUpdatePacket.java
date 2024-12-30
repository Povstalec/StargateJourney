package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundMilkyWayStargateUpdatePacket(BlockPos blockPos, int rotation, int oldRotation, boolean isChevronRaised, int signalStrength,
                                                      boolean computerRotation, boolean rotateClockwise, int desiredSymbol) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundMilkyWayStargateUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_milky_way_stargate_update"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundMilkyWayStargateUpdatePacket> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ClientboundMilkyWayStargateUpdatePacket>()
    {
        public ClientboundMilkyWayStargateUpdatePacket decode(RegistryFriendlyByteBuf buf)
        {
            return new ClientboundMilkyWayStargateUpdatePacket(FriendlyByteBuf.readBlockPos(buf), buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readInt(),
                    buf.readBoolean(), buf.readBoolean(), buf.readInt());
        }
        
        public void encode(RegistryFriendlyByteBuf buf, ClientboundMilkyWayStargateUpdatePacket packet)
        {
            FriendlyByteBuf.writeBlockPos(buf, packet.blockPos);
            buf.writeInt(packet.rotation);
            buf.writeInt(packet.oldRotation);
            buf.writeBoolean(packet.isChevronRaised);
            buf.writeInt(packet.signalStrength);
            buf.writeBoolean(packet.computerRotation);
            buf.writeBoolean(packet.rotateClockwise);
            buf.writeInt(packet.desiredSymbol);
            
        }
    };
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundMilkyWayStargateUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() ->
        {
        	ClientAccess.updateMilkyWayStargate(packet.blockPos, packet.rotation, packet.oldRotation, packet.isChevronRaised, packet.signalStrength, packet.computerRotation, packet.rotateClockwise, packet.desiredSymbol);
        });
    }
}


