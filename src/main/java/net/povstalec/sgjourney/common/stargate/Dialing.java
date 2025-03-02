package net.povstalec.sgjourney.common.stargate;

import java.util.List;
import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.events.custom.SGJourneyEvents;

public class Dialing
{
	public static final int[] DIALED_7_CHEVRON_CONFIGURATION = new int [] {1, 2, 3, 6, 7, 8, 4, 5};
	public static final int[] DIALED_8_CHEVRON_CONFIGURATION = new int [] {1, 2, 3, 4, 6, 7, 8, 5};
	public static final int[] DIALED_9_CHEVRON_CONFIGURATION = new int [] {1, 2, 3, 4, 5, 6, 7, 8};
	
	public static final int[] DEFAULT_CHEVRON_CONFIGURATION = DIALED_7_CHEVRON_CONFIGURATION;
	
	public static Stargate.Feedback dialStargate(ServerLevel level, Stargate dialingStargate,
			Address.Immutable address, Address.Immutable dialingAddress, boolean doKawoosh)
	{
		if(SGJourneyEvents.onStargateDial(level.getServer(), dialingStargate, address, dialingAddress, doKawoosh))
			return Stargate.Feedback.NONE;
		
		switch(address.getLength())
		{
		case 6:
			return get7ChevronStargate(level, dialingStargate, address, dialingAddress, doKawoosh);
		case 7:
			return get8ChevronStargate(level, dialingStargate, address, dialingAddress, doKawoosh);
		case 8:
			return get9ChevronStargate(level, dialingStargate, address, dialingAddress, doKawoosh);
		default:
			return dialingStargate.resetStargate(level.getServer(), Stargate.Feedback.INVALID_ADDRESS);
		}
	}
	
	private static Stargate.Feedback get7ChevronStargate(ServerLevel level, Stargate dialingStargate, 
		Address.Immutable dialedAddress, Address.Immutable dialingAddress, boolean doKawoosh)
	{
		Optional<SolarSystem.Serializable> solarSystem = Universe.get(level).getSolarSystemFromAddress(level.dimension(), dialedAddress);
		
		if(solarSystem.isEmpty())
			return dialingStargate.resetStargate(level.getServer(), Stargate.Feedback.INVALID_ADDRESS);
		
		return getStargate(level, dialingStargate, solarSystem.get(), Address.Type.ADDRESS_7_CHEVRON, dialingAddress, doKawoosh);
	}
	
	private static Stargate.Feedback get8ChevronStargate(ServerLevel level, Stargate dialingStargate,
			Address.Immutable extragalacticAddress, Address.Immutable dialingAddress, boolean doKawoosh)
	{
		Optional<SolarSystem.Serializable> solarSystem = Universe.get(level).getSolarSystemFromExtragalacticAddress(extragalacticAddress);
		
		if(solarSystem.isEmpty())
			return dialingStargate.resetStargate(level.getServer(), Stargate.Feedback.INVALID_ADDRESS);
		
		return getStargate(level, dialingStargate, solarSystem.get(), Address.Type.ADDRESS_8_CHEVRON, dialingAddress, doKawoosh);
	}
	
	private static Stargate.Feedback getStargate(ServerLevel level, Stargate dialingStargate, SolarSystem.Serializable dialedSystem,
			Address.Type addressType, Address.Immutable dialingAddress, boolean doKawoosh)
	{
		Optional<SolarSystem.Serializable> currentSystem = Universe.get(level).getSolarSystemFromDimension(level.dimension());
		
		MinecraftServer server = level.getServer();
		
		if(currentSystem.isPresent() && dialedSystem.equals(currentSystem.get()))
			return dialingStargate.resetStargate(server, Stargate.Feedback.SAME_SYSTEM_DIAL);
		
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
				return dialingStargate.resetStargate(server, Stargate.Feedback.NO_DIMENSIONS);
		}
		
