package net.povstalec.sgjourney.common.packets;

import java.util.function.Supplier;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.common.items.GDOItem;

public class ServerboundGDOUpdatePacket
{
	public final boolean mainHand;
	
    public final String idc;
    public final int frequency;
    public final boolean transmit;

    public ServerboundGDOUpdatePacket(boolean mainHand, int frequency, String idc, boolean transmit)
    {
    	this.mainHand = mainHand;
    	
        this.idc = idc;
        this.frequency = frequency;
        this.transmit = transmit;
    }

    public ServerboundGDOUpdatePacket(FriendlyByteBuf buffer)
    {
    	this(buffer.readBoolean(), buffer.readInt(), buffer.readUtf(), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer)
    {
    	buffer.writeBoolean(mainHand);
    	
        buffer.writeInt(frequency);
        buffer.writeUtf(idc);
        buffer.writeBoolean(transmit);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
    	ctx.get().enqueueWork(() -> {
    		final ServerPlayer player = ctx.get().getSender();
    		
    		ItemStack stack = player.getItemInHand(mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    		
    		if(stack.getItem() instanceof GDOItem)
    		{
    			GDOItem.setFrequencyAndIDC(stack, frequency, idc);
    			
    			if(transmit)
    				GDOItem.sendTransmission(player.level(), player, stack);
    		}
    	});
        return true;
    }
}


