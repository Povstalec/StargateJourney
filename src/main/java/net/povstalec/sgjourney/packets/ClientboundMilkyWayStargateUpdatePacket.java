package net.povstalec.sgjourney.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundMilkyWayStargateUpdatePacket
{
    public final BlockPos pos;
    public final int rotation;
    public final boolean isChevronRaised;

    public ClientboundMilkyWayStargateUpdatePacket(BlockPos pos, int rotation, boolean isChevronRaised)
    {
        this.pos = pos;
        this.rotation = rotation;
        this.isChevronRaised = isChevronRaised;
    }

    public ClientboundMilkyWayStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.rotation);
        buffer.writeBoolean(this.isChevronRaised);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateMilkyWayStargate(this.pos, this.rotation, this.isChevronRaised);
        });
        return true;
    }
}


