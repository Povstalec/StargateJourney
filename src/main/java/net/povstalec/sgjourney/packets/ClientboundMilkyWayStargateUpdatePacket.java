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
    public final int oldRotation;
    public final boolean isChevronRaised;
    public final int signalStrength;

    public ClientboundMilkyWayStargateUpdatePacket(BlockPos pos, int rotation, int oldRotation, boolean isChevronRaised, int signalStrength)
    {
        this.pos = pos;
        this.rotation = rotation;
        this.oldRotation = oldRotation;
        this.isChevronRaised = isChevronRaised;
        this.signalStrength = signalStrength;
    }

    public ClientboundMilkyWayStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readInt(), buffer.readBoolean(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.rotation);
        buffer.writeInt(this.oldRotation);
        buffer.writeBoolean(this.isChevronRaised);
        buffer.writeInt(this.signalStrength);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
        	ClientAccess.updateMilkyWayStargate(this.pos, this.rotation, this.oldRotation, this.isChevronRaised, this.signalStrength);
        });
        return true;
    }
}


