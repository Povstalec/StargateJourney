package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundDHDUpdatePacket
{
    public final BlockPos pos;
    public final String symbols;
    public final int[] address;
    boolean isCenterButtonEngaged;

    public ClientboundDHDUpdatePacket(BlockPos pos, String symbols, int[] address, boolean isCenterButtonEngaged)
    {
        this.pos = pos;
        this.symbols = symbols;
        this.address = address;
        this.isCenterButtonEngaged = isCenterButtonEngaged;
    }

    public ClientboundDHDUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readUtf(), buffer.readVarIntArray(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeUtf(this.symbols);
        buffer.writeVarIntArray(this.address);
        buffer.writeBoolean(this.isCenterButtonEngaged);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateDHD(pos, symbols, address, isCenterButtonEngaged);
        });
        return true;
    }
}


