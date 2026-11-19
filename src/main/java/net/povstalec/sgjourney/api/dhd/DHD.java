package net.povstalec.sgjourney.api.dhd;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.povstalec.sgjourney.api.stargate.DHDControlledStargate;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.items.crystals.ControlCrystalItem;
import net.povstalec.sgjourney.common.misc.AutoCache;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.Dialing;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.dhd.SGJourneyBlockEntityDHD;

import javax.annotation.Nullable;
import java.util.Set;

/**
 * Interface representing a Dial Home Device, which is intended to connect to a {@link DHDControlledStargate}.
 * <p>
 *     The implementation of {@link AutoCache.IController#receiverCache()} is expected to be fully capable of recognizing nearby instances of {@link DHDControlledStargate}
 *     and choosing one to connect to, as well as maintaining this connection across multiple game sessions.
 * </p>
 * <p>
 *     Recognizing nearby instances of {@link DHDControlledStargate} is done by searching for the {@link DHDControlledStargate#DHD_CONTROLLED_STARGATE_CAPABILITY}.
 * </p>
 * <p>
 *     For an example of how to make the AutoCache work, see {@link SGJourneyBlockEntityDHD#loadFromBlockEntity(AbstractDHDEntity)}.
 * </p>
 */
public interface DHD extends AutoCache.IController<DHD, DHDControlledStargate>
{
	Capability<DHD> DHD_CAPABILITY = CapabilityManager.get(new CapabilityToken<>() {});
	
	/**
	 * @return Current Minecraft Server
	 */
	@Nullable
	MinecraftServer getServer();
	
	/**
	 * @return Dimension the DHD is located in or null if it's not located in any Dimension
	 */
	@Nullable
	ResourceKey<Level> getDimension();
	
	/**
	 * @return Level the DHD is currently located in, null if it's not located in any Level
	 */
	@Nullable
	Level getLevel();
	
	/**
	 * @return Position vector of the DHD's center or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getPosition();
	
	/**
	 * @return Returns true if this DHD is loaded (for example, in the case of DHDs placed in the world, if the Chunk the DHD is located in is loaded)
	 */
	boolean isLoaded();
	
	/**
	 * @return Stargate the DHD is currently connected to, or null if it isn't connected to one
	 */
	@Nullable
	default DHDControlledStargate getStargate()
	{
		return receiverCache().get();
	}
	
	/**
	 * @return Maximum distance from which this DHD can connect to a Stargate
	 */
	double getMaxConnectionDistance();
	
	/**
	 * @return Maximum distance (squared) from which this DHD can connect to a Stargate
	 */
	default double getMaxConnectionDistanceSqr()
	{
		return getMaxConnectionDistance() * getMaxConnectionDistance();
	}
	
	// Updating
	
	/**
	 * Updates this DHD, typically runs as a result of {@link DHDControlledStargate#update()} being run
	 */
	default void update() {}
	
	/**
	 * Updates this DHD with information from the Stargate it's connected to
	 * @param stargate Stargate that initiated the update
	 * @param address Address the Stargate is requesting the DHD to display
	 * @param isCenterButtonEngaged Whether the Stargate is requesting the DHD to engage its center button
	 */
	void updateFromStargate(DHDControlledStargate stargate, Address address, boolean isCenterButtonEngaged);
	
	//============================================================================================
	//*************************************Advanced protocols*************************************
	//============================================================================================
	
	/**
	 * Advanced Protocols are a way to
	 * @return True if the DHD has Advanced Protocols active, otherwise false
	 */
	boolean advancedProtocolsActive();
	
	/**
	 * @return The number of ticks the Stargate should wait before automatically closing after the last traveler left its vicinity
	 * (note that this method is ignored until at least one traveler has traveled through the Stargate)
	 */
	default int autocloseTicks()
	{
		return advancedProtocolsActive() ? ControlCrystalItem.AUTOCLOSE_TICKS : 0;
	}
	
	//============================================================================================
	//******************************************Dialing*******************************************
	//============================================================================================
	
	/**
	 * @return The Address currently encoded in the DHD
	 */
	Address getAddress();
	
	/**
	 * @return True if this DHD enables the Stargate to do Call Forwarding, otherwise false
	 */
	boolean doCallForwarding();
	
	/**
	 * @return True if this DHD activates the Stargate's Network Restrictions, otherwise false
	 * (can be overridden by the Stargate's internal Network Restriction handling)
	 */
	boolean hasNetworkRestrictions();
	
	/**
	 * @return Set of networks this DHD allows the Stargate to be a part of
	 * (combines with the Stargate's internal set of networks it's a part of)
	 */
	Set<Integer> getNetworks();
	
	/**
	 * Attempts to engage the Stargate
	 * @return Feedback from attempting to engage the Stargate
	 */
	default StargateInfo.FeedbackMessage engageStargate()
	{
		receiverCache().markDirtyTwoWays();
		return receiverCache().returnOrSupplyDefault(stargate ->
			{
				StargateInfo.FeedbackMessage feedback;
				if(stargate.isConnected())
					feedback = stargate.dhdDisconnectStargate(this, StargateInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT.withInfo());
				else
					feedback = stargate.dhdEngageStargate(this, true/*Do kawoosh*/, Dialing.Action.EXECUTE);
				stargate.updateDHD(this);
				return feedback;
			},
			() ->
			{
				messageNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_connected_to_stargate").withStyle(ChatFormatting.DARK_RED));
				return StargateInfo.Feedback.NONE.withInfo();
			});
	}
	
	/**
	 * Attempts to encode a symbol the into Stargate
	 * @return Feedback from attempting to encode a symbol into the Stargate
	 */
	default StargateInfo.FeedbackMessage encodeSymbol(int symbol)
	{
		receiverCache().markDirtyTwoWays();
		return receiverCache().returnOrSupplyDefault(stargate ->
			{
				StargateInfo.FeedbackMessage feedback;
				// Remap symbol if needed while Advanced Protocols are enabled
				if(advancedProtocolsActive() && !stargate.getSymbolMap().isSymbolMapped(symbol))
					feedback = stargate.dhdEngageSymbol(this, stargate.getSymbolMap().remapToRandomSymbol(symbol, stargate.getAddress().toArray()), false, StargateInfo.ChevronSound.ENGAGE);
				else
					feedback = stargate.dhdEngageSymbol(this, symbol, false, StargateInfo.ChevronSound.ENGAGE);
				stargate.updateDHD(this);
				return feedback;
			},
			() ->
			{
				messageNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_connected_to_stargate").withStyle(ChatFormatting.DARK_RED));
				return StargateInfo.Feedback.NONE.withInfo();
			});
	}
	
	/**
	 * This method typically runs when an attempt to dial the Stargate is made
	 * @param feedback Feedback from attempting to dial the Stargate
	 * @param address Address the Stargate attempted to dial
	 */
	void onDialAttempt(StargateInfo.FeedbackMessage feedback, Address address);
	
	/**
	 * This method typically runs when a Stargate's recent feedback needs to be propagated to the DHD,
	 * allowing the DHD to display the message to nearby players, for example
	 * @param feedback Stargate's feedback
	 */
	default void receiveStargateFeedback(StargateInfo.FeedbackMessage feedback)
	{
		if(feedback.feedback().isError())
			messageNearbyPlayers(feedback.getMessageComponent());
	}
	
	/**
	 * This method typically runs when there is an attempt to display a message to players near the DHD
	 * (This method does not necessarily need to be overridden, the DHD can simply not to display any messages)
	 * @param message Message to display
	 */
	default void messageNearbyPlayers(Component message) {}
}
