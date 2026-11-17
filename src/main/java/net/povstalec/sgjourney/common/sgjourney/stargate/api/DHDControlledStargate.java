package net.povstalec.sgjourney.common.sgjourney.stargate.api;

import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.misc.AutoCache;
import net.povstalec.sgjourney.common.sgjourney.Dialing;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.dhd.api.DHD;
import net.povstalec.sgjourney.common.sgjourney.stargate.SGJourneyBlockEntityStargate;

import javax.annotation.Nullable;

/**
 * Interface representing a Stargate, which is intended to connect to a {@link DHD}.
 * <p>
 *     The implementation of {@link AutoCache.IReceiver#controllerCache()} is expected to be fully capable of recognizing nearby instances of {@link DHD}
 *     and choosing one to connect to, as well as maintaining this connection across multiple game sessions.
 * </p>
 * <p>
 *     Recognizing nearby instances of {@link DHD} is done by searching for the {@link DHD#DHD_CAPABILITY}.
 * </p>
 * <p>
 *     For an example of how to make the AutoCache work, see {@link SGJourneyBlockEntityStargate#loadFromBlockEntity(AbstractStargateEntity)}.
 * </p>
 */
public interface DHDControlledStargate extends Stargate, AutoCache.IReceiver<DHD, DHDControlledStargate>
{
	Capability<DHDControlledStargate> DHD_CONTROLLED_STARGATE_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
	
	/**
	 * @return DHD the Stargate is currently connected to, or null if it isn't connected to one
	 */
	@Nullable
	default DHD getDHD()
	{
		return controllerCache().get();
	}
	
	// Updating
	
	/**
	 * Runs when a DHD requires an update (typically this would be the DHD connected to this Stargate)
	 * @param dhd DHD to update
	 */
	void updateDHD(DHD dhd);
	
	/**
	 * Updates this Stargate with information from the provided DHD
	 * @param dhd DHD to update from
	 */
	default void updateFromDHD(DHD dhd)
	{
		if(getServer() != null)
			StargateNetwork.get(getServer()).updateStargateInNetwork(this);
	}
	
	/**
	 * Updates this Stargate, typically used whenever the Stargate needs updating, like when a Stargate
	 * is added to/removed from the Stargate Network and the Stargate priority list needs to be recalculated
	 */
	@Override
	default void update()
	{
		controllerCache().ifPresent(DHD::update);
	}
	
	//============================================================================================
	//******************************************Dialing*******************************************
	//============================================================================================
	
	/**
	 * Method to {@link Stargate#indirectEngageSymbol(int, boolean, StargateInfo.ChevronSound) indirectly engage symbols} using a DHD
	 * @param dhd DHD that's being used to engage symbols
	 * @param symbol Symbol to be encoded
	 * @param canEngageStargate If true, encoding the Point of Origin will automatically engage the Stargate
	 * @param chevronSound Type of chevron sound to play
	 * @return Feedback from encoding the symbol
	 */
	default StargateInfo.FeedbackMessage dhdEngageSymbol(DHD dhd, int symbol, boolean canEngageStargate, StargateInfo.ChevronSound chevronSound)
	{
		return indirectEngageSymbol(symbol, canEngageStargate, chevronSound);
	}
	
	/**
	 * Method to  {@link Stargate#indirectEngageStargate(boolean, Dialing.Action) indirectly engage the Stargate} using a DHD
	 * @param dhd DHD that's being used to engage the Stargate
	 * @return Feedback from engaging the Stargate
	 */
	default StargateInfo.FeedbackMessage dhdEngageStargate(DHD dhd, boolean doKawoosh, Dialing.Action action)
	{
		return indirectEngageStargate(doKawoosh, action);
	}
	
	/**
	 * {@link Stargate#indirectEngageSymbol(int, boolean, StargateInfo.ChevronSound) Disconnects the Stargate} using a DHD
	 * @param dhd DHD that's being used to disconnect the Stargate
	 * @param feedback Feedback Message containing the reason for disconnecting the Stargate
	 * @return Feedback with information regarding how this Stargate's disconnect attempt went
	 */
	default StargateInfo.FeedbackMessage dhdDisconnectStargate(DHD dhd, StargateInfo.FeedbackMessage feedback)
	{
		return disconnectStargate(feedback);
	}
}
