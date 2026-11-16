package net.povstalec.sgjourney.common.sgjourney.stargate.api;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.EntityCapability;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.misc.AutoCache;
import net.povstalec.sgjourney.common.sgjourney.Dialing;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.dhd.api.DHD;

import javax.annotation.Nullable;

public interface DHDControlledStargate extends Stargate, AutoCache.IReceiver<DHD, DHDControlledStargate>
{
	String DHD_CONTROLLED_STARGATE = "dhd_controlled_stargate";
	
	BlockCapability<DHDControlledStargate, Direction> DHD_CONTROLLED_STARGATE_CAPABILITY_BLOCK = BlockCapability.create(
		StargateJourney.sgjourneyLocation(DHD_CONTROLLED_STARGATE), DHDControlledStargate.class, Direction.class);
	EntityCapability<DHDControlledStargate, Void> DHD_CONTROLLED_STARGATE_CAPABILITY_ENTITY = EntityCapability.createVoid(
		StargateJourney.sgjourneyLocation(DHD_CONTROLLED_STARGATE), DHDControlledStargate.class);
	
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
