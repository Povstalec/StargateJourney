package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundCartoucheUpdatePacket
{
    public final BlockPos pos;
    public final String symbols;
    public final int[] address;

    public ClientboundCartoucheUpdatePacket(BlockPos pos, String symbols, int[] address)
    {
        this.pos = pos;
        this.symbols = symbols;
        this.address = address;
    }

    public ClientboundCartoucheUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readUtf(), buffer.readVarIntArray());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeUtf(this.symbols);
        buffer.writeVarIntArray(this.address);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateCartouche(pos, symbols, address);
        });
        return true;
    }
}


