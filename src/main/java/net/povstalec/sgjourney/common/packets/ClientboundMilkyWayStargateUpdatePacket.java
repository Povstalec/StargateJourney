package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundMilkyWayStargateUpdatePacket
{
    public final BlockPos pos;
    public final boolean isChevronOpen;

    public ClientboundMilkyWayStargateUpdatePacket(BlockPos pos, boolean isChevronOpen)
    {
        this.pos = pos;
        this.isChevronOpen = isChevronOpen;
    }

    public ClientboundMilkyWayStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeBoolean(this.isChevronOpen);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
        	ClientAccess.updateMilkyWayStargate(this.pos, this.isChevronOpen);
        });
        return true;
    }
}


