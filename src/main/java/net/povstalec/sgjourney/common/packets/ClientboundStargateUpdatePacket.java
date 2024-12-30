package net.povstalec.sgjourney.common.packets;

import java.util.List;
import java.util.function.Supplier;

import io.netty.buffer.ByteBuf;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.Utf8String;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundStargateUpdatePacket(BlockPos blockPos, int[] address, int[] engagedChevrons, int kawooshTick, int tick,
                                              short irisProgress, String pointOfOrigin, String symbols, ResourceLocation variant, ItemStack iris) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundStargateUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_stargate_update"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundStargateUpdatePacket> STREAM_CODEC = new StreamCodec<RegistryFriendlyByteBuf, ClientboundStargateUpdatePacket>()
    {
        public ClientboundStargateUpdatePacket decode(RegistryFriendlyByteBuf buf)
        {
            return new ClientboundStargateUpdatePacket(FriendlyByteBuf.readBlockPos(buf), buf.readVarIntArray(), buf.readVarIntArray(), buf.readInt(), buf.readInt(),
                    buf.readShort(), buf.readUtf(), buf.readUtf(), buf.readResourceLocation(), ItemStack.STREAM_CODEC.decode(buf));
        }
        
        public void encode(RegistryFriendlyByteBuf buf, ClientboundStargateUpdatePacket packet)
        {
            FriendlyByteBuf.writeBlockPos(buf, packet.blockPos);
            buf.writeVarIntArray(packet.address);
            buf.writeVarIntArray(packet.engagedChevrons);
            buf.writeInt(packet.kawooshTick);
            buf.writeInt(packet.tick);
            buf.writeShort(packet.irisProgress);
            buf.writeUtf(packet.pointOfOrigin);
            buf.writeUtf(packet.symbols);
            buf.writeResourceLocation(packet.variant);
            ItemStack.STREAM_CODEC.encode(buf, packet.iris);
            
        }
    };
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundStargateUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
        	ClientAccess.updateStargate(packet.blockPos, packet.address, packet.engagedChevrons, packet.kawooshTick, packet.tick,
                    packet.irisProgress, packet.pointOfOrigin, packet.symbols, packet.variant, packet.iris);
        });
    }
}


