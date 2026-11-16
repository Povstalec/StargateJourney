package net.povstalec.sgjourney.common.packets;

import java.util.UUID;
import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

public class ClientboundDialerOpenScreenPacket
{
    public ClientboundDialerOpenScreenPacket()
	{
		//TODO
	}

    public ClientboundDialerOpenScreenPacket(FriendlyByteBuf buffer)
    {
		//TODO
    }

    public void encode(FriendlyByteBuf buffer)
    {
       //TODO
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.updateDialer();
        });
        return true;
    }
}


