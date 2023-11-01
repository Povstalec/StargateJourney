package net.povstalec.sgjourney.common.stargate;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.CommonStargateNetworkConfig;
import net.povstalec.sgjourney.common.data.StargateNetwork;
import net.povstalec.sgjourney.common.data.Universe;
import net.povstalec.sgjourney.common.init.TagInit;
import net.povstalec.sgjourney.common.misc.Conversion;

public class Dialing
{
	private static final String EMPTY = StargateJourney.EMPTY;
	
	public static final int[] DIALED_7_CHEVRON_CONFIGURATION = new int [] {0, 1, 2, 3, 6, 7, 8, 4, 5};
	public static final int[] DIALED_8_CHEVRON_CONFIGURATION = new int [] {0, 1, 2, 3, 4, 6, 7, 8, 5};
	public static final int[] DIALED_9_CHEVRON_CONFIGURATION = new int [] {0, 1, 2, 3, 4, 5, 6, 7, 8};
	
	public static final int[] DIALING_CHEVRON_CONFIGURATION = DIALED_7_CHEVRON_CONFIGURATION;
	
	public static Stargate.Feedback dialStargate(Level level, AbstractStargateEntity stargate)
	{
		Address address = stargate.getAddress();
		
		switch(address.getLength())
		{
		case 6:
			return get7ChevronStargate(level, stargate, address);
		case 7:
			return get8ChevronStargate(level, stargate, address);
		case 8:
			return get9ChevronStargate(level, stargate, address);
		default:
			return stargate.resetStargate(Stargate.Feedback.INVALID_ADDRESS);
		}
	}
	
	private static Stargate.Feedback get7ChevronStargate(Level level, AbstractStargateEntity stargate, Address address)
	{
		String addressString = address.toString();
		
		// List of Galaxies the dialing Dimension is located in
		ListTag galaxies = Universe.get(level).getGalaxiesFromDimension(level.dimension().location().toString());
		
		if(galaxies.isEmpty())
			return stargate.resetStargate(Stargate.Feedback.NO_GALAXY);
		
		String solarSystem = EMPTY;
		
		for(int i = 0; i < galaxies.size(); i++)
		{
			String galaxy = galaxies.getCompound(i).getAllKeys().iterator().next();
			
			solarSystem = Universe.get(level).getSolarSystemInGalaxy(galaxy, addressString);
			
			if(!solarSystem.equals(EMPTY))
				break;
		}
		
		if(solarSystem.equals(EMPTY))
			return stargate.resetStargate(Stargate.Feedback.INVALID_ADDRESS);
		
		return getStargate(level, stargate, solarSystem);
	}
	
	private static Stargate.Feedback get8ChevronStargate(Level level, AbstractStargateEntity stargate, Address address)
	{
		String addressString = address.toString();
		String solarSystem = Universe.get(level).getSolarSystemFromExtragalacticAddress(addressString);
		
		if(solarSystem.equals(EMPTY))
			return stargate.resetStargate(Stargate.Feedback.INVALID_ADDRESS);
		
		return getStargate(level, stargate, solarSystem);
	}
	
	private static Stargate.Feedback getStargate(Level level, AbstractStargateEntity stargate, String systemID)
	{
		String currentSystem = Universe.get(level).getSolarSystemFromDimension(level.dimension().location().toString());
		
		if(systemID.equals(currentSystem))
			return stargate.resetStargate(Stargate.Feedback.SAME_SYSTEM_DIAL);
		
		MinecraftServer server = level.getServer();
		
		CompoundTag solarSystem = StargateNetwork.get(server).getSolarSystem(systemID);
		
		if(solarSystem.isEmpty())
		{
			ListTag dimensionList = Universe.get(server).getDimensionsFromSolarSystem(systemID);
			
			// Cycles through the list of Dimensions in the Solar System
			int dimensions = 0;
			for(int i = 0; i < dimensionList.size(); i++)
			{
				ResourceKey<Level> levelKey = Conversion.stringToDimension(dimensionList.getString(i));
				
				if(level.getServer().levelKeys().contains(levelKey))
				{
					Level targetLevel = server.getLevel(levelKey);
					findStargates(targetLevel);
					dimensions++;
				}
			}
			
			if(dimensions == 0)
				return stargate.resetStargate(Stargate.Feedback.NO_DIMENSIONS);
			
			solarSystem = StargateNetwork.get(server).getSolarSystem(systemID);
			if(solarSystem.isEmpty())
				return stargate.resetStargate(Stargate.Feedback.NO_STARGATES);
			
			solarSystem = StargateNetwork.get(server).getSolarSystem(systemID);
		}
		
		return getPreferredStargate(server, stargate, solarSystem);
	}
	
