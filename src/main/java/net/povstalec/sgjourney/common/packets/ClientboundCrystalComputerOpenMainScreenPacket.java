package net.povstalec.sgjourney.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

import java.util.function.Supplier;

public class ClientboundCrystalComputerOpenMainScreenPacket
{
    public final boolean mainHand;

    public ClientboundCrystalComputerOpenMainScreenPacket(boolean mainHand)
    {
        this.mainHand = mainHand;
    }

    public ClientboundCrystalComputerOpenMainScreenPacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(mainHand);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.openCrystalComputerMainScreen(mainHand);
        });
        return true;
    }
}


