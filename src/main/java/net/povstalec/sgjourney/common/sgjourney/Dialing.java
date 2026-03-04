package net.povstalec.sgjourney.common.sgjourney;

import java.util.List;

import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.StargateJourney;
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
	
	public static int[] getChevronConfiguration(Address.Type addressType)
	{
		return switch(addressType)
		{
			case ADDRESS_7_CHEVRON -> Dialing.DIALED_7_CHEVRON_CONFIGURATION;
			case ADDRESS_8_CHEVRON -> Dialing.DIALED_8_CHEVRON_CONFIGURATION;
			case ADDRESS_9_CHEVRON -> Dialing.DIALED_9_CHEVRON_CONFIGURATION;
			default -> Dialing.DEFAULT_CHEVRON_CONFIGURATION;
		};
	}
	
	public static StargateInfo.Feedback dialStargate(MinecraftServer server, Stargate dialingStargate, Address address, boolean doKawoosh, boolean mustBeLoaded)
	{
		if(SGJourneyEvents.onStargateDial(server, dialingStargate, address, doKawoosh))
			return StargateInfo.Feedback.NONE;
		
		return switch(address.getType())
		{
			case ADDRESS_7_CHEVRON -> get7ChevronStargate(server, dialingStargate, address, doKawoosh, mustBeLoaded);
			case ADDRESS_8_CHEVRON -> get8ChevronStargate(server, dialingStargate, address, doKawoosh, mustBeLoaded);
			case ADDRESS_9_CHEVRON -> get9ChevronStargate(server, dialingStargate, address, doKawoosh, mustBeLoaded);
			case ADDRESS_INVALID -> dialingStargate.resetStargate(server, StargateInfo.Feedback.INVALID_ADDRESS, true);
		};
	}
	
	public static StargateInfo.Feedback dialStargate(MinecraftServer server, Stargate dialingStargate, Address address, boolean doKawoosh)
	{
		return dialStargate(server, dialingStargate, address, doKawoosh, false);
	}
	
	private static StargateInfo.Feedback get7ChevronStargate(MinecraftServer server, Stargate dialingStargate, Address dialedAddress, boolean doKawoosh, boolean mustBeLoaded)
	{
		AddressRegion.Serializable addressRegion = Universe.get(server).getAddressRegionFromAddress(dialingStargate.getAddressRegion(server), dialedAddress);
		
		if(addressRegion == null)
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.INVALID_ADDRESS, true);
		
		return getStargate(server, dialingStargate, addressRegion, Address.Type.ADDRESS_7_CHEVRON, doKawoosh, mustBeLoaded);
	}
	
	private static StargateInfo.Feedback get8ChevronStargate(MinecraftServer server, Stargate dialingStargate, Address extragalacticAddress, boolean doKawoosh, boolean mustBeLoaded)
	{
		AddressRegion.Serializable addressRegion = Universe.get(server).getAddressRegionFromExtragalacticAddress(extragalacticAddress);
		
		if(addressRegion == null)
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.INVALID_ADDRESS, true);
		
		return getStargate(server, dialingStargate, addressRegion, Address.Type.ADDRESS_8_CHEVRON, doKawoosh, mustBeLoaded);
	}
	
	private static StargateInfo.Feedback getStargate(MinecraftServer server, Stargate dialingStargate, AddressRegion.Serializable dialedRegion, Address.Type addressType, boolean doKawoosh, boolean mustBeLoaded)
	{
		AddressRegion.Serializable currentRegion = dialingStargate.getAddressRegion(server);
		
		if(dialedRegion.equals(currentRegion))
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.SAME_SYSTEM_DIAL, true);
		
		// If the Stargate Network knows of no Stargates in this Address Region, try locating any Structures with them
		if(!mustBeLoaded && dialedRegion.getStargates().isEmpty()) // No point in loading chunks if the connection requires a loaded Stargate
		{
			// Cycles through the list of Dimensions in the Address Region
			int dimensions = 0;
			for(SpaceLocation spaceLocation : dialedRegion.getSpaceLocations())
			{
				ResourceKey<Level> levelKey = spaceLocation.getDimension();
				
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
		
		return getPreferredStargate(server, dialingStargate, dialedRegion, addressType, doKawoosh, mustBeLoaded);
	}
	
	private static StargateInfo.Feedback attemptConnection(MinecraftServer server, Stargate dialingStargate, Stargate dialedStargate, Address.Type addressType, boolean doKawoosh, boolean mustBeLoaded)
	{
		if(mustBeLoaded && !dialedStargate.isLoaded(server))
			return StargateInfo.Feedback.TARGET_NOT_LOADED;
		
		return dialedStargate.tryConnect(server, dialingStargate, addressType, doKawoosh);
	}
	
	private static StargateInfo.Feedback getStargateFromAddress(MinecraftServer server, Stargate dialingStargate, Address address, boolean doKawoosh, boolean mustBeLoaded)
	{
		Stargate stargate = StargateNetwork.get(server).getStargate(address);
		
		if(stargate == null)
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.INVALID_ADDRESS, true);
		
		StargateInfo.Feedback feedback = attemptConnection(server, dialingStargate, stargate, Address.Type.ADDRESS_9_CHEVRON, doKawoosh, mustBeLoaded);
		
		// If Stargate isn't obstructed and its network isn't restricted, connect
		if(!feedback.isSkippable())
			return feedback;
		
		return dialingStargate.resetStargate(server, feedback, true);
	}
	
	private static StargateInfo.Feedback get9ChevronStargate(MinecraftServer server, Stargate dialingStargate, Address address, boolean doKawoosh, boolean mustBeLoaded)
	{
		return getStargateFromAddress(server, dialingStargate, address, doKawoosh, mustBeLoaded);
	}
	
	private static StargateInfo.Feedback getPreferredStargate(MinecraftServer server, Stargate dialingStargate, AddressRegion.Serializable addressRegion, Address.Type addressType, boolean doKawoosh, boolean mustBeLoaded)
	{
		List<Stargate> stargates = addressRegion.getStargates();
		
		if(stargates.isEmpty())
			return dialingStargate.resetStargate(server, StargateInfo.Feedback.NO_STARGATES, true);
		
		// Primary Stargate
		if(CommonStargateNetworkConfig.primary_stargate.get() && addressRegion.primaryStargate() != null)
		{
			StargateInfo.Feedback feedback = attemptConnection(server, dialingStargate, addressRegion.primaryStargate(), addressType, doKawoosh, mustBeLoaded);
			
			// If Stargate isn't obstructed and its network isn't restricted, connect
			if(!feedback.isSkippable())
				return feedback;
		}
		
		StargateInfo.Feedback feedback = StargateInfo.Feedback.UNKNOWN_ERROR;
		
		// Preferred Stargate
		for(Stargate targetStargate : stargates)
		{
			feedback = attemptConnection(server, dialingStargate, targetStargate, addressType, doKawoosh, mustBeLoaded);
			
			// If Stargate isn't obstructed and its network isn't restricted, connect
			if(!feedback.isSkippable())
				return feedback;
		}
		
		if(feedback == StargateInfo.Feedback.UNKNOWN_ERROR)
			StargateJourney.LOGGER.error("Address Region has Stargates, but somehow none can be accessed");
		return dialingStargate.resetStargate(server, feedback, true);
	}
	
	public static StargateInfo.Feedback connectStargates(MinecraftServer server, Stargate dialingStargate, Stargate dialedStargate, Address.Type addressType, boolean doKawoosh)
	{
		return StargateNetwork.get(server).createConnection(server, dialingStargate, dialedStargate, addressType, doKawoosh);
	}
}
