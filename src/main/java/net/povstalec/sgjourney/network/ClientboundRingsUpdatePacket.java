package net.povstalec.sgjourney.network;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundRingsUpdatePacket
{
    public final BlockPos pos;
    public final int ticks;
    public final int emptySpace;
    public final int progress;
    public final int transportHeight;
    public final int transportLight;

    public ClientboundRingsUpdatePacket(BlockPos pos, int ticks, int emptySpace, int progress, int transportHeight, int transportLight)
    {
        this.pos = pos;
        this.ticks = ticks;
        this.emptySpace = emptySpace;
        this.progress = progress;
        this.transportHeight = transportHeight;
        this.transportLight = transportLight;
    }

    public ClientboundRingsUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeInt(ticks);
        buffer.writeInt(emptySpace);
        buffer.writeInt(progress);
        buffer.writeInt(transportHeight);
        buffer.writeInt(transportLight);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateRings(pos, ticks, emptySpace, progress, transportHeight, transportLight);
        });
        return true;
    }
}


