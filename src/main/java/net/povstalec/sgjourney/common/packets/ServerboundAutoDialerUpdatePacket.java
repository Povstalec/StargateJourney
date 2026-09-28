package net.povstalec.sgjourney.common.packets;

import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.network.codec.NeoForgeStreamCodecs;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.items.AutoDialerItem;
import net.povstalec.sgjourney.common.sgjourney.Address;

public record ServerboundAutoDialerUpdatePacket(InteractionHand interactionHand, Address address, boolean doKawoosh) implements CustomPacketPayload
{
	public static final CustomPacketPayload.Type<ServerboundAutoDialerUpdatePacket> TYPE =
		new CustomPacketPayload.Type<>(StargateJourney.sgjourneyLocation("c2s_auto_dialer_update"));
	
	public static final StreamCodec<RegistryFriendlyByteBuf, ServerboundAutoDialerUpdatePacket> STREAM_CODEC = StreamCodec.composite(
		NeoForgeStreamCodecs.enumCodec(InteractionHand.class), ServerboundAutoDialerUpdatePacket::interactionHand,
		Address.STREAM_CODEC, ServerboundAutoDialerUpdatePacket::address,
		ByteBufCodecs.BOOL, ServerboundAutoDialerUpdatePacket::doKawoosh,
		ServerboundAutoDialerUpdatePacket::new
	);
	
	@Override
	public CustomPacketPayload.Type<? extends CustomPacketPayload> type()
	{
		return TYPE;
	}
	
	public static void handle(ServerboundAutoDialerUpdatePacket packet, IPayloadContext ctx)
	{
		ctx.enqueueWork(() ->
		{
			final Player player = ctx.player();
			
			ItemStack stack = player.getItemInHand(packet.interactionHand);
			
			if(stack.getItem() instanceof AutoDialerItem)
			{
				AutoDialerItem.setAddress(stack, new Address.Immutable(packet.address));
				AutoDialerItem.setDoKawoosh(stack, packet.doKawoosh);
			}
		});
	}
}


