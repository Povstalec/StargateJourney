package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundStargateUpdatePacket
{
    public final BlockPos pos;
    public final long energy;
    public final int openTime;
    public final int timeSinceLastTraveler;
    public final int[] address;
    public final int[] engagedChevrons;
    public final int kawooshTick;
    public final int tick;
    public final short irisProgress;
    public final ResourceLocation pointOfOrigin;
    public final ResourceLocation symbols;
    public final ResourceLocation variant;
    public final ItemStack iris;

    public ClientboundStargateUpdatePacket(BlockPos pos, long energy, int openTime, int timeSinceLastTraveler, int[] address, int[] engagedChevrons, int kawooshTick, int tick, short irisProgress,
                                           ResourceLocation pointOfOrigin, ResourceLocation symbols, ResourceLocation variant, ItemStack iris)
    {
        this.pos = pos;
        this.energy = energy;
        this.openTime = openTime;
        this.timeSinceLastTraveler = timeSinceLastTraveler;
        this.address = address;
        this.engagedChevrons = engagedChevrons;
        this.kawooshTick = kawooshTick;
        this.tick = tick;
        this.irisProgress = irisProgress;
        this.pointOfOrigin = pointOfOrigin;
        this.symbols = symbols;
        this.variant = variant;
        this.iris = iris;
    }

    public ClientboundStargateUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readLong(), buffer.readInt(), buffer.readInt(), buffer.readVarIntArray(), buffer.readVarIntArray(), buffer.readInt(), buffer.readInt(), buffer.readShort(), buffer.readResourceLocation(), buffer.readResourceLocation(), buffer.readResourceLocation(), buffer.readItem());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeLong(this.energy);
        buffer.writeInt(this.openTime);
        buffer.writeInt(this.timeSinceLastTraveler);
        buffer.writeVarIntArray(this.address);
        buffer.writeVarIntArray(this.engagedChevrons);
        buffer.writeInt(this.kawooshTick);
        buffer.writeInt(this.tick);
        buffer.writeShort(this.irisProgress);
        buffer.writeResourceLocation(this.pointOfOrigin);
        buffer.writeResourceLocation(this.symbols);
        buffer.writeResourceLocation(this.variant);
        buffer.writeItem(this.iris);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateStargate(this.pos, this.energy, this.openTime, this.timeSinceLastTraveler, this.address, this.engagedChevrons, this.kawooshTick, this.tick, this.irisProgress, this.pointOfOrigin, this.symbols, this.variant, this.iris);
        });
        return true;
    }
}


