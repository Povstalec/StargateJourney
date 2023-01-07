package net.povstalec.sgjourney.network;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundMilkyWayStargateUpdatePacket
{
    public final BlockPos pos;
    public final short degrees;
    public final boolean isChevronRaised;

    public ClientboundMilkyWayStargateUpdatePacket(BlockPos pos, short degrees, boolean isChevronRaised)
    {
        this.pos = pos;
        this.degrees = degrees;
        this.isChevronRaised = isChevronRaised;
    }

    public ClientboundMilkyWayStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readShort(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeShort(this.degrees);
        buffer.writeBoolean(this.isChevronRaised);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateMilkyWayStargate(this.pos, this.degrees, this.isChevronRaised);
        });
        return true;
    }
}


