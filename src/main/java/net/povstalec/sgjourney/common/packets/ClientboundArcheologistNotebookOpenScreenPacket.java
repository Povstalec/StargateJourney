package net.povstalec.sgjourney.common.packets;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

import java.util.UUID;
import java.util.function.Supplier;

public class ClientboundArcheologistNotebookOpenScreenPacket
{
    public final UUID playerId;
    public final boolean mainHand;
    
    public final CompoundTag tag;

    public ClientboundArcheologistNotebookOpenScreenPacket(UUID playerId, boolean mainHand, CompoundTag tag)
    {
        this.playerId = playerId;
        this.mainHand = mainHand;
        
        this.tag = tag;
    }

    public ClientboundArcheologistNotebookOpenScreenPacket(FriendlyByteBuf buffer)
    {
        this(buffer.readUUID(), buffer.readBoolean(), buffer.readNbt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUUID(playerId);
        buffer.writeBoolean(mainHand);
        
        buffer.writeNbt(tag);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> ClientAccess.openArcheologistNotebookScreen(playerId, mainHand, tag));
        return true;
    }
}


