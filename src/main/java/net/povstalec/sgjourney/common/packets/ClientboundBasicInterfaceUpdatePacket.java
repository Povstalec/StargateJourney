package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundBasicInterfaceUpdatePacket
{
    public final BlockPos pos;
    public final long energy;

    public ClientboundBasicInterfaceUpdatePacket(BlockPos pos, long energy)
    {
        this.pos = pos;
        this.energy = energy;
    }

    public ClientboundBasicInterfaceUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readLong());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeLong(this.energy);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateBasicInterface(this.pos, this.energy);
        });
        return true;
    }
}


