package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundInterfaceUpdatePacket
{
    public final BlockPos pos;
    public final long energy;
    public final long energyTarget;

    public ClientboundInterfaceUpdatePacket(BlockPos pos, long energy, long energyTarget)
    {
        this.pos = pos;
        this.energy = energy;
        this.energyTarget = energyTarget;
    }

    public ClientboundInterfaceUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readLong(), buffer.readLong());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeLong(this.energy);
        buffer.writeLong(this.energyTarget);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateInterface(this.pos, this.energy, this.energyTarget);
        });
        return true;
    }
}


