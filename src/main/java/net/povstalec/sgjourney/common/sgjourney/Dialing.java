package net.povstalec.sgjourney.common.sgjourney;

import java.util.List;
import java.util.Objects;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Vec3i;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.data.StargateNetworkSettings;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.events.custom.SGJourneyEvents;
import net.povstalec.sgjourney.common.sgjourney.stargate.Stargate;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

public class Dialing
{
	public static final int[] DIALED_7_CHEVRON_CONFIGURATION = new int [] {1, 2, 3, 6, 7, 8, 4, 5};
	public static final int[] DIALED_8_CHEVRON_CONFIGURATION = new int [] {1, 2, 3, 4, 6, 7, 8, 5};
	public static final int[] DIALED_9_CHEVRON_CONFIGURATION = new int [] {1, 2, 3, 4, 5, 6, 7, 8};
	
	public static final int[] DEFAULT_CHEVRON_CONFIGURATION = DIALED_7_CHEVRON_CONFIGURATION;
	
	//============================================================================================
	//******************************************Stargate******************************************
	//============================================================================================
	
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
	
	public static StargateInfo.FeedbackMessage dialStargate(MinecraftServer server, Stargate dialingStargate, Address address, boolean doKawoosh)
	{
		if(dialingStargate.addressFilterInfo().getFilterType().shouldFilter())
		{
			if(dialingStargate.addressFilterInfo().getFilterType().isBlacklist() && dialingStargate.addressFilterInfo().isAddressBlacklisted(address))
				return dialingStargate.resetStargate(StargateInfo.Feedback.TARGET_BLACKLISTED);
			
			else if(dialingStargate.addressFilterInfo().getFilterType().isWhitelist() && !dialingStargate.addressFilterInfo().isAddressWhitelisted(address))
				return dialingStargate.resetStargate(StargateInfo.Feedback.TARGET_NOT_WHITELISTED);
		}
		
		return dialStargate(server, dialingStargate, address, doKawoosh, false);
	}
	
	public static StargateInfo.FeedbackMessage dialStargate(MinecraftServer server, Stargate dialingStargate, Address address, boolean doKawoosh, boolean mustBeLoaded)
	{
		if(SGJourneyEvents.onStargateDial(server, dialingStargate, address, doKawoosh))
			return StargateInfo.Feedback.NONE.withInfo();
		
		if(!SpaceLocation.fromDimension(server, dialingStargate.getDimension()).isInStargateNetwork())
			return dialingStargate.resetStargate(StargateInfo.Feedback.SELF_OUTSIDE_STARGATE_NETWORK);
		
		return switch(address.getType())
		{
			case ADDRESS_7_CHEVRON -> get7ChevronStargate(server, dialingStargate, address, doKawoosh, mustBeLoaded);
			case ADDRESS_8_CHEVRON -> get8ChevronStargate(server, dialingStargate, address, doKawoosh, mustBeLoaded);
			case ADDRESS_9_CHEVRON -> get9ChevronStargate(server, dialingStargate, address, doKawoosh, mustBeLoaded);
			case ADDRESS_INVALID -> dialingStargate.resetStargate(StargateInfo.Feedback.INVALID_ADDRESS);
		};
	}
	
	private static StargateInfo.FeedbackMessage get7ChevronStargate(MinecraftServer server, Stargate dialingStargate, Address dialedAddress, boolean doKawoosh, boolean mustBeLoaded)
	{
		AddressRegion addressRegion = Universe.get(server).getSameGalaxyAddressRegion(dialingStargate.getAddressRegion(), dialedAddress);
		
		if(addressRegion == null)
			return dialingStargate.resetStargate(StargateInfo.Feedback.INVALID_ADDRESS);
		
		return getStargate(server, dialingStargate, addressRegion, Address.Type.ADDRESS_7_CHEVRON, doKawoosh, mustBeLoaded);
	}
	
	private static StargateInfo.FeedbackMessage get8ChevronStargate(MinecraftServer server, Stargate dialingStargate, Address extragalacticAddress, boolean doKawoosh, boolean mustBeLoaded)
	{
		AddressRegion addressRegion = Universe.get(server).getAddressRegionFromExtragalacticAddress(extragalacticAddress);
		
		if(addressRegion == null)
			return dialingStargate.resetStargate(StargateInfo.Feedback.INVALID_ADDRESS);
		
		return getStargate(server, dialingStargate, addressRegion, Address.Type.ADDRESS_8_CHEVRON, doKawoosh, mustBeLoaded);
	}
	
