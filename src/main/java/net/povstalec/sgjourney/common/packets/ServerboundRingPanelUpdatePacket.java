package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.common.block_entities.RingPanelEntity;

public class ServerboundRingPanelUpdatePacket
{
	public final BlockPos blockPos;
	public final int number;

    public ServerboundRingPanelUpdatePacket(BlockPos blockPos, int number)
    {
		this.blockPos = blockPos;
		this.number = number;
    }

    public ServerboundRingPanelUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
    	buffer.writeBlockPos(blockPos);
    	buffer.writeInt(number);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
    	ctx.get().enqueueWork(() -> {
    		final BlockEntity blockEntity = ctx.get().getSender().level.getBlockEntity(blockPos);
    		if(blockEntity instanceof RingPanelEntity ringPanel)
    		{
    			ringPanel.activateRings(number);
    		}
    	});
        return true;
    }
}


