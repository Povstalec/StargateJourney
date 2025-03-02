package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundRotatingStargateUpdatePacket(BlockPos blockPos, int rotation, int oldRotation, int signalStrength, boolean computerRotation, boolean rotateClockwise, int desiredRotation) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundRotatingStargateUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_rotating_stargate_update"));
    
    public ClientboundRotatingStargateUpdatePacket(BlockPos blockPos, int rotation, int oldRotation, int signalStrength,boolean computerRotation,
                                                   boolean rotateClockwise, int desiredRotation)
    {
        this.blockPos = blockPos;
        this.rotation = rotation;
        this.oldRotation = oldRotation;
        this.signalStrength = signalStrength;
        this.computerRotation = computerRotation;
        this.rotateClockwise = rotateClockwise;
        this.desiredRotation = desiredRotation;
    }
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundRotatingStargateUpdatePacket> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ClientboundRotatingStargateUpdatePacket>()
    {
        public ClientboundRotatingStargateUpdatePacket decode(RegistryFriendlyByteBuf buf)
        {
            return new ClientboundRotatingStargateUpdatePacket(FriendlyByteBuf.readBlockPos(buf), buf.readInt(), buf.readInt(), buf.readInt(), buf.readBoolean(), buf.readBoolean(), buf.readInt());
        }
        
        public void encode(RegistryFriendlyByteBuf buf, ClientboundRotatingStargateUpdatePacket packet)
        {
            FriendlyByteBuf.writeBlockPos(buf, packet.blockPos);
            buf.writeInt(packet.rotation);
            buf.writeInt(packet.oldRotation);
            buf.writeInt(packet.signalStrength);
            buf.writeBoolean(packet.computerRotation);
            buf.writeBoolean(packet.rotateClockwise);
            buf.writeInt(packet.desiredRotation);
            
        }
    };
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }
    
    public static void handle(ClientboundRotatingStargateUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> ClientAccess.updateRotatingStargate(packet.blockPos, packet.rotation, packet.oldRotation, packet.signalStrength, packet.computerRotation, packet.rotateClockwise, packet.desiredRotation));
    }
}


