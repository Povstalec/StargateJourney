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
import net.povstalec.sgjourney.common.misc.CoordinateHelper;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;

public class Dialing
{
	public static final int[] DIALED_7_CHEVRON_CONFIGURATION = new int [] {1, 2, 3, 6, 7, 8, 4, 5};
	public static final int[] DIALED_8_CHEVRON_CONFIGURATION = new int [] {1, 2, 3, 4, 6, 7, 8, 5};
	public static final int[] DIALED_9_CHEVRON_CONFIGURATION = new int [] {1, 2, 3, 4, 5, 6, 7, 8};
	
	public static final int[] DEFAULT_CHEVRON_CONFIGURATION = DIALED_7_CHEVRON_CONFIGURATION;
	
	public static int[] getChevronConfiguration(int addressLength)
	{
		return switch(addressLength)
		{
			case 6 -> Dialing.DIALED_7_CHEVRON_CONFIGURATION;
			case 7 -> Dialing.DIALED_8_CHEVRON_CONFIGURATION;
			case 8 -> Dialing.DIALED_9_CHEVRON_CONFIGURATION;
			default -> Dialing.DEFAULT_CHEVRON_CONFIGURATION;
		};
	}
	
	public static StargateInfo.Feedback dialStargate(MinecraftServer server, Stargate dialingStargate, Address.Immutable address, boolean doKawoosh)
	{
		if(SGJourneyEvents.onStargateDial(server, dialingStargate, address, doKawoosh))
			return StargateInfo.Feedback.NONE;
		
		return switch(address.getLength())
		{
			case 6 -> get7ChevronStargate(server, dialingStargate, address, doKawoosh);
			case 7 -> get8ChevronStargate(server, dialingStargate, address, doKawoosh);
			case 8 -> get9ChevronStargate(server, dialingStargate, address, doKawoosh);
			default -> dialingStargate.resetStargate(server, StargateInfo.Feedback.INVALID_ADDRESS, true);
		};
	}
	
	private static StargateInfo.Feedback get7ChevronStargate(MinecraftServer server, Stargate dialingStargate, Address.Immutable dialedAddress, boolean doKawoosh)
	{
		SolarSystem.Serializable solarSystem = Universe.get(server).getSolarSystemFromAddress(dialingStargate.getSolarSystem(server), dialedAddress);
		
		if(solarSystem == null)
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.INVALID_ADDRESS, true);
		
		return getStargate(server, dialingStargate, solarSystem, Address.Type.ADDRESS_7_CHEVRON, doKawoosh);
	}
	
	private static StargateInfo.Feedback get8ChevronStargate(MinecraftServer server, Stargate dialingStargate,
															 Address.Immutable extragalacticAddress, boolean doKawoosh)
	{
		SolarSystem.Serializable solarSystem = Universe.get(server).getSolarSystemFromExtragalacticAddress(extragalacticAddress);
		
		if(solarSystem == null)
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.INVALID_ADDRESS, true);
		
		return getStargate(server, dialingStargate, solarSystem, Address.Type.ADDRESS_8_CHEVRON, doKawoosh);
	}
	
	private static StargateInfo.Feedback getStargate(MinecraftServer server, Stargate dialingStargate, SolarSystem.Serializable dialedSystem, Address.Type addressType, boolean doKawoosh)
	{
		SolarSystem.Serializable currentSystem = dialingStargate.getSolarSystem(server);
		
		if(dialedSystem.equals(currentSystem))
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.SAME_SYSTEM_DIAL, true);
		
		if(dialedSystem.getStargates().isEmpty())
		{
			List<ResourceKey<Level>> dimensionList = dialedSystem.getDimensions();
			
			// Cycles through the list of Dimensions in the Solar System
			int dimensions = 0;
			for(int i = 0; i < dimensionList.size(); i++)
			{
				ResourceKey<Level> levelKey = dimensionList.get(i);
				
				if(server.levelKeys().contains(levelKey))
				{
					Level targetLevel = server.getLevel(levelKey);
					StargateNetwork.findStargates((ServerLevel) targetLevel);
					dimensions++;
				}
			}
			
			if(dimensions == 0)
				return dialingStargate.resetStargate(server, StargateInfo.Feedback.NO_DIMENSIONS, true);
		}
		
		return getPreferredStargate(server, dialingStargate, dialedSystem, addressType, doKawoosh);
	}
	
	private static StargateInfo.Feedback getStargateFromAddress(MinecraftServer server, Stargate dialingStargate, Address.Immutable address, boolean doKawoosh)
	{
		Stargate stargate = StargateNetwork.get(server).getStargate(address);
		
		if(stargate == null)
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.INVALID_ADDRESS, true);
		
		StargateInfo.Feedback feedback = stargate.tryConnect(server, dialingStargate, Address.Type.ADDRESS_9_CHEVRON, doKawoosh);
		
		if(!feedback.isError())
			return feedback;
		
		return dialingStargate.resetStargate(server, feedback, true);
	}
	
	private static StargateInfo.Feedback get9ChevronStargate(MinecraftServer server, Stargate dialingStargate,
															 Address.Immutable address, boolean doKawoosh)
	{
		return getStargateFromAddress(server, dialingStargate, address, doKawoosh);
	}
	
	private static StargateInfo.Feedback getPreferredStargate(MinecraftServer server, Stargate dialingStargate, SolarSystem.Serializable solarSystem, Address.Type addressType, boolean doKawoosh)
	{
		List<Stargate> stargates = solarSystem.getStargates();
		
		if(stargates.isEmpty())
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.NO_STARGATES, true);
		
		// Primary Stargate
		if(CommonStargateNetworkConfig.primary_stargate.get() && solarSystem.primaryStargate() != null)
		{
			StargateInfo.Feedback feedback = solarSystem.primaryStargate().tryConnect(server, dialingStargate, addressType, doKawoosh);
			
			if(feedback != StargateInfo.Feedback.TARGET_OBSTRUCTED && feedback != StargateInfo.Feedback.TARGET_RESTRICTED &&
					feedback != StargateInfo.Feedback.BLACKLISTED_BY_TARGET && feedback != StargateInfo.Feedback.NOT_WHITELISTED_BY_TARGET)
				return feedback;
		}
		
		// Preferred Stargate
		for(int i = 0; i < stargates.size(); i++)
		{
			boolean isLastStargate = i == stargates.size() - 1;
			Stargate targetStargate = stargates.get(i);
			
			StargateInfo.Feedback feedback = targetStargate.tryConnect(server, dialingStargate, addressType, doKawoosh);
			
			// If Stargate isn't obstructed and its network isn't restricted, connect
			if(feedback != StargateInfo.Feedback.TARGET_OBSTRUCTED && feedback != StargateInfo.Feedback.TARGET_RESTRICTED &&
					feedback != StargateInfo.Feedback.BLACKLISTED_BY_TARGET && feedback != StargateInfo.Feedback.NOT_WHITELISTED_BY_TARGET)
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
