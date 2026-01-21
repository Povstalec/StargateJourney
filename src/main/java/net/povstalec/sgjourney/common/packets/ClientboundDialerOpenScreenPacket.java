package net.povstalec.sgjourney.common.packets;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundDialerOpenScreenPacket
{
    public final UUID playerId;

    public ClientboundDialerOpenScreenPacket(UUID playerId)
    {
        this.playerId = playerId;
    }

    public ClientboundDialerOpenScreenPacket(FriendlyByteBuf buffer)
    {
        this(buffer.readUUID());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUUID(this.playerId);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateDialer(playerId);
        });
        return true;
    }
}


