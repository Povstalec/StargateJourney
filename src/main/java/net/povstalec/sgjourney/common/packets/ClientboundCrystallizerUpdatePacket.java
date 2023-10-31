package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundCrystallizerUpdatePacket
{
    public final BlockPos pos;
    public final int fluidAmount;

    public ClientboundCrystallizerUpdatePacket(BlockPos pos, int fluidAmount)
    {
        this.pos = pos;
        this.fluidAmount = fluidAmount;
    }

    public ClientboundCrystallizerUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.fluidAmount);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateCrystallizer(this.pos, this.fluidAmount);
        });
        return true;
    }
}


