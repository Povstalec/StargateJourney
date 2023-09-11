package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundStargateUpdatePacket
{
    public final BlockPos pos;
    public final int[] address;
    public final int[] engagedChevrons;
    public final boolean dialingOut;
    public final int kawooshTick;
    public final int tick;
    public final String pointOfOrigin;
    public final String symbols;

    public ClientboundStargateUpdatePacket(BlockPos pos, int[] address, int[] engagedChevrons, boolean dialingOut, int kawooshTick, int tick, String pointOfOrigin, String symbols)
    {
        this.pos = pos;
        this.address = address;
        this.engagedChevrons = engagedChevrons;
        this.dialingOut = dialingOut;
        this.kawooshTick = kawooshTick;
        this.tick = tick;
        this.pointOfOrigin = pointOfOrigin;
        this.symbols = symbols;
    }

    public ClientboundStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readVarIntArray(), buffer.readVarIntArray(), buffer.readBoolean(), buffer.readInt(), buffer.readInt(), buffer.readUtf(), buffer.readUtf());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeVarIntArray(this.address);
        buffer.writeVarIntArray(this.engagedChevrons);
        buffer.writeBoolean(this.dialingOut);
        buffer.writeInt(this.kawooshTick);
        buffer.writeInt(this.tick);
        buffer.writeUtf(this.pointOfOrigin);
        buffer.writeUtf(this.symbols);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateStargate(this.pos, this.address, this.engagedChevrons, this.dialingOut, this.kawooshTick, this.tick, this.pointOfOrigin, this.symbols);
        });
        return true;
    }
}


