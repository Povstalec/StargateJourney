package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundCrystallizerUpdatePacket
{
    public final BlockPos pos;
    private final FluidStack fluidStack;
    public final int progress;

    public ClientboundCrystallizerUpdatePacket(BlockPos pos, FluidStack fluidStack, int progress)
    {
        this.pos = pos;
        this.fluidStack = fluidStack;
        this.progress = progress;
    }

    public ClientboundCrystallizerUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readFluidStack(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeFluidStack(this.fluidStack);
        buffer.writeInt(this.progress);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateCrystallizer(this.pos, this.fluidStack, this.progress);
        });
        return true;
    }
}


