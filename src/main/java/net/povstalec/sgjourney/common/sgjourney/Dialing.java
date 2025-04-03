package net.povstalec.sgjourney.common.sgjourney;

import java.util.List;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.CommonStargateNetworkConfig;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.events.custom.SGJourneyEvents;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

public class Dialing
{
	public static final int[] DIALED_7_CHEVRON_CONFIGURATION = new int [] {1, 2, 3, 6, 7, 8, 4, 5};
	public static final int[] DIALED_8_CHEVRON_CONFIGURATION = new int [] {1, 2, 3, 4, 6, 7, 8, 5};
	public static final int[] DIALED_9_CHEVRON_CONFIGURATION = new int [] {1, 2, 3, 4, 5, 6, 7, 8};
	
	public static final int[] DEFAULT_CHEVRON_CONFIGURATION = DIALED_7_CHEVRON_CONFIGURATION;
	
	public static StargateInfo.Feedback dialStargate(ServerLevel level, Stargate dialingStargate,
													 Address.Immutable address, Address.Immutable dialingAddress, boolean doKawoosh)
	{
		if(SGJourneyEvents.onStargateDial(level.getServer(), dialingStargate, address, dialingAddress, doKawoosh))
			return StargateInfo.Feedback.NONE;
		
		switch(address.getLength())
		{
		case 6:
			return get7ChevronStargate(level, dialingStargate, address, dialingAddress, doKawoosh);
		case 7:
			return get8ChevronStargate(level, dialingStargate, address, dialingAddress, doKawoosh);
		case 8:
			return get9ChevronStargate(level, dialingStargate, address, dialingAddress, doKawoosh);
		default:
			return dialingStargate.resetStargate(level.getServer(), StargateInfo.Feedback.INVALID_ADDRESS, true);
		}
	}
	
	private static StargateInfo.Feedback get7ChevronStargate(ServerLevel level, Stargate dialingStargate,
															 Address.Immutable dialedAddress, Address.Immutable dialingAddress, boolean doKawoosh)
	{
		SolarSystem.Serializable solarSystem = Universe.get(level).getSolarSystemFromAddress(level.dimension(), dialedAddress);
		
		if(solarSystem == null)
			return dialingStargate.resetStargate(level.getServer(), StargateInfo.Feedback.INVALID_ADDRESS, true);
		
		return getStargate(level, dialingStargate, solarSystem, Address.Type.ADDRESS_7_CHEVRON, dialingAddress, doKawoosh);
	}
	
	private static StargateInfo.Feedback get8ChevronStargate(ServerLevel level, Stargate dialingStargate,
															 Address.Immutable extragalacticAddress, Address.Immutable dialingAddress, boolean doKawoosh)
	{
		SolarSystem.Serializable solarSystem = Universe.get(level).getSolarSystemFromExtragalacticAddress(extragalacticAddress);
		
		if(solarSystem == null)
			return dialingStargate.resetStargate(level.getServer(), StargateInfo.Feedback.INVALID_ADDRESS, true);
		
		return getStargate(level, dialingStargate, solarSystem, Address.Type.ADDRESS_8_CHEVRON, dialingAddress, doKawoosh);
	}
	
	private static StargateInfo.Feedback getStargate(ServerLevel level, Stargate dialingStargate, SolarSystem.Serializable dialedSystem,
													 Address.Type addressType, Address.Immutable dialingAddress, boolean doKawoosh)
	{
		SolarSystem.Serializable currentSystem = Universe.get(level).getSolarSystemFromDimension(level.dimension());
		
		MinecraftServer server = level.getServer();
		
		if(currentSystem != null && dialedSystem.equals(currentSystem))
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.SAME_SYSTEM_DIAL, true);
		
		if(dialedSystem.getStargates().isEmpty())
		{
			List<ResourceKey<Level>> dimensionList = dialedSystem.getDimensions();
			
			// Cycles through the list of Dimensions in the Solar System
			int dimensions = 0;
			for(int i = 0; i < dimensionList.size(); i++)
			{
				ResourceKey<Level> levelKey = dimensionList.get(i);
				
				if(level.getServer().levelKeys().contains(levelKey))
				{
					Level targetLevel = server.getLevel(levelKey);
					StargateNetwork.findStargates((ServerLevel) targetLevel);
					dimensions++;
				}
			}
			
			if(dimensions == 0)
				return dialingStargate.resetStargate(server, StargateInfo.Feedback.NO_DIMENSIONS, true);
		}
		
