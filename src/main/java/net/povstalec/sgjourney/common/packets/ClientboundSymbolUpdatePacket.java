package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundSymbolUpdatePacket
{
    public final BlockPos pos;
    public final String symbol;

    public ClientboundSymbolUpdatePacket(BlockPos pos, String symbol)
    {
        this.pos = pos;
        this.symbol = symbol;
    }

    public ClientboundSymbolUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readUtf());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeUtf(this.symbol);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateSymbol(this.pos, this.symbol);
        });
        return true;
    }
}


