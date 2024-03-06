package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundSymbolUpdatePacket
{
    public final BlockPos pos;
    public final int symbolNumber;
    public final String pointOfOrigin;
    public final String symbols;

    public ClientboundSymbolUpdatePacket(BlockPos pos, int symbolNumber, String pointOfOrigin, String symbols)
    {
        this.pos = pos;
        this.symbolNumber = symbolNumber;
        this.pointOfOrigin = pointOfOrigin;
        this.symbols = symbols;
    }

    public ClientboundSymbolUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readUtf(), buffer.readUtf());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.symbolNumber);
        buffer.writeUtf(this.pointOfOrigin);
        buffer.writeUtf(this.symbols);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateSymbol(this.pos, this.symbolNumber, this.pointOfOrigin, this.symbols);
        });
        return true;
    }
}