	private static void findStargates(Level level)
	{
		StargateJourney.LOGGER.info("Attempting to locate the Stargate Structure in " + level.dimension().location().toString());
		
		int xOffset = CommonStargateNetworkConfig.stargate_generation_center_x_chunk_offset.get();
        int zOffset = CommonStargateNetworkConfig.stargate_generation_center_z_chunk_offset.get();
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
	
	public static Stargate.Feedback getStargateFromID(MinecraftServer server, AbstractStargateEntity stargate, String id)
	{
		CompoundTag stargateList = StargateNetwork.get(server).getStargates();
		
		if(!stargateList.contains(id))
			return stargate.resetStargate(Stargate.Feedback.INVALID_ADDRESS);
		
		BlockPos pos = Conversion.intArrayToBlockPos(stargateList.getCompound(id).getIntArray("Coordinates"));
		
		if(server.getLevel(Conversion.stringToDimension(stargateList.getCompound(id).getString("Dimension"))).getBlockEntity(pos) instanceof AbstractStargateEntity targetStargate)
		{
			if(targetStargate.isObstructed())
				return stargate.resetStargate(Stargate.Feedback.TARGET_OBSTRUCTED);
			return dialStargate(stargate, targetStargate);
		}
		return stargate.resetStargate(Stargate.Feedback.COULD_NOT_REACH_TARGET_STARGATE);
	}
	
	public static Stargate.Feedback get9ChevronStargate(Level level, AbstractStargateEntity stargate, Address address)
	{
		String id = address.toString();
		return getStargateFromID(level.getServer(), stargate, id);
	}
	
	private static Stargate.Feedback getPreferredStargate(MinecraftServer server, AbstractStargateEntity stargate, CompoundTag solarSystem)
	{
		while(!solarSystem.isEmpty())
		{
			String preferredStargate = StargateNetwork.get(server).getPreferredStargate(solarSystem);
			
			if(!preferredStargate.equals(EMPTY))
			{
				CompoundTag stargateInfo = solarSystem.getCompound(preferredStargate);
				
				int[] coordinates = stargateInfo.getIntArray("Coordinates");
				BlockPos pos = Conversion.intArrayToBlockPos(coordinates);
				ResourceKey<Level> dimension = Conversion.stringToDimension(stargateInfo.getString("Dimension"));
				ServerLevel targetLevel = server.getLevel(dimension);
				
				if(targetLevel.getBlockEntity(pos) instanceof AbstractStargateEntity targetStargate)
				{
					if(!targetStargate.isObstructed())
						return dialStargate(stargate, targetStargate);
					else if(targetStargate.isObstructed() && solarSystem.size() == 1)
						return stargate.resetStargate(Stargate.Feedback.TARGET_OBSTRUCTED);
						
				}
			}
			solarSystem.remove(preferredStargate);
		}
		
		return stargate.resetStargate(Stargate.Feedback.UNKNOWN_ERROR);
	}
	
	private static Stargate.Feedback dialStargate(AbstractStargateEntity dialingStargate, AbstractStargateEntity dialedStargate)
	{
		return dialingStargate.dialStargate(dialedStargate);
	}
}
