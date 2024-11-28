package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.neoforged.neoforge.fluids.FluidStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.client.ClientAccess;

public record ClientboundCrystallizerUpdatePacket(BlockPos blockPos, FluidStack fluidStack, int progress) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundCrystallizerUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_crystallizer_update"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundCrystallizerUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ClientboundCrystallizerUpdatePacket::blockPos,
            FluidStack.STREAM_CODEC, ClientboundCrystallizerUpdatePacket::fluidStack,
            ByteBufCodecs.VAR_INT, ClientboundCrystallizerUpdatePacket::progress,
            ClientboundCrystallizerUpdatePacket::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundCrystallizerUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
        	ClientAccess.updateCrystallizer(packet.blockPos, packet.fluidStack, packet.progress);
        });
    }
}


