package net.povstalec.sgjourney.common.items;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.neoforged.neoforge.network.PacketDistributor;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.init.DataComponentInit;
import net.povstalec.sgjourney.common.misc.ComponentHelper;
import net.povstalec.sgjourney.common.misc.LocatorHelper;
import net.povstalec.sgjourney.common.packets.ClientboundAutoDialerOpenScreenPacket;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.Dialing;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class AutoDialerItem extends Item
{
	public AutoDialerItem(Properties properties)
	{
		super(properties);
	}
	
	public static void setAddress(ItemStack stack, Address.Immutable address)
	{
		stack.set(DataComponentInit.ADDRESS_IMMUTABLE, address);
	}
	
	public static Address.Immutable getAddress(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.ADDRESS_IMMUTABLE, Address.Immutable.EMPTY);
	}
	
	public static void setDoKawoosh(ItemStack stack, boolean doKawoosh)
	{
		stack.set(DataComponentInit.DO_KAWOOSH, doKawoosh);
	}
	
	public static boolean doKawoosh(ItemStack stack)
	{
		return stack.getOrDefault(DataComponentInit.DO_KAWOOSH, true);
	}
	
	//TODO Add energy storage to the item
	
	private static StargateInfo.FeedbackMessage instaDialStargate(Stargate stargate, Address address, boolean doKawoosh, StargateConnection.Type type)
	{
		// Make sure the gate has enough energy
		long stargateEnergy = stargate.extractEnergy(type.getEstablishingPowerCost() + type.getPowerDraw(false) * 200, true);
		long energyNeeded = type.getEstablishingPowerCost() - stargateEnergy + type.getPowerDraw(false) * 200;
		
		stargate.receiveEnergy(energyNeeded, false);
		
		return stargate.instaDial(address, doKawoosh, Dialing.Action.EXECUTE);
	}
	
	@Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand)
	{
		if(level.isClientSide())
			return super.use(level, player, usedHand);
		
		ItemStack stack = player.getItemInHand(usedHand);
		
		if(player.isShiftKeyDown())
		{
			PacketDistributor.sendToPlayer((ServerPlayer) player, new ClientboundAutoDialerOpenScreenPacket(usedHand));
			return InteractionResultHolder.success(stack);
		}
		
		AbstractStargateEntity<?> stargateEntity = LocatorHelper.getNearestStargate(level, player.getOnPos().above(), 16);
		if(stargateEntity != null)
		{
			if(stargateEntity.isConnected())
				stargateEntity.disconnectStargate(StargateInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT.withInfo());
			else
			{
				Stargate stargate = stargateEntity.getStargate();
				if(stargate != null)
				{
					Address address = getAddress(stack);
					if(address.isEmpty())
					{
						player.displayClientMessage(Component.translatable("message.sgjourney.auto_dialer.error.no_address").withStyle(ChatFormatting.RED), true);
						return InteractionResultHolder.success(stack);
					}
					
					boolean doKawoosh = doKawoosh(stack);
					StargateInfo.FeedbackMessage feedback = stargate.instaDial(address, doKawoosh, Dialing.Action.SIMULATE_ENOUGH_ENERGY);
					if(feedback.feedback().isConnectionEstablished())
					{
						switch(feedback.feedback())
						{
							case CONNECTION_ESTABLISHED_SYSTEM_WIDE -> player.displayClientMessage(instaDialStargate(stargate, address, doKawoosh, StargateConnection.Type.SYSTEM_WIDE).getMessageComponent(), true);
							case CONNECTION_ESTABLISHED_INTERSTELLAR -> player.displayClientMessage(instaDialStargate(stargate, address, doKawoosh, StargateConnection.Type.INTERSTELLAR).getMessageComponent(), true);
							case CONNECTION_ESTABLISHED_INTERGALACTIC -> player.displayClientMessage(instaDialStargate(stargate, address, doKawoosh, StargateConnection.Type.INTERGALACTIC).getMessageComponent(), true);
						}
					}
					else
						player.displayClientMessage(feedback.getMessageComponent(), true);
				}
			}
		}
		
		return InteractionResultHolder.success(stack);
    }
	
	@Override
	public void appendHoverText(ItemStack stack, TooltipContext context, List<Component> tooltipComponents, TooltipFlag tooltipFlag)
	{
		tooltipComponents.add(Component.translatable("info.sgjourney.address").append(": ").append(getAddress(stack).toComponent(false)).withStyle(ChatFormatting.YELLOW));
		tooltipComponents.add(Component.translatable("tooltip.sgjourney.auto_dialer.kawoosh").append(": " + doKawoosh(stack)).withStyle(ChatFormatting.DARK_BLUE));
		
		tooltipComponents.add(ComponentHelper.usage("tooltip.sgjourney.auto_dialer.usage.menu"));
		tooltipComponents.add(ComponentHelper.usage("tooltip.sgjourney.auto_dialer.usage.dial"));
		tooltipComponents.add(ComponentHelper.description("tooltip.sgjourney.auto_dialer.description"));
	}
}