	private static StargateInfo.FeedbackMessage getStargate(MinecraftServer server, Stargate dialingStargate, AddressRegion dialedRegion, Address.Type addressType, boolean doKawoosh, boolean mustBeLoaded)
	{
		AddressRegion currentRegion = dialingStargate.getAddressRegion();
		
		if(dialedRegion.equals(currentRegion))
			return dialingStargate.resetStargate(StargateInfo.Feedback.SAME_SYSTEM_DIAL);
		
		// If the Stargate Network knows of no Stargates in this Address Region, try locating any Structures with them
		if(!mustBeLoaded && !StargateNetwork.get(server).hasStargatesInRegion(dialedRegion.getResourceKey())) // No point in loading chunks if the connection requires a loaded Stargate
		{
			// Cycles through the list of Dimensions in the Address Region
			int dimensions = 0;
			for(SpaceLocation spaceLocation : dialedRegion.getSpaceLocations())
			{
				ResourceKey<Level> levelKey = spaceLocation.getDimension();
				
				if(server.levelKeys().contains(levelKey))
				{
					StargateNetwork.findStargatesInLevel(Objects.requireNonNull(server.getLevel(levelKey)));
					dimensions++;
				}
			}
			
			if(dimensions == 0)
				return dialingStargate.resetStargate(StargateInfo.Feedback.NO_DIMENSIONS);
		}
		
		return getPreferredStargate(server, dialingStargate, dialedRegion, addressType, doKawoosh, mustBeLoaded);
	}
	
	private static StargateInfo.FeedbackMessage attemptConnection(MinecraftServer server, Stargate dialingStargate, Stargate dialedStargate, Address.Type addressType, boolean doKawoosh, boolean mustBeLoaded)
	{
		if(mustBeLoaded && !dialedStargate.isLoaded())
			return StargateInfo.Feedback.TARGET_NOT_LOADED.withInfo();
		
		// If the dialing Stargate is restricted
		if(dialingStargate.isNetworkRestricted(dialedStargate.getNetworks()))
			return StargateInfo.Feedback.SELF_RESTRICTED.withInfo();
		
		if(!SpaceLocation.fromDimension(server, dialedStargate.getDimension()).isInStargateNetwork())
			return dialingStargate.resetStargate(StargateInfo.Feedback.TARGET_OUTSIDE_STARGATE_NETWORK);
		
		return dialedStargate.tryConnect(dialingStargate, addressType, doKawoosh);
	}
	
	private static StargateInfo.FeedbackMessage getStargateFromAddress(MinecraftServer server, Stargate dialingStargate, Address address, boolean doKawoosh, boolean mustBeLoaded)
	{
		Stargate stargate = StargateNetwork.get(server).getStargate(address);
		
		if(stargate == null)
			return dialingStargate.resetStargate(StargateInfo.Feedback.INVALID_ADDRESS);
		
		StargateInfo.FeedbackMessage feedback = attemptConnection(server, dialingStargate, stargate, Address.Type.ADDRESS_9_CHEVRON, doKawoosh, mustBeLoaded);
		
		// If Stargate isn't obstructed and its network isn't restricted, connect
		if(!feedback.feedback().isSkippable())
			return feedback;
		
		return dialingStargate.resetStargate(feedback);
	}
	
	private static StargateInfo.FeedbackMessage get9ChevronStargate(MinecraftServer server, Stargate dialingStargate, Address address, boolean doKawoosh, boolean mustBeLoaded)
	{
		return getStargateFromAddress(server, dialingStargate, address, doKawoosh, mustBeLoaded);
	}
	
	private static StargateInfo.FeedbackMessage getPreferredStargate(MinecraftServer server, Stargate dialingStargate, AddressRegion addressRegion, Address.Type addressType, boolean doKawoosh, boolean mustBeLoaded)
	{
		StargateNetwork stargateNetwork = StargateNetwork.get(server);
		List<Stargate> stargates = stargateNetwork.getStargatesInRegion(addressRegion.getResourceKey());
		
		if(stargates.isEmpty())
			return dialingStargate.resetStargate(StargateInfo.Feedback.NO_STARGATES);
		
		Stargate primaryStargate = stargateNetwork.getPrimaryStargateFromAddressRegion(addressRegion.getResourceKey());
		// Primary Stargate
		if(StargateNetworkSettings.get(server).prioritizePrimaryStargates() && primaryStargate != null)
		{
			StargateInfo.FeedbackMessage feedback = attemptConnection(server, dialingStargate, primaryStargate, addressType, doKawoosh, mustBeLoaded);
			
			// If Stargate isn't obstructed and its network isn't restricted, connect
			if(!feedback.feedback().isSkippable())
				return feedback;
		}
		
		StargateInfo.FeedbackMessage feedback = StargateInfo.Feedback.UNKNOWN_ERROR.withInfo();
		
		// Preferred Stargate
		for(Stargate targetStargate : stargates)
		{
			feedback = attemptConnection(server, dialingStargate, targetStargate, addressType, doKawoosh, mustBeLoaded);
			
			// If Stargate isn't obstructed and its network isn't restricted, connect
			if(!feedback.feedback().isSkippable())
				return feedback;
		}
		
		if(feedback.feedback() == StargateInfo.Feedback.UNKNOWN_ERROR)
			StargateJourney.LOGGER.error("Address Region has Stargates, but somehow none can be accessed");
		return dialingStargate.resetStargate(feedback);
	}
	
