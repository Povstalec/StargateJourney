package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundGDOOpenScreenPacket
{
    public final boolean mainHand;
    
    public final String idc;
    public final int frequency;

    public ClientboundGDOOpenScreenPacket(boolean mainHand, String idc, int frequency)
    {
        this.mainHand = mainHand;
        
        this.idc = idc;
        this.frequency = frequency;
    }

    public ClientboundGDOOpenScreenPacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBoolean(), buffer.readUtf(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(mainHand);
        
        buffer.writeUtf(idc);
        buffer.writeInt(frequency);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.openGDOScreen(mainHand, idc, frequency);
        });
        return true;
    }
}


