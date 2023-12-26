package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundNaquadahLiquidizerUpdatePacket
{
    public final BlockPos pos;
    private final FluidStack fluidStack1;
    private final FluidStack fluidStack2;
    public final int progress;

    public ClientboundNaquadahLiquidizerUpdatePacket(BlockPos pos, FluidStack fluidStack1, FluidStack fluidStack2, int progress)
    {
        this.pos = pos;
        this.fluidStack1 = fluidStack1;
        this.fluidStack2 = fluidStack2;
        this.progress = progress;
    }

    public ClientboundNaquadahLiquidizerUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readFluidStack(), buffer.readFluidStack(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeFluidStack(this.fluidStack1);
        buffer.writeFluidStack(this.fluidStack2);
        buffer.writeInt(this.progress);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateNaquadahLiquidizer(pos, fluidStack1, fluidStack2, progress);
        });
        return true;
    }
}


