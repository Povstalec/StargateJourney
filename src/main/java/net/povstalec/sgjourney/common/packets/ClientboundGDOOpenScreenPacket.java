package net.povstalec.sgjourney.common.packets;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundGDOOpenScreenPacket
{
    public final UUID playerId;
    public final boolean mainHand;
    
    public final String idc;
    public final int frequency;

    public ClientboundGDOOpenScreenPacket(UUID playerId, boolean mainHand, String idc, int frequency)
    {
        this.playerId = playerId;
        this.mainHand = mainHand;
        
        this.idc = idc;
        this.frequency = frequency;
    }

    public ClientboundGDOOpenScreenPacket(FriendlyByteBuf buffer)
    {
        this(buffer.readUUID(), buffer.readBoolean(), buffer.readUtf(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeUUID(playerId);
        buffer.writeBoolean(mainHand);
        
        buffer.writeUtf(idc);
        buffer.writeInt(frequency);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.openGDOScreen(playerId, mainHand, idc, frequency);
        });
        return true;
    }
}


