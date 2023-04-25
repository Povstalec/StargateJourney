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
    public final int animationTicks;
    public final int rotation;
    public final int oldRotation;

    public ClientboundUniverseStargateUpdatePacket(BlockPos pos, int symbolBuffer, int[] addressBuffer, int animationTicks, int rotation, int oldRotation)
    {
        this.pos = pos;
        this.symbolBuffer = symbolBuffer;
        this.addressBuffer = addressBuffer;
        this.animationTicks = animationTicks;
        this.rotation = rotation;
        this.oldRotation = oldRotation;
    }

    public ClientboundUniverseStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readVarIntArray(), buffer.readInt(), buffer.readInt(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.symbolBuffer);
        buffer.writeVarIntArray(this.addressBuffer);
        buffer.writeInt(this.animationTicks);
        buffer.writeInt(this.rotation);
        buffer.writeInt(this.oldRotation);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateUniverseStargate(this.pos, this.symbolBuffer, this.addressBuffer, this.animationTicks, this.rotation, this.oldRotation);
        });
        return true;
    }
}


