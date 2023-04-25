package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundNaquadahGeneratorUpdatePacket
{
    public final BlockPos pos;
    public final int reactionProgress;
    public final long energy;

    public ClientboundNaquadahGeneratorUpdatePacket(BlockPos pos, int reactionProgress, long energy)
    {
        this.pos = pos;
        this.reactionProgress = reactionProgress;
        this.energy = energy;
    }

    public ClientboundNaquadahGeneratorUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readLong());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.reactionProgress);
        buffer.writeLong(this.energy);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateNaquadahGenerator(this.pos, this.reactionProgress, this.energy);
        });
        return true;
    }
}


