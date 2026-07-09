package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractNaquadahLiquidizerEntity;

import java.util.function.Supplier;

public class ServerboundLiquidizerUpdatePacket
{
	public final BlockPos blockPos;
	public final boolean inputTank;

    public ServerboundLiquidizerUpdatePacket(BlockPos blockPos, boolean inputTank)
    {
		this.blockPos = blockPos;
		this.inputTank = inputTank;
    }

    public ServerboundLiquidizerUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer)
    {
    	buffer.writeBlockPos(blockPos);
    	buffer.writeBoolean(inputTank);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
    	ctx.get().enqueueWork(() ->
		{
    		final BlockEntity blockEntity = ctx.get().getSender().level.getBlockEntity(blockPos);
    		if(blockEntity instanceof AbstractNaquadahLiquidizerEntity<?> liquidizer)
			{
				if(inputTank)
					liquidizer.dumpInputFluidTank();
				else
					liquidizer.dumpOutputFluidTank();
			}
    	});
        return true;
    }
}


