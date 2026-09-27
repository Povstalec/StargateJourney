package net.povstalec.sgjourney.common.packets;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.network.NetworkEvent;
import net.povstalec.sgjourney.common.items.AutoDialerItem;
import net.povstalec.sgjourney.common.sgjourney.Address;

import java.util.function.Supplier;

public class ServerboundAutoDialerUpdatePacket
{
	public final InteractionHand interactionHand;
	
    public final Address address;
    public final boolean doKawoosh;

    public ServerboundAutoDialerUpdatePacket(InteractionHand interactionHand, Address address, boolean doKawoosh)
    {
    	this.interactionHand = interactionHand;
    	
        this.address = address;
        this.doKawoosh = doKawoosh;
    }

    public ServerboundAutoDialerUpdatePacket(FriendlyByteBuf buffer)
    {
    	this(buffer.readBoolean() ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND, Address.Mutable.read(buffer), buffer.readBoolean());
    }

    public void encode(FriendlyByteBuf buffer)
    {
    	buffer.writeBoolean(interactionHand == InteractionHand.MAIN_HAND);
    	
		address.write(buffer);
        buffer.writeBoolean(doKawoosh);
    }

    public boolean handle(Supplier<NetworkEvent.Context> ctx)
    {
    	ctx.get().enqueueWork(() ->
		{
    		final ServerPlayer player = ctx.get().getSender();
    		
    		ItemStack stack = player.getItemInHand(interactionHand);
    		
    		if(stack.getItem() instanceof AutoDialerItem)
    		{
				AutoDialerItem.setAddress(stack, address);
				AutoDialerItem.setDoKawoosh(stack, doKawoosh);
    		}
    	});
        return true;
    }
}