		return getPreferredStargate(level, dialingStargate, dialedSystem, addressType, dialingAddress, doKawoosh);
	}
	
	private static Stargate.Feedback getStargateFromAddress(MinecraftServer server, Stargate dialingStargate,
			Address.Immutable address, Address.Immutable dialingAddress, boolean doKawoosh)
	{
		Optional<Stargate> stargate = StargateNetwork.get(server).getStargate(address);
		
		if(stargate.isEmpty())
			return dialingStargate.resetStargate(server, Stargate.Feedback.INVALID_ADDRESS);
		
		BlockPos pos = stargate.get().getBlockPos();
		ResourceKey<Level> dimension = stargate.get().getDimension();
		
		if(pos != null && dimension != null)
		{
			if(server.getLevel(dimension).getBlockEntity(pos) instanceof AbstractStargateEntity targetStargate)
			{
				if(targetStargate.isObstructed())
					return dialingStargate.resetStargate(server, Stargate.Feedback.TARGET_OBSTRUCTED);
				
				else if(targetStargate.addressFilterInfo().getFilterType().shouldFilter())
				{
					if(targetStargate.addressFilterInfo().getFilterType().isBlacklist() && targetStargate.addressFilterInfo().isAddressBlacklisted(dialingAddress))
						return dialingStargate.resetStargate(server, Stargate.Feedback.BLACKLISTED_SELF);
					
					else if(targetStargate.addressFilterInfo().getFilterType().isWhitelist() && !targetStargate.addressFilterInfo().isAddressWhitelisted(dialingAddress))
						return dialingStargate.resetStargate(server, Stargate.Feedback.WHITELISTED_SELF);
				}
				
				return connectStargates(server, dialingStargate, stargate.get(), Address.Type.ADDRESS_9_CHEVRON, doKawoosh);
			}
		}
		
		return dialingStargate.resetStargate(server, Stargate.Feedback.COULD_NOT_REACH_TARGET_STARGATE);
	}
	
	private static Stargate.Feedback get9ChevronStargate(ServerLevel level, Stargate dialingStargate,
			Address.Immutable address, Address.Immutable dialingAddress, boolean doKawoosh)
	{
		return getStargateFromAddress(level.getServer(), dialingStargate, address, dialingAddress, doKawoosh);
	}
	
	private static Stargate.Feedback getPreferredStargate(ServerLevel level, Stargate dialingStargate, SolarSystem.Serializable solarSystem, Address.Type addressType, Address.Immutable dialingAddress, boolean doKawoosh)
	{
		MinecraftServer server = level.getServer();
		List<Stargate> stargates = solarSystem.getStargates();
		
		if(stargates.isEmpty())
			return dialingStargate.resetStargate(server, Stargate.Feedback.NO_STARGATES);
		else
		{
			for(int i = 0; i < stargates.size(); i++)
			{
				boolean isLastStargate = i == stargates.size() - 1;
				Stargate targetStargate = stargates.get(i);
				
				if(server.getLevel(targetStargate.getDimension()).getBlockEntity(targetStargate.getBlockPos()) instanceof AbstractStargateEntity targetStargateEntity)
				{
					// If Stargate isn't obstructed and it's network isn't restricted, connect
					if(!targetStargateEntity.isObstructed() && !targetStargateEntity.isRestricted(dialingStargate.getNetwork()) &&
							!(targetStargateEntity.addressFilterInfo().getFilterType().isBlacklist() && targetStargateEntity.addressFilterInfo().isAddressBlacklisted(dialingAddress)) &&
							!(targetStargateEntity.addressFilterInfo().getFilterType().isWhitelist() && !targetStargateEntity.addressFilterInfo().isAddressWhitelisted(dialingAddress)))
						return connectStargates(level.getServer(), dialingStargate, targetStargate, addressType, doKawoosh);
					
					// If last Stargate is obstructed
					else if(targetStargateEntity.isObstructed() && isLastStargate)
						return dialingStargate.resetStargate(server, Stargate.Feedback.TARGET_OBSTRUCTED);
					
					// If last Stargate is restricted
					else if(targetStargateEntity.isRestricted(dialingStargate.getNetwork()) && isLastStargate)
						return dialingStargate.resetStargate(server, Stargate.Feedback.TARGET_RESTRICTED);

					// If last Stargate has a blacklist
					else if(targetStargateEntity.addressFilterInfo().getFilterType().isBlacklist() && targetStargateEntity.addressFilterInfo().isAddressBlacklisted(dialingAddress) && isLastStargate)
							return dialingStargate.resetStargate(server, Stargate.Feedback.BLACKLISTED_SELF);

					// If last Stargate has a whitelist
					else if(targetStargateEntity.addressFilterInfo().getFilterType().isWhitelist() && !targetStargateEntity.addressFilterInfo().isAddressWhitelisted(dialingAddress) && isLastStargate)
						return dialingStargate.resetStargate(server, Stargate.Feedback.WHITELISTED_SELF);
				}
			}
		}
		
		return dialingStargate.resetStargate(server, Stargate.Feedback.UNKNOWN_ERROR);
	}
	
	public static Stargate.Feedback connectStargates(MinecraftServer server, Stargate dialingStargate, Stargate dialedStargate, Address.Type addressType, boolean doKawoosh)
	{
		return StargateNetwork.get(server).createConnection(server, dialingStargate, dialedStargate, addressType, doKawoosh);
	}
}
