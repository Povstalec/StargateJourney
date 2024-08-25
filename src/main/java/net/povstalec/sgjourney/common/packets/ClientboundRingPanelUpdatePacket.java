package net.povstalec.sgjourney.common.packets;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundRingPanelUpdatePacket
{
    public final BlockPos pos;
    public final ArrayList<BlockPos> ringsPos;
    public final ArrayList<Component> ringsName;

    public ClientboundRingPanelUpdatePacket(BlockPos pos, List<BlockPos> ringsPos, List<Component> ringsName)
    {
        this.pos = pos;
        this.ringsPos = new ArrayList<BlockPos>(ringsPos);
        this.ringsName = new ArrayList<Component>(ringsName);
    }

    public ClientboundRingPanelUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readList(buf -> buf.readBlockPos()), buffer.readList(buf -> buf.readComponent()));
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBlockPos(this.pos);
        buffer.writeCollection(this.ringsPos, (buf, pos) -> buf.writeBlockPos(pos));
        buffer.writeCollection(this.ringsName, (buf, name) -> buf.writeComponent(name));
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateRingPanel(this.pos, this.ringsPos, this.ringsName);
        });
        return true;
    }
}


