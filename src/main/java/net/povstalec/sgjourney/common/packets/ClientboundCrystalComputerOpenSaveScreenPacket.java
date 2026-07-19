package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.InteractionHand;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.client.ClientAccess;

import java.util.function.Supplier;

public class ClientboundCrystalComputerOpenSaveScreenPacket
{
    public final InteractionHand interactionHand;
	public final BlockPos clickedPos;

    public ClientboundCrystalComputerOpenSaveScreenPacket(InteractionHand interactionHand, BlockPos clickedPos)
    {
        this.interactionHand = interactionHand;
        this.clickedPos = clickedPos;
    }

    public ClientboundCrystalComputerOpenSaveScreenPacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, buffer.readBlockPos());
    }

    public void encode(FriendlyByteBuf buffer)
    {
        buffer.writeBoolean(interactionHand == InteractionHand.MAIN_HAND);
        buffer.writeBlockPos(clickedPos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
        ctx.get().enqueueWork(() -> {
        	ClientAccess.openCrystalComputerSaveScreen(interactionHand, clickedPos);
        });
        return true;
    }
}


