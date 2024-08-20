package net.povstalec.sgjourney.common.packets;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundGDOOpenScreenPacket
{
    public final UUID playerId;

    public ClientboundGDOOpenScreenPacket(UUID playerId)
    {
        this.playerId = playerId;
    }

    public ClientboundGDOOpenScreenPacket(FriendlyByteBuf buffer)
    {
        this(buffer.readUUID());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUUID(playerId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.openGDOScreen(playerId);
        });
        return true;
    }
}


