package net.povstalec.sgjourney.common.packets;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.items.GDOItem;

public record ServerboundGDOUpdatePacket(boolean mainHand, String idc, int frequency, boolean transmit) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ServerboundGDOUpdatePacket> TYPE =
			new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("c2s_gdo_update"));
	
	public static final StreamCodec<ByteBuf, ServerboundGDOUpdatePacket> STREAM_CODEC = StreamCodec.composite(
			ByteBufCodecs.BOOL, ServerboundGDOUpdatePacket::mainHand,
			ByteBufCodecs.STRING_UTF8, ServerboundGDOUpdatePacket::idc,
			ByteBufCodecs.VAR_INT, ServerboundGDOUpdatePacket::frequency,
			ByteBufCodecs.BOOL, ServerboundGDOUpdatePacket::transmit,
			ServerboundGDOUpdatePacket::new
	);
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	public static void handle(ServerboundGDOUpdatePacket packet, IPayloadContext ctx)
    {
    	ctx.enqueueWork(() -> {
    		final Player player = ctx.player();
    		ItemStack stack = player.getItemInHand(packet.mainHand ? InteractionHand.MAIN_HAND : InteractionHand.OFF_HAND);
    		
    		if(stack.getItem() instanceof GDOItem)
    		{
    			GDOItem.setFrequencyAndIDC(stack, packet.frequency, packet.idc);
    			
    			if(packet.transmit)
    				GDOItem.sendTransmission(player.level(), player, stack);
    		}
    	});
    }
}


