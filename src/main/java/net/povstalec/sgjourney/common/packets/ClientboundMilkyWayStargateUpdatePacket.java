package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundMilkyWayStargateUpdatePacket
{
    public final BlockPos pos;
    public final int rotation;
    public final int oldRotation;
    public final boolean isChevronRaised;
    public final int signalStrength;
    public final boolean computerRotation;
    public final boolean rotateClockwise;
    public final int desiredSymbol;

    public ClientboundMilkyWayStargateUpdatePacket(BlockPos pos, int rotation, int oldRotation, boolean isChevronRaised, int signalStrength, boolean computerRotation, boolean rotateClockwise, int desiredSymbol)
    {
        this.pos = pos;
        this.rotation = rotation;
        this.oldRotation = oldRotation;
        this.isChevronRaised = isChevronRaised;
        this.signalStrength = signalStrength;
        this.computerRotation = computerRotation;
        this.rotateClockwise = rotateClockwise;
        this.desiredSymbol = desiredSymbol;
    }

    public ClientboundMilkyWayStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt(), buffer.readInt(), buffer.readBoolean(), buffer.readInt(), buffer.readBoolean(), buffer.readBoolean(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeInt(this.rotation);
        buffer.writeInt(this.oldRotation);
        buffer.writeBoolean(this.isChevronRaised);
        buffer.writeInt(this.signalStrength);
        buffer.writeBoolean(this.computerRotation);
        buffer.writeBoolean(this.rotateClockwise);
        buffer.writeInt(this.desiredSymbol);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() ->
        {
        	ClientAccess.updateMilkyWayStargate(this.pos, this.rotation, this.oldRotation, this.isChevronRaised, this.signalStrength, this.computerRotation, this.rotateClockwise, this.desiredSymbol);
        });
        return true;
    }
}


