package net.povstalec.sgjourney.network;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundStargateUpdatePacket
{
    public final BlockPos pos;
    public final short degrees;
    public final int chevronsActive;
    public final boolean isChevronRaised;
    public final boolean isBusy;
    public final int tick;
    public final String pointOfOrigin;
    public final String symbols;
    public final int currentSymbol;

    public ClientboundStargateUpdatePacket(BlockPos pos, short degrees, int chevronsActive, boolean isChevronRaised, boolean isBusy, int tick, String pointOfOrigin, String symbols, int currentSymbol)
    {
        this.pos = pos;
        this.degrees = degrees;
        this.chevronsActive = chevronsActive;
        this.isChevronRaised = isChevronRaised;
        this.isBusy = isBusy;
        this.tick = tick;
        this.pointOfOrigin = pointOfOrigin;
        this.symbols = symbols;
        this.currentSymbol = currentSymbol;
    }

    public ClientboundStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readShort(), buffer.readInt(), buffer.readBoolean(), buffer.readBoolean(), buffer.readInt(), buffer.readUtf(), buffer.readUtf(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeShort(this.degrees);
        buffer.writeInt(this.chevronsActive);
        buffer.writeBoolean(this.isChevronRaised);
        buffer.writeBoolean(this.isBusy);
        buffer.writeInt(this.tick);
        buffer.writeUtf(this.pointOfOrigin);
        buffer.writeUtf(this.symbols);
        buffer.writeInt(this.currentSymbol);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateStargate(this.pos, this.degrees, this.chevronsActive, this.isChevronRaised, this.isBusy, this.tick, this.pointOfOrigin, this.symbols, this.currentSymbol);
        });
        return true;
    }
}


