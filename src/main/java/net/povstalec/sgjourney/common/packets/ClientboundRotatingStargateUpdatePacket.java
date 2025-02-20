package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

import java.util.function.Supplier;

public class ClientboundRotatingStargateUpdatePacket
{
    public final BlockPos pos;
    public final int rotation;
    public final int oldRotation;
    public final int signalStrength;
    public final boolean computerRotation;
    public final boolean rotateClockwise;
    public final int desiredRotation;
    
    public ClientboundRotatingStargateUpdatePacket(BlockPos pos, int rotation, int oldRotation, int signalStrength,boolean computerRotation,
                                                   boolean rotateClockwise, int desiredRotation)
    {
        this.pos = pos;
        this.rotation = rotation;
        this.oldRotation = oldRotation;
        this.signalStrength = signalStrength;
        this.computerRotation = computerRotation;
        this.rotateClockwise = rotateClockwise;
        this.desiredRotation = desiredRotation;
    }

    public ClientboundRotatingStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readInt(), buffer.readInt(), buffer.readBoolean(), buffer.readBoolean(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.rotation);
        buffer.writeInt(this.oldRotation);
        buffer.writeInt(this.signalStrength);
        buffer.writeBoolean(this.computerRotation);
        buffer.writeBoolean(this.rotateClockwise);
        buffer.writeInt(this.desiredRotation);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
        	ClientAccess.updateRotatingStargate(this.pos, this.rotation, this.oldRotation, this.signalStrength, this.computerRotation, this.rotateClockwise, this.desiredRotation);
        });
        return true;
    }
}


