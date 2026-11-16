package net.povstalec.sgjourney.common.sgjourney.dhd;

import net.minecraft.network.chat.Component;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.dhd.api.BlockEntityDHD;
import net.povstalec.sgjourney.common.sgjourney.stargate.api.DHDControlledStargate;

import javax.annotation.Nullable;
import java.util.Set;

public interface SGJourneyBlockEntityDHD<DHDEntity extends AbstractDHDEntity<?>> extends BlockEntityDHD<DHDEntity>
{
	// Stuff for handling Block Entities
	
	default void loadFromBlockEntity(AbstractDHDEntity<?> dhd)
	{
		dhd.setupAutoCache(receiverCache());
	}
	
	@Override
	default @Nullable Vec3 getPosition()
	{
		return dhdReturn(stargate -> stargate.getBlockPos().getCenter(), null);
	}
	
	@Override
	default Address getAddress()
	{
		return dhdReturnOrSupply(AbstractDHDEntity::getAddress, Address.Mutable::new);
	}
	
	@Override
	default double getMaxConnectionDistance()
	{
		return dhdReturn(AbstractDHDEntity::getMaxConnectionDistance, 0D);
	}
	
	@Override
	default void updateFromStargate(DHDControlledStargate stargate, Address address, boolean shouldEngageCenterButton)
	{
		dhdRun(dhd -> dhd.updateDHD(stargate, address, shouldEngageCenterButton));
	}
	
	//============================================================================================
	//*************************************Advanced protocols*************************************
	//============================================================================================
	
	@Override
	default boolean advancedProtocolsActive()
	{
		return dhdReturn(AbstractDHDEntity::advancedProtocolsActive, false);
	}
	
	//============================================================================================
	//******************************************Dialing*******************************************
	//============================================================================================
	
	@Override
	default boolean doCallForwarding()
	{
		return dhdReturn(AbstractDHDEntity::doCallForwarding, false);
	}
	
	@Override
	default boolean hasNetworkRestrictions()
	{
		return dhdReturn(AbstractDHDEntity::hasNetworkRestrictions, false);
	}
	
	@Override
	default Set<Integer> getNetworks()
	{
		return dhdReturn(AbstractDHDEntity::getNetworks, Set.of());
	}
	
	@Override
	default void onDialAttempt(StargateInfo.FeedbackMessage feedback, Address address)
	{
		dhdRun(dhd -> dhd.onDialAttempt(feedback, address));
	}
	
	@Override
	default void messageNearbyPlayers(Component message)
	{
		dhdRun(dhd -> dhd.sendMessageToNearbyPlayers(message, AbstractDHDEntity.DHD_INFO_DISTANCE));
	}
}
