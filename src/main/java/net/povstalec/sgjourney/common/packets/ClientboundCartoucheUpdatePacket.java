package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundCartoucheUpdatePacket
{
    public final BlockPos pos;
    public final ResourceLocation symbols;
    public final int[] address;

    public ClientboundCartoucheUpdatePacket(BlockPos pos, ResourceLocation symbols, int[] address)
    {
        this.pos = pos;
        this.symbols = symbols;
        this.address = address;
    }

    public ClientboundCartoucheUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readResourceLocation(), buffer.readVarIntArray());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeResourceLocation(this.symbols);
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


