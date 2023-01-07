package net.povstalec.sgjourney.network;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundStargateUpdatePacket
{
    public final BlockPos pos;
    public final int chevronsActive;
    public final boolean isBusy;
    public final int tick;
    public final String pointOfOrigin;
    public final String symbols;
    public final int currentSymbol;

    public ClientboundStargateUpdatePacket(BlockPos pos,int chevronsActive, boolean isBusy, int tick, String pointOfOrigin, String symbols, int currentSymbol)
    {
        this.pos = pos;
        this.chevronsActive = chevronsActive;
        this.isBusy = isBusy;
        this.tick = tick;
        this.pointOfOrigin = pointOfOrigin;
        this.symbols = symbols;
        this.currentSymbol = currentSymbol;
    }

    public ClientboundStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readBoolean(), buffer.readInt(), buffer.readUtf(), buffer.readUtf(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.chevronsActive);
        buffer.writeBoolean(this.isBusy);
        buffer.writeInt(this.tick);
        buffer.writeUtf(this.pointOfOrigin);
        buffer.writeUtf(this.symbols);
        buffer.writeInt(this.currentSymbol);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateStargate(this.pos, this.chevronsActive, this.isBusy, this.tick, this.pointOfOrigin, this.symbols, this.currentSymbol);
        });
        return true;
    }
}


