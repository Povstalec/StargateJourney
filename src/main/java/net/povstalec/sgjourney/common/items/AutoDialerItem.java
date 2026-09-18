package net.povstalec.sgjourney.common.items;

import net.minecraft.nbt.Tag;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.misc.LocatorHelper;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.Dialing;
import net.povstalec.sgjourney.common.sgjourney.StargateConnection;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;
import org.jetbrains.annotations.NotNull;

public class AutoDialerItem extends Item
{
	public static final String DO_KAWOOSH = "do_kawoosh";
	
	public AutoDialerItem(Properties properties)
	{
		super(properties);
	}
	
	public static void setAddress(ItemStack stack, Address address)
	{
		address.saveToCompoundTag(stack.getOrCreateTag(), Address.ADDRESS);
	}
	
	public static Address getAddress(ItemStack stack)
	{
		if(!stack.hasTag() || !stack.getTag().contains(Address.ADDRESS, Tag.TAG_INT_ARRAY))
			return Address.Immutable.EMPTY;
		
		return new Address.Immutable(stack.getTag().getIntArray(Address.ADDRESS));
	}
	
	public static void setDoKawoosh(ItemStack stack, boolean doKawoosh)
	{
		stack.getOrCreateTag().putBoolean(DO_KAWOOSH, doKawoosh);
	}
	
	public static boolean doKawoosh(ItemStack stack)
	{
		if(!stack.hasTag() || !stack.getTag().contains(DO_KAWOOSH))
			return true;
		
		return stack.getTag().getBoolean(DO_KAWOOSH);
	}
	
	//TODO Add energy storage to the item
	
	private static void instaDialStargate(Stargate stargate, Address address, StargateConnection.Type type)
	{
		// Make sure the gate has enough energy
		long stargateEnergy = stargate.extractEnergy(type.getEstablishingPowerCost() + type.getPowerDraw(false) * 200, true);
		long energyNeeded = type.getEstablishingPowerCost() - stargateEnergy + type.getPowerDraw(false) * 200;
		
		stargate.receiveEnergy(energyNeeded, false);
		
		stargate.instaDial(address, false, Dialing.Action.EXECUTE);
	}
	
	@Override
    public @NotNull InteractionResultHolder<ItemStack> use(Level level, @NotNull Player player, @NotNull InteractionHand usedHand)
	{
		if(level.isClientSide())
			return super.use(level, player, usedHand);

		ItemStack stack = player.getItemInHand(usedHand);
		
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
					//TODO Check if there is an Address
					
					StargateInfo.FeedbackMessage feedback = stargate.instaDial(address, doKawoosh(stack), Dialing.Action.SIMULATE_ENOUGH_ENERGY);
					if(feedback.feedback().isConnectionEstablished())
					{
						switch(feedback.feedback())
						{
							case CONNECTION_ESTABLISHED_SYSTEM_WIDE -> instaDialStargate(stargate, address, StargateConnection.Type.SYSTEM_WIDE);
							case CONNECTION_ESTABLISHED_INTERSTELLAR -> instaDialStargate(stargate, address, StargateConnection.Type.INTERSTELLAR);
							case CONNECTION_ESTABLISHED_INTERGALACTIC -> instaDialStargate(stargate, address, StargateConnection.Type.INTERGALACTIC);
						}
					}
					
					//TODO Feedback for the player
				}
			}
		}
		
		return InteractionResultHolder.success(stack);
    }
}
