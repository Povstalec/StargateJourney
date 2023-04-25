package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundPegasusStargateUpdatePacket
{
    public final BlockPos pos;
    public final int symbolBuffer;
    public final int[] addressBuffer;
    public final int currentSymbol;

    public ClientboundPegasusStargateUpdatePacket(BlockPos pos, int symbolBuffer, int[] addressBuffer, int currentSymbol)
    {
        this.pos = pos;
        this.symbolBuffer = symbolBuffer;
        this.addressBuffer = addressBuffer;
        this.currentSymbol = currentSymbol;
    }

    public ClientboundPegasusStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readVarIntArray(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.symbolBuffer);
        buffer.writeVarIntArray(this.addressBuffer);
        buffer.writeInt(this.currentSymbol);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updatePegasusStargate(this.pos, this.symbolBuffer, this.addressBuffer, this.currentSymbol);
        });
        return true;
    }
}


