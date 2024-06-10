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
    public final int kawooshTick;
    public final int tick;
    public final short irisProgress;
    public final String pointOfOrigin;
    public final String symbols;
    public final String variant;

    public ClientboundStargateUpdatePacket(BlockPos pos, int[] address, int[] engagedChevrons, int kawooshTick, int tick, short irisProgress,
    		String pointOfOrigin, String symbols, String variant)
    {
        this.pos = pos;
        this.address = address;
        this.engagedChevrons = engagedChevrons;
        this.kawooshTick = kawooshTick;
        this.tick = tick;
        this.irisProgress = irisProgress;
        this.pointOfOrigin = pointOfOrigin;
        this.symbols = symbols;
        this.variant = variant;
    }

    public ClientboundStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readVarIntArray(), buffer.readVarIntArray(), buffer.readInt(), buffer.readInt(), buffer.readShort(), buffer.readUtf(), buffer.readUtf(), buffer.readUtf());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeVarIntArray(this.address);
        buffer.writeVarIntArray(this.engagedChevrons);
        buffer.writeInt(this.kawooshTick);
        buffer.writeInt(this.tick);
        buffer.writeShort(this.irisProgress);
        buffer.writeUtf(this.pointOfOrigin);
        buffer.writeUtf(this.symbols);
        buffer.writeUtf(this.variant);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateStargate(this.pos, this.address, this.engagedChevrons, this.kawooshTick, this.tick, this.irisProgress, this.pointOfOrigin, this.symbols, this.variant);
        });
        return true;
    }
}


