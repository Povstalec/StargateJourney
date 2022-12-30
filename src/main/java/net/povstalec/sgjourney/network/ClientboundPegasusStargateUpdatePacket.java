package net.povstalec.sgjourney.network;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundPegasusStargateUpdatePacket
{
    public final BlockPos pos;
    public final int chevronsActive;
    public final boolean isBusy;
    public final int tick;
    public final String pointOfOrigin;
    public final int currentSymbol;
    public final int[] address;
    public final int symbolBuffer;
    public final int[] addressBuffer;

    public ClientboundPegasusStargateUpdatePacket(BlockPos pos, int chevronsActive, boolean isBusy, int tick, String pointOfOrigin, int currentSymbol, int[] address, int symbolBuffer, int[] addressBuffer)
    {
        this.pos = pos;
        this.chevronsActive = chevronsActive;
        this.isBusy = isBusy;
        this.tick = tick;
        this.pointOfOrigin = pointOfOrigin;
        this.currentSymbol = currentSymbol;
        this.address = address;
        this.symbolBuffer = symbolBuffer;
        this.addressBuffer = addressBuffer;
    }

    public ClientboundPegasusStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readBoolean(), buffer.readInt(), buffer.readUtf(), buffer.readInt(), buffer.readVarIntArray(), buffer.readInt(), buffer.readVarIntArray());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.chevronsActive);
        buffer.writeBoolean(this.isBusy);
        buffer.writeInt(this.tick);
        buffer.writeUtf(this.pointOfOrigin);
        buffer.writeInt(this.currentSymbol);
        buffer.writeVarIntArray(this.address);
        buffer.writeInt(this.symbolBuffer);
        buffer.writeVarIntArray(this.addressBuffer);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updatePegasusStargate(this.pos, this.chevronsActive, this.isBusy, this.tick, this.pointOfOrigin, this.currentSymbol, this.address, this.symbolBuffer, this.addressBuffer);
        });
        return true;
    }
}


