package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

import java.util.function.Supplier;

public class ClientboundTollanStargateUpdatePacket
{
    public final BlockPos pos;
    public final int[] addressBuffer;
    public final int currentSymbol;

    public ClientboundTollanStargateUpdatePacket(BlockPos pos, int[] addressBuffer, int currentSymbol)
    {
        this.pos = pos;
        this.addressBuffer = addressBuffer;
        this.currentSymbol = currentSymbol;
    }

    public ClientboundTollanStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readVarIntArray(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeVarIntArray(this.addressBuffer);
        buffer.writeInt(this.currentSymbol);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateTollanStargate(this.pos, this.addressBuffer, this.currentSymbol);
        });
        return true;
    }
}


