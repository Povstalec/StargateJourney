package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundDHDUpdatePacket
{
    public final BlockPos pos;
    public final long energy;
    public final ResourceLocation pointOfOrigin;
    public final ResourceLocation symbols;
    public final int[] address;
    boolean isCenterButtonEngaged;

    public ClientboundDHDUpdatePacket(BlockPos pos, long energy, ResourceLocation pointOfOrigin, ResourceLocation symbols, int[] address, boolean isCenterButtonEngaged)
    {
        this.pos = pos;
        this.energy = energy;
        this.pointOfOrigin = pointOfOrigin;
        this.symbols = symbols;
        this.address = address;
        this.isCenterButtonEngaged = isCenterButtonEngaged;
    }

    public ClientboundDHDUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readLong(), buffer.readResourceLocation(), buffer.readResourceLocation(), buffer.readVarIntArray(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeLong(this.energy);
        buffer.writeResourceLocation(this.symbols);
        buffer.writeResourceLocation(this.symbols);
        buffer.writeVarIntArray(this.address);
        buffer.writeBoolean(this.isCenterButtonEngaged);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateDHD(pos, energy, pointOfOrigin, symbols, address, isCenterButtonEngaged);
        });
        return true;
    }
}


