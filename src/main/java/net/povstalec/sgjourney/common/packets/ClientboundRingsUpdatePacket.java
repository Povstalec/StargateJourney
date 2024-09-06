package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundRingsUpdatePacket
{
    public final BlockPos pos;
    public final int emptySpace;
    public final int transportHeight;

    public ClientboundRingsUpdatePacket(BlockPos pos, int emptySpace, int transportHeight)
    {
        this.pos = pos;
        this.emptySpace = emptySpace;
        this.transportHeight = transportHeight;
    }

    public ClientboundRingsUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeInt(emptySpace);
        buffer.writeInt(transportHeight);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateRings(pos, emptySpace, transportHeight);
        });
        return true;
    }
}


