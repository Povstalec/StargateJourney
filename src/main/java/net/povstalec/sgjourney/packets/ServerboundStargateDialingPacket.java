package net.povstalec.sgjourney.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;

public class ServerboundStargateDialingPacket
{
	public final BlockPos blockPos;
	public final int symbol;

    public ServerboundStargateDialingPacket(BlockPos blockPos, int symbol)
    {
		this.blockPos = blockPos;
		this.symbol = symbol;
    }

    public ServerboundStargateDialingPacket(FriendlyByteBuf buffer)
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
    		if(blockEntity instanceof AbstractStargateEntity stargate)
    		{
    			stargate.engageSymbol(symbol);
    		}
    	});
        return true;
    }
}


