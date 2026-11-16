package net.povstalec.sgjourney.common.sgjourney.dhd;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.povstalec.sgjourney.common.items.crystals.ControlCrystalItem;
import net.povstalec.sgjourney.common.misc.AutoCache;
import net.povstalec.sgjourney.common.sgjourney.Address;
import net.povstalec.sgjourney.common.sgjourney.Dialing;
import net.povstalec.sgjourney.common.sgjourney.StargateInfo;
import net.povstalec.sgjourney.common.sgjourney.stargate.DHDControlledStargate;

import javax.annotation.Nullable;
import java.util.Set;

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
	default Level getLevel()
	{
		ResourceKey<Level> dimension = getDimension();
		if(dimension == null)
			return null;
		
		return getServer().getLevel(dimension);
	}
	
	/**
	 * @return Position vector of the DHD's center or null if it doesn't have a position
	 */
	@Nullable
	Vec3 getPosition();
	
	/**
	 * @return Returns true if this DHD is loaded (for example, in the case of DHDs placed in the world, if the Chunk the DHD is located in is loaded)
	 */
	boolean isLoaded();
	
	@Nullable
	default DHDControlledStargate getStargate()
	{
		return receiverCache().get();
	}
	
	double getMaxConnectionDistance();
	
	default double getMaxConnectionDistanceSqr()
	{
		return getMaxConnectionDistance() * getMaxConnectionDistance();
	}
	
	long getEnergyTarget();
	
	void updateFromStargate(DHDControlledStargate stargate, Address address, boolean isCenterButtonEngaged);
	
	//============================================================================================
	//*************************************Advanced protocols*************************************
	//============================================================================================
	
	boolean advancedProtocolsActive();
	
	default int autocloseTicks()
	{
		return advancedProtocolsActive() ? ControlCrystalItem.AUTOCLOSE_TICKS : 0;
	}
	
	default boolean remapSymbols()
	{
		return advancedProtocolsActive();
	}
	
	//============================================================================================
	//******************************************Dialing*******************************************
	//============================================================================================
	
	Address getAddress();
	
	boolean doCallForwarding();
	
	boolean hasNetworkRestrictions();
	
	Set<Integer> getNetworks();
	
	default boolean engageStargate()
	{
		receiverCache().markDirtyTwoWays();
		return receiverCache().returnOrSupplyDefault(stargate ->
			{
				if(stargate.isConnected())
					stargate.dhdDisconnectStargate(this, StargateInfo.Feedback.CONNECTION_ENDED_BY_DISCONNECT.withInfo());
				else
					stargate.dhdEngageStargate(this, true/*Do kawoosh*/, Dialing.Action.EXECUTE);
				stargate.updateDHD(this);
				return true;
			},
			() ->
			{
				messageNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_connected_to_stargate").withStyle(ChatFormatting.DARK_RED));
				return false;
			});
	}
	
	default boolean encodeSymbol(int symbol)
	{
		receiverCache().markDirtyTwoWays();
		return receiverCache().returnOrSupplyDefault(stargate ->
			{
				// Remap symbol if needed while Advanced Protocols are enabled
				if(advancedProtocolsActive() && !stargate.getSymbolMap().isSymbolMapped(symbol))
					stargate.dhdEngageSymbol(this, stargate.getSymbolMap().remapToRandomSymbol(symbol, stargate.getAddress().toArray()), false, StargateInfo.ChevronSound.ENGAGE);
				else
					stargate.dhdEngageSymbol(this, symbol, false, StargateInfo.ChevronSound.ENGAGE);
				stargate.updateDHD(this);
				return true;
			},
			() ->
			{
				messageNearbyPlayers(Component.translatable("message.sgjourney.dhd.error.not_connected_to_stargate").withStyle(ChatFormatting.DARK_RED));
				return false;
			});
	}
	
	void onDialAttempt(StargateInfo.FeedbackMessage feedback, Address address);
	
	default void receiveStargateFeedback(StargateInfo.FeedbackMessage feedback)
	{
		if(feedback.feedback().isError())
			messageNearbyPlayers(feedback.getMessageComponent());
	}
	
	default void messageNearbyPlayers(Component message) {}
}
