package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

import java.util.function.Supplier;

public class ClientboundCrystalComputerOpenSaveScreenPacket
{
    public final boolean mainHand;
	public final BlockPos clickedPos;

    public ClientboundCrystalComputerOpenSaveScreenPacket(boolean mainHand, BlockPos clickedPos)
    {
        this.mainHand = mainHand;
        this.clickedPos = clickedPos;
    }

    public ClientboundCrystalComputerOpenSaveScreenPacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBoolean(), buffer.readBlockPos());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(mainHand);
        buffer.writeBlockPos(clickedPos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.openCrystalComputerSaveScreen(mainHand, clickedPos);
        });
        return true;
    }
}


