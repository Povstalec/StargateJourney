package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundUniverseStargateUpdatePacket
{
    public final BlockPos pos;
    public final int symbolBuffer;
    public final int[] addressBuffer;

    public ClientboundUniverseStargateUpdatePacket(BlockPos pos, int symbolBuffer, int[] addressBuffer)
    {
        this.pos = pos;
        this.symbolBuffer = symbolBuffer;
        this.addressBuffer = addressBuffer;
    }

    public ClientboundUniverseStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readVarIntArray());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.symbolBuffer);
        buffer.writeVarIntArray(this.addressBuffer);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateUniverseStargate(this.pos, this.symbolBuffer, this.addressBuffer);
        });
        return true;
    }
}


