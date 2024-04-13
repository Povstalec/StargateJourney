package net.povstalec.sgjourney.common.stargate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.CommonGenerationConfig;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.events.custom.SGJourneyEvents;
import net.povstalec.sgjourney.common.init.TagInit;

public class Dialing
{
	public static final int[] DIALED_7_CHEVRON_CONFIGURATION = new int [] {0, 1, 2, 3, 6, 7, 8, 4, 5};
	public static final int[] DIALED_8_CHEVRON_CONFIGURATION = new int [] {0, 1, 2, 3, 4, 6, 7, 8, 5};
	public static final int[] DIALED_9_CHEVRON_CONFIGURATION = new int [] {0, 1, 2, 3, 4, 5, 6, 7, 8};
	
	public static final int[] DEFAULT_CHEVRON_CONFIGURATION = DIALED_7_CHEVRON_CONFIGURATION;
	
	public static Stargate.Feedback dialStargate(ServerLevel level, Stargate dialingStargate, Address.Immutable address, boolean doKawoosh)
	{
		if(SGJourneyEvents.onStargateDial(level.getServer(), dialingStargate, address, doKawoosh))
			return Stargate.Feedback.NONE;
		
		switch(address.getLength())
		{
		case 6:
			return get7ChevronStargate(level, dialingStargate, address, doKawoosh);
		case 7:
			return get8ChevronStargate(level, dialingStargate, address, doKawoosh);
		case 8:
			return get9ChevronStargate(level, dialingStargate, address, doKawoosh);
		default:
			return dialingStargate.resetStargate(level.getServer(), Stargate.Feedback.INVALID_ADDRESS);
		}
	}
	
	private static Stargate.Feedback get7ChevronStargate(ServerLevel level, Stargate dialingStargate, Address.Immutable address, boolean doKawoosh)
	{
		Optional<SolarSystem.Serializable> solarSystem = Universe.get(level).getSolarSystemFromAddress(level.dimension(), address);
		
		if(solarSystem.isEmpty())
			return dialingStargate.resetStargate(level.getServer(), Stargate.Feedback.INVALID_ADDRESS);
		
		return getStargate(level, dialingStargate, solarSystem.get(), Address.Type.ADDRESS_7_CHEVRON, doKawoosh);
	}
	
	private static Stargate.Feedback get8ChevronStargate(ServerLevel level, Stargate dialingStargate, Address.Immutable extragalacticAddress, boolean doKawoosh)
	{
		Optional<SolarSystem.Serializable> solarSystem = Universe.get(level).getSolarSystemFromExtragalacticAddress(extragalacticAddress);
		
		if(solarSystem.isEmpty())
			return dialingStargate.resetStargate(level.getServer(), Stargate.Feedback.INVALID_ADDRESS);
		
		return getStargate(level, dialingStargate, solarSystem.get(), Address.Type.ADDRESS_8_CHEVRON, doKawoosh);
	}
	
	private static Stargate.Feedback getStargate(ServerLevel level, Stargate dialingStargate, SolarSystem.Serializable dialedSystem, Address.Type addressType, boolean doKawoosh)
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
					findStargates((ServerLevel) targetLevel);
					dimensions++;
				}
			}
			
			if(dimensions == 0)
				return dialingStargate.resetStargate(server, Stargate.Feedback.NO_DIMENSIONS);
		}
		
		return getPreferredStargate(level, dialingStargate, dialedSystem, addressType, doKawoosh);
	}
	
	private static void findStargates(ServerLevel level)
	{
		StargateJourney.LOGGER.info("Attempting to locate the Stargate Structure in " + level.dimension().location().toString());
		
		int xOffset = CommonGenerationConfig.stargate_generation_center_x_chunk_offset.get();
        int zOffset = CommonGenerationConfig.stargate_generation_center_z_chunk_offset.get();
		//Nearest Structure that potentially has a Stargate
		BlockPos blockpos = ((ServerLevel) level).findNearestMapStructure(TagInit.Structures.HAS_STARGATE, new BlockPos(xOffset * 16, 0, zOffset * 16), 150, false);
		if(blockpos == null)
		{
			StargateJourney.LOGGER.info("Stargate Structure not found");
			return;
		}
		//Map of Block Entities that might contain a Stargate
		List<AbstractStargateEntity> stargates = new ArrayList<AbstractStargateEntity>();
		
		for(int x = -2; x <= 2; x++)
		{
			for(int z = -2; z <= 2; z++)
			{
				ChunkAccess chunk = level.getChunk(blockpos.east(16 * x).south(16 * z));
				Set<BlockPos> positions = chunk.getBlockEntitiesPos();
				
				positions.stream().forEach(pos ->
				{
					if(level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
						stargates.add(stargate);
				});
			}
		}
		
		if(stargates.isEmpty())
		{
			StargateJourney.LOGGER.info("No Stargates found in Stargate Structure");
			return;
		}
		
		stargates.stream().forEach(stargate -> stargate.onLoad());
		return;
	}
	
	private static Stargate.Feedback getStargateFromAddress(MinecraftServer server, Stargate dialingStargate, Address.Immutable address, boolean doKawoosh)
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
				
				return connectStargates(server, dialingStargate, stargate.get(), Address.Type.ADDRESS_9_CHEVRON, doKawoosh);
			}
		}
		
		return dialingStargate.resetStargate(server, Stargate.Feedback.COULD_NOT_REACH_TARGET_STARGATE);
	}
	
	private static Stargate.Feedback get9ChevronStargate(ServerLevel level, Stargate dialingStargate, Address.Immutable address, boolean doKawoosh)
	{
		return getStargateFromAddress(level.getServer(), dialingStargate, address, doKawoosh);
	}
	
	private static Stargate.Feedback getPreferredStargate(ServerLevel level, Stargate dialingStargate, SolarSystem.Serializable solarSystem, Address.Type addressType, boolean doKawoosh)
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
					if(!targetStargateEntity.isObstructed() && !targetStargateEntity.isRestricted(dialingStargate.getNetwork()))
						return connectStargates(level.getServer(), dialingStargate, targetStargate, addressType, doKawoosh);
					// If last Stargate is obstructed
					else if(targetStargateEntity.isObstructed() && isLastStargate)
						return dialingStargate.resetStargate(server, Stargate.Feedback.TARGET_OBSTRUCTED);
					// If last Stargate is restricted
					else if(targetStargateEntity.isRestricted(dialingStargate.getNetwork()) && isLastStargate)
						return dialingStargate.resetStargate(server, Stargate.Feedback.TARGET_RESTRICTED);
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
