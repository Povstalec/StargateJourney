package net.povstalec.sgjourney.network;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundPegasusStargateUpdatePacket
{
    public final BlockPos pos;
    public final int[] address;
    public final int symbolBuffer;
    public final int[] addressBuffer;

    public ClientboundPegasusStargateUpdatePacket(BlockPos pos, int[] address, int symbolBuffer, int[] addressBuffer)
    {
        this.pos = pos;
        this.address = address;
        this.symbolBuffer = symbolBuffer;
        this.addressBuffer = addressBuffer;
    }

    public ClientboundPegasusStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readVarIntArray(), buffer.readInt(), buffer.readVarIntArray());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeVarIntArray(this.address);
        buffer.writeInt(this.symbolBuffer);
        buffer.writeVarIntArray(this.addressBuffer);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updatePegasusStargate(this.pos, this.address, this.symbolBuffer, this.addressBuffer);
        });
        return true;
    }
}


