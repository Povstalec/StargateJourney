package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;

public class ServerboundDHDUpdatePacket
{
	public final BlockPos blockPos;
	public final int symbol;

    public ServerboundDHDUpdatePacket(BlockPos blockPos, int symbol)
    {
		this.blockPos = blockPos;
		this.symbol = symbol;
    }

    public ServerboundDHDUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readInt());
    }

    public void encode(FriendlyByteBuf buffer)
    {
    	buffer.writeBlockPos(blockPos);
    	buffer.writeInt(symbol);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
    	ctx.get().enqueueWork(() -> {
    		final BlockEntity blockEntity = ctx.get().getSender().level.getBlockEntity(blockPos);
    		if(blockEntity instanceof AbstractDHDEntity dhd)
    		{
    			dhd.engageChevron(this.symbol);
    		}
    	});
        return true;
    }
}


