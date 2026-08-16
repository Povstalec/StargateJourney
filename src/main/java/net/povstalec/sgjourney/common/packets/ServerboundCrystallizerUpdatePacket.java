package net.povstalec.sgjourney.common.packets;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.common.block_entities.tech.AbstractCrystallizerEntity;

import java.util.function.Supplier;

public class ServerboundCrystallizerUpdatePacket
{
	public final BlockPos blockPos;

    public ServerboundCrystallizerUpdatePacket(BlockPos blockPos)
    {
		this.blockPos = blockPos;
    }

    public ServerboundCrystallizerUpdatePacket(FriendlyByteBuf buffer)
    {
        this(buffer.readBlockPos());
    }

    public void encode(FriendlyByteBuf buffer)
    {
    	buffer.writeBlockPos(blockPos);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
    	ctx.get().enqueueWork(() ->
		{
    		final BlockEntity blockEntity = ctx.get().getSender().level.getBlockEntity(blockPos);
    		if(blockEntity instanceof AbstractCrystallizerEntity<?> crystallizer)
				crystallizer.dumpInputFluidTank();
    	});
        return true;
    }
}


