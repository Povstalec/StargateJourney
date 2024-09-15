package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.common.block_entities.TransceiverEntity;

public class ServerboundTransceiverUpdatePacket
{
	public final BlockPos pos;
	public final boolean remove;
	
	public final boolean toggleFrequency;
	
    public final int number;
    public final boolean transmit;

    public ServerboundTransceiverUpdatePacket(BlockPos pos, boolean remove, boolean toggleFrequency, int number, boolean transmit)
    {
    	this.pos = pos;
    	this.remove = remove;
    	
    	this.toggleFrequency = toggleFrequency;
    	
        this.number = number;
        this.transmit = transmit;
    }

    public ServerboundTransceiverUpdatePacket(FriendlyByteBuf buffer)
    {
    	this(buffer.readBlockPos(), buffer.readBoolean(), buffer.readBoolean(), buffer.readInt(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer)
    {
    	buffer.writeBlockPos(pos);
    	buffer.writeBoolean(remove);
    	
    	buffer.writeBoolean(toggleFrequency);
    	
        buffer.writeInt(number);
        buffer.writeBoolean(transmit);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
    	ctx.get().enqueueWork(() -> {
    		final BlockEntity blockEntity = ctx.get().getSender().level().getBlockEntity(pos);
    		
    		if(blockEntity instanceof TransceiverEntity transceiver)
    		{
    			if(transmit)
    				transceiver.sendTransmission();
    			else if(toggleFrequency)
    				transceiver.toggleFrequency();
    			else if(remove)
    				transceiver.removeFromCode();
    			else
    				transceiver.addToCode(number);
    		}
    	});
        return true;
    }
}


