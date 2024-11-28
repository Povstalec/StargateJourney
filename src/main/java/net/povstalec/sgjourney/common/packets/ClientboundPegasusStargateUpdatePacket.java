package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundPegasusStargateUpdatePacket(BlockPos blockPos, int symbolBuffer, int[] addressBuffer, int currentSymbol) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundPegasusStargateUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_pegasus_stargate_update"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundPegasusStargateUpdatePacket> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ClientboundPegasusStargateUpdatePacket>()
    {
        public ClientboundPegasusStargateUpdatePacket decode(RegistryFriendlyByteBuf buf)
        {
            return new ClientboundPegasusStargateUpdatePacket(buf.readBlockPos(), buf.readInt(), buf.readVarIntArray(), buf.readInt());
        }
        
        public void encode(RegistryFriendlyByteBuf buf, ClientboundPegasusStargateUpdatePacket packet)
        {
            buf.writeBlockPos(packet.blockPos);
            buf.writeInt(packet.symbolBuffer);
            buf.writeVarIntArray(packet.addressBuffer);
            buf.writeInt(packet.currentSymbol);
            
        }
    };
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundPegasusStargateUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
        	ClientAccess.updatePegasusStargate(packet.blockPos, packet.symbolBuffer, packet.addressBuffer, packet.currentSymbol);
        });
    }
}


