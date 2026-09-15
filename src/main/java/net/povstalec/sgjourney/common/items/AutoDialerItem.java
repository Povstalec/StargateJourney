package net.povstalec.sgjourney.common.items;

import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.misc.LocatorHelper;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

public class AutoDialerItem extends Item
{
	public AutoDialerItem(Properties properties)
	{
		super(properties);
	}
	
	@Override
    public InteractionResultHolder<ItemStack> use(Level level, Player player, InteractionHand usedHand)
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
					Address address = new Address.Immutable(26, 6, 14, 31, 11, 29);
					stargate.instaDial(address, false);
				}
			}
		}
		
		return InteractionResultHolder.success(stack);
    }
}