		return getPreferredStargate(level, dialingStargate, dialedSystem, addressType, dialingAddress, doKawoosh);
	}
	
	private static StargateInfo.Feedback getStargateFromAddress(MinecraftServer server, Stargate dialingStargate,
																Address.Immutable address, Address.Immutable dialingAddress, boolean doKawoosh)
	{
		Stargate stargate = StargateNetwork.get(server).getStargate(address);
		
		if(stargate == null)
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.INVALID_ADDRESS, true);
		
		BlockPos pos = stargate.getBlockPos();
		ResourceKey<Level> dimension = stargate.getDimension();
		
		if(pos != null && dimension != null)
		{
			if(server.getLevel(dimension).getBlockEntity(pos) instanceof AbstractStargateEntity targetStargate)
			{
				if(targetStargate.isObstructed())
					return dialingStargate.resetStargate(server, StargateInfo.Feedback.TARGET_OBSTRUCTED, true);
				
				else if(targetStargate.addressFilterInfo().getFilterType().shouldFilter())
				{
					if(targetStargate.addressFilterInfo().getFilterType().isBlacklist() && targetStargate.addressFilterInfo().isAddressBlacklisted(dialingAddress))
						return dialingStargate.resetStargate(server, StargateInfo.Feedback.BLACKLISTED_SELF, true);
					
					else if(targetStargate.addressFilterInfo().getFilterType().isWhitelist() && !targetStargate.addressFilterInfo().isAddressWhitelisted(dialingAddress))
						return dialingStargate.resetStargate(server, StargateInfo.Feedback.WHITELISTED_SELF, true);
				}
				
				return connectStargates(server, dialingStargate, stargate, Address.Type.ADDRESS_9_CHEVRON, doKawoosh);
			}
		}
		
		return dialingStargate.resetStargate(server, StargateInfo.Feedback.COULD_NOT_REACH_TARGET_STARGATE, true);
	}
	
	private static StargateInfo.Feedback get9ChevronStargate(ServerLevel level, Stargate dialingStargate,
															 Address.Immutable address, Address.Immutable dialingAddress, boolean doKawoosh)
	{
		return getStargateFromAddress(level.getServer(), dialingStargate, address, dialingAddress, doKawoosh);
	}
	
	private static StargateInfo.Feedback getPreferredStargate(ServerLevel level, Stargate dialingStargate, SolarSystem.Serializable solarSystem, Address.Type addressType, Address.Immutable dialingAddress, boolean doKawoosh)
	{
		MinecraftServer server = level.getServer();
		List<Stargate> stargates = solarSystem.getStargates();
		
		if(stargates.isEmpty())
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.NO_STARGATES, true);
		
		// Primary Stargate
		if(CommonStargateNetworkConfig.primary_stargate.get() && solarSystem.primaryStargate() != null)
		{
			StargateInfo.Feedback feedback = solarSystem.primaryStargate().tryConnect(server, dialingStargate, addressType, dialingAddress, doKawoosh);
			
			if(feedback != StargateInfo.Feedback.TARGET_OBSTRUCTED && feedback != StargateInfo.Feedback.TARGET_RESTRICTED &&
					feedback != StargateInfo.Feedback.BLACKLISTED_SELF && feedback != StargateInfo.Feedback.WHITELISTED_SELF)
				return feedback;
		}
		
		// Preferred Stargate
		for(int i = 0; i < stargates.size(); i++)
		{
			boolean isLastStargate = i == stargates.size() - 1;
			Stargate targetStargate = stargates.get(i);
			
			StargateInfo.Feedback feedback = targetStargate.tryConnect(server, dialingStargate, addressType, dialingAddress, doKawoosh);
			
			// If Stargate isn't obstructed and its network isn't restricted, connect
			if(feedback != StargateInfo.Feedback.TARGET_OBSTRUCTED && feedback != StargateInfo.Feedback.TARGET_RESTRICTED &&
					feedback != StargateInfo.Feedback.BLACKLISTED_SELF && feedback != StargateInfo.Feedback.WHITELISTED_SELF)
				return feedback;
			
			if(isLastStargate)
				return dialingStargate.resetStargate(server, feedback, true);
		}
		
		return dialingStargate.resetStargate(server, StargateInfo.Feedback.UNKNOWN_ERROR, true);
	}
	
	public static StargateInfo.Feedback connectStargates(MinecraftServer server, Stargate dialingStargate, Stargate dialedStargate, Address.Type addressType, boolean doKawoosh)
	{
		return StargateNetwork.get(server).createConnection(server, dialingStargate, dialedStargate, addressType, doKawoosh);
	}
}
