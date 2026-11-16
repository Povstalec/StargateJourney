package net.povstalec.sgjourney.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

import java.util.function.Supplier;

public class ClientboundAutoDialerOpenScreenPacket
{
    public final InteractionHand interactionHand;

    public ClientboundAutoDialerOpenScreenPacket(InteractionHand interactionHand)
    {
        this.interactionHand = interactionHand;
    }

    public ClientboundAutoDialerOpenScreenPacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(interactionHand == InteractionHand.MAIN_HAND);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> ClientAccess.openAutoDialerScreen(interactionHand));
        return true;
    }
}


