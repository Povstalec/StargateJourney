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

public record ClientboundNaquadahLiquidizerUpdatePacket(BlockPos blockPos, FluidStack fluidStack1, FluidStack fluidStack2, int progress) implements CustomPacketPayload
{
    public static final CustomPacketPayload.Type<ClientboundNaquadahLiquidizerUpdatePacket> TYPE =
            new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("s2c_naquadah_liquidizer_update"));
    
    public static final StreamCodec<RegistryFriendlyByteBuf, ClientboundNaquadahLiquidizerUpdatePacket> STREAM_CODEC = StreamCodec.composite(
            BlockPos.STREAM_CODEC, ClientboundNaquadahLiquidizerUpdatePacket::blockPos,
            FluidStack.STREAM_CODEC, ClientboundNaquadahLiquidizerUpdatePacket::fluidStack1,
            FluidStack.STREAM_CODEC, ClientboundNaquadahLiquidizerUpdatePacket::fluidStack2,
            ByteBufCodecs.VAR_INT, ClientboundNaquadahLiquidizerUpdatePacket::progress,
            ClientboundNaquadahLiquidizerUpdatePacket::new
    );
    
    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
    {
        return TYPE;
    }

    public static void handle(ClientboundNaquadahLiquidizerUpdatePacket packet, IPayloadContext ctx)
    {
        ctx.enqueueWork(() -> {
        	ClientAccess.updateNaquadahLiquidizer(packet.blockPos, packet.fluidStack1, packet.fluidStack2, packet.progress);
        });
    }
}


