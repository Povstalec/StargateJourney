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
    public final int progress;

    public ClientboundRingsUpdatePacket(BlockPos pos, int emptySpace, int transportHeight, int progress)
    {
        this.pos = pos;
        this.emptySpace = emptySpace;
        this.transportHeight = transportHeight;
        this.progress = progress;
    }

    public ClientboundRingsUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeInt(emptySpace);
        buffer.writeInt(transportHeight);
        buffer.writeInt(progress);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateRings(pos, emptySpace, transportHeight, progress);
        });
        return true;
    }
}


