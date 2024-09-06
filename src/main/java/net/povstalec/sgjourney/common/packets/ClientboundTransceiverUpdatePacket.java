package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundTransceiverUpdatePacket
{
    public final BlockPos pos;
    public final boolean editingFrequency;
    public final int frequency;
    public final String idc;

    public ClientboundTransceiverUpdatePacket(BlockPos pos, boolean editingFrequency, int frequency, String idc)
    {
        this.pos = pos;
        this.editingFrequency = editingFrequency;
        this.frequency = frequency;
        this.idc = idc;
    }

    public ClientboundTransceiverUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readBoolean(), buffer.readInt(), buffer.readUtf());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(pos);
        buffer.writeBoolean(editingFrequency);
        buffer.writeInt(frequency);
        buffer.writeUtf(idc);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateTransceiver(pos, editingFrequency, frequency, idc);
        });
        return true;
    }
}


