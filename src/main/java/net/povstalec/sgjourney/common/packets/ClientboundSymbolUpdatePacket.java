package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundSymbolUpdatePacket
{
    public final BlockPos pos;
    public final int symbolNumber;
    public final ResourceLocation pointOfOrigin;
    public final ResourceLocation symbols;

    public ClientboundSymbolUpdatePacket(BlockPos pos, int symbolNumber, ResourceLocation pointOfOrigin, ResourceLocation symbols)
    {
        this.pos = pos;
        this.symbolNumber = symbolNumber;
        this.pointOfOrigin = pointOfOrigin;
        this.symbols = symbols;
    }

    public ClientboundSymbolUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readResourceLocation(), buffer.readResourceLocation());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.symbolNumber);
        buffer.writeResourceLocation(this.pointOfOrigin);
        buffer.writeResourceLocation(this.symbols);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateSymbol(this.pos, this.symbolNumber, this.pointOfOrigin, this.symbols);
        });
        return true;
    }
}


