package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundDialerUpdatePacket
{
    public final BlockPos pos;

    public ClientboundDialerUpdatePacket(BlockPos pos)
    {
        this.pos = pos;
    }

    public ClientboundDialerUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateDialer(pos);
        });
        return true;
    }
}