	public static StargateInfo.FeedbackMessage connectStargates(MinecraftServer server, Stargate dialingStargate, Stargate dialedStargate, Address.Type addressType, boolean doKawoosh)
	{
		return StargateNetwork.get(server).createConnection(dialingStargate, dialedStargate, addressType, doKawoosh);
	}
	
	//============================================================================================
	//****************************************Transporter*****************************************
	//============================================================================================
	
	public static TransporterInfo.FeedbackMessage dialTransporterID(MinecraftServer server, Transporter dialingTransporter, TransporterID targetID, boolean mustBeLoaded)
	{
		if(SGJourneyEvents.onTransporterDialID(server, dialingTransporter, targetID))
			return TransporterInfo.Feedback.NONE.withInfo();
		
		Transporter target = TransporterNetwork.get(server).getTransporter(targetID);
		if(target == null)
			return dialingTransporter.resetTransporter(TransporterInfo.Feedback.INVALID_TRANSPORTER_ID);
		
		return connectionAttempt(dialingTransporter, target, mustBeLoaded);
	}
	
	public static TransporterInfo.FeedbackMessage dialTransporterCoords(MinecraftServer server, Transporter dialingTransporter, Vec3i coords, boolean mustBeLoaded)
	{
		if(SGJourneyEvents.onTransporterDialCoords(server, dialingTransporter, coords))
			return TransporterInfo.Feedback.NONE.withInfo();
		
		ServerLevel level = dialingTransporter.getLevel();
		if(level == null)
		{
			StargateJourney.LOGGER.error("Dialing Transporter is not located in any dimension");
			return dialingTransporter.resetTransporter(TransporterInfo.Feedback.UNKNOWN_ERROR);
		}
		
		BlockEntity blockEntity = dialingTransporter.getLevel().getBlockEntity(new BlockPos(coords));
		if(blockEntity instanceof AbstractTransporterEntity<?> transporterEntity)
		{
			Transporter target = transporterEntity.getTransporter();
			if(target == null)
				return dialingTransporter.resetTransporter(TransporterInfo.Feedback.NO_TRANSPORTER_AT_COORDS);
			
			return connectionAttempt(dialingTransporter, target, mustBeLoaded);
		}
		else
			return dialingTransporter.resetTransporter(TransporterInfo.Feedback.NO_TRANSPORTER_AT_COORDS);
	}
	
	private static TransporterInfo.FeedbackMessage connectionAttempt(Transporter initiatingTransporter, Transporter targetTransporter, boolean mustBeLoaded)
	{
		if(mustBeLoaded && !targetTransporter.isLoaded())
			return TransporterInfo.Feedback.TARGET_NOT_LOADED.withInfo();
		
		// If the initiating Transporter is restricted
		if(initiatingTransporter.isNetworkRestricted(targetTransporter.getNetworks()))
			return TransporterInfo.Feedback.SELF_RESTRICTED.withInfo();
		
		if(initiatingTransporter.transporterIDFilterInfo().getFilterType().shouldFilter())
		{
			if(initiatingTransporter.transporterIDFilterInfo().getFilterType().isBlacklist() && initiatingTransporter.transporterIDFilterInfo().isIDBlacklisted(targetTransporter.getID()))
				return initiatingTransporter.resetTransporter(TransporterInfo.Feedback.TARGET_BLACKLISTED);
			
			else if(initiatingTransporter.transporterIDFilterInfo().getFilterType().isWhitelist() && !initiatingTransporter.transporterIDFilterInfo().isIDWhitelisted(targetTransporter.getID()))
				return initiatingTransporter.resetTransporter(TransporterInfo.Feedback.TARGET_NOT_WHITELISTED);
		}
		
		return targetTransporter.tryConnect(initiatingTransporter);
	}
	
	public static TransporterInfo.FeedbackMessage connectTransporters(MinecraftServer server, Transporter transporterA, Transporter transporterB)
	{
		return TransporterNetwork.get(server).createConnection(transporterA, transporterB);
	}
	
	
}
