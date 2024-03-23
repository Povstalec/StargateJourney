package net.povstalec.sgjourney.common.stargate;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
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
import net.povstalec.sgjourney.common.init.TagInit;
import net.povstalec.sgjourney.common.misc.Conversion;

public class Dialing
{
	private static final String EMPTY = StargateJourney.EMPTY;
	
	public static final int[] DIALED_7_CHEVRON_CONFIGURATION = new int [] {0, 1, 2, 3, 6, 7, 8, 4, 5};
	public static final int[] DIALED_8_CHEVRON_CONFIGURATION = new int [] {0, 1, 2, 3, 4, 6, 7, 8, 5};
	public static final int[] DIALED_9_CHEVRON_CONFIGURATION = new int [] {0, 1, 2, 3, 4, 5, 6, 7, 8};
	
	public static final int[] DEFAULT_CHEVRON_CONFIGURATION = DIALED_7_CHEVRON_CONFIGURATION;
	
	public static Stargate.Feedback dialStargate(Level level, AbstractStargateEntity dialingStargate, Address address, boolean doKawoosh)
	{
		switch(address.getLength())
		{
		case 6:
			return get7ChevronStargate(level, dialingStargate, address, doKawoosh);
		case 7:
			return get8ChevronStargate(level, dialingStargate, address, doKawoosh);
		case 8:
			return get9ChevronStargate(level, dialingStargate, address, doKawoosh);
		default:
			return dialingStargate.resetStargate(Stargate.Feedback.INVALID_ADDRESS);
		}
	}
	
	private static Stargate.Feedback get7ChevronStargate(Level level, AbstractStargateEntity dialingStargate, Address address, boolean doKawoosh)
	{
		//String addressString = address.toString();
		
		// List of Galaxies the dialing Dimension is located in
		Optional<SolarSystem.Serializable> solarSystem = Universe.get(level).getSolarSystemFromAddress(level.dimension(), address);
		
		/*if(galaxies.isEmpty())
			return dialingStargate.resetStargate(Stargate.Feedback.NO_GALAXY);
		
		String solarSystem = EMPTY;
		
		for(int i = 0; i < galaxies.size(); i++)
		{
			String galaxy = galaxies.getCompound(i).getAllKeys().iterator().next();
			
			solarSystem = Universe.get(level).getSolarSystemInGalaxy(galaxy, addressString);
			
			if(!solarSystem.equals(EMPTY))
				break;
		}*/
		
		if(solarSystem.isEmpty())
			return dialingStargate.resetStargate(Stargate.Feedback.INVALID_ADDRESS);
		
		return getStargate(level, dialingStargate, solarSystem.get(), Address.Type.ADDRESS_7_CHEVRON, doKawoosh);
	}
	
	private static Stargate.Feedback get8ChevronStargate(Level level, AbstractStargateEntity dialingStargate, Address extragalacticAddress, boolean doKawoosh)
	{
		Optional<SolarSystem.Serializable> solarSystem = Universe.get(level).getSolarSystemFromExtragalacticAddress(extragalacticAddress);
		
		if(solarSystem.isEmpty())
			return dialingStargate.resetStargate(Stargate.Feedback.INVALID_ADDRESS);
		
		return getStargate(level, dialingStargate, solarSystem.get(), Address.Type.ADDRESS_8_CHEVRON, doKawoosh);
	}
	
	private static Stargate.Feedback getStargate(Level level, AbstractStargateEntity dialingStargate, SolarSystem.Serializable dialedSystem, Address.Type addressType, boolean doKawoosh)
	{
		Optional<SolarSystem.Serializable> currentSystem = Universe.get(level).getSolarSystemFromDimension(level.dimension());
		
		if(currentSystem.isPresent() && dialedSystem.equals(currentSystem.get()))
			return dialingStargate.resetStargate(Stargate.Feedback.SAME_SYSTEM_DIAL);
		
		MinecraftServer server = level.getServer();
		
		//CompoundTag solarSystem = StargateNetwork.get(server).getSolarSystem(systemID);
		
		if(dialedSystem.getStargates().isEmpty())
		{
			List<ResourceKey<Level>> dimensionList = dialedSystem.getDimensions();
			
			
			//ListTag dimensionList = Universe.get(server).getDimensionsFromSolarSystem(systemID);
			
			// Cycles through the list of Dimensions in the Solar System
			int dimensions = 0;
			for(int i = 0; i < dimensionList.size(); i++)
			{
				ResourceKey<Level> levelKey = dimensionList.get(i);
				
				if(level.getServer().levelKeys().contains(levelKey))
				{
					Level targetLevel = server.getLevel(levelKey);
					findStargates(targetLevel);
					dimensions++;
				}
			}
			
			if(dimensions == 0)
				return dialingStargate.resetStargate(Stargate.Feedback.NO_DIMENSIONS);
			
			//solarSystem = StargateNetwork.get(server).getSolarSystem(systemID);
			//if(dialedSystem.getStargates().isEmpty())
			//	return dialingStargate.resetStargate(Stargate.Feedback.NO_STARGATES);
			
			//solarSystem = StargateNetwork.get(server).getSolarSystem(systemID);
		}
		
		return getPreferredStargate(server, dialingStargate, dialedSystem, addressType, doKawoosh);
	}
	
	private static void findStargates(Level level)
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
	
	public static Stargate.Feedback getStargateFromAddress(MinecraftServer server, AbstractStargateEntity dialingStargate, Address address, boolean doKawoosh)
	{
		Optional<Stargate> stargate = StargateNetwork.get(server).getStargate(address);
		
		if(stargate.isEmpty())
			return dialingStargate.resetStargate(Stargate.Feedback.COULD_NOT_REACH_TARGET_STARGATE);
		
		BlockPos pos = stargate.get().getBlockPos();
		ResourceKey<Level> dimension = stargate.get().getDimension();
		
		if(pos != null && dimension != null)
		{
			if(server.getLevel(dimension).getBlockEntity(pos) instanceof AbstractStargateEntity targetStargate)
			{
				if(targetStargate.isObstructed())
					return dialingStargate.resetStargate(Stargate.Feedback.TARGET_OBSTRUCTED);
				
				return dialStargate(dialingStargate, targetStargate, Address.Type.ADDRESS_9_CHEVRON, doKawoosh);
			}
		}
		
		return dialingStargate.resetStargate(Stargate.Feedback.COULD_NOT_REACH_TARGET_STARGATE);
	}
	
	public static Stargate.Feedback get9ChevronStargate(Level level, AbstractStargateEntity dialingStargate, Address address, boolean doKawoosh)
	{
		return getStargateFromAddress(level.getServer(), dialingStargate, address, doKawoosh);
	}
	
	private static Stargate.Feedback getPreferredStargate(MinecraftServer server, AbstractStargateEntity dialingStargate, SolarSystem.Serializable solarSystem, Address.Type addressType, boolean doKawoosh)
	{
		List<Stargate> stargates = solarSystem.getStargates();
		
		if(stargates.isEmpty())
			return dialingStargate.resetStargate(Stargate.Feedback.NO_STARGATES);
		else
		{
			for(int i = 0; i < stargates.size(); i++)
			{
				boolean isLastStargate = i == stargates.size() - 1;
				Stargate stargate = stargates.get(i);
				
				if(server.getLevel(stargate.getDimension()).getBlockEntity(stargate.getBlockPos()) instanceof AbstractStargateEntity targetStargate)
				{
					if(!targetStargate.isObstructed() && !targetStargate.isRestricted(dialingStargate))
						return dialStargate(dialingStargate, targetStargate, addressType, doKawoosh);
					else if(targetStargate.isObstructed() && isLastStargate)
						return dialingStargate.resetStargate(Stargate.Feedback.TARGET_OBSTRUCTED);
					else if(targetStargate.isRestricted(dialingStargate) && isLastStargate)
						return dialingStargate.resetStargate(Stargate.Feedback.TARGET_RESTRICTED);
				}
			}
		}
		
		/*while(!solarSystem.isEmpty())
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
					if(!targetStargate.isObstructed() && !targetStargate.isRestricted(dialingStargate))
						return dialStargate(dialingStargate, targetStargate, addressType, doKawoosh);
					else if(targetStargate.isObstructed() && solarSystem.size() == 1)
						return dialingStargate.resetStargate(Stargate.Feedback.TARGET_OBSTRUCTED);
					else if(targetStargate.isRestricted(dialingStargate) && solarSystem.size() == 1)
						return dialingStargate.resetStargate(Stargate.Feedback.TARGET_RESTRICTED);
						
				}
			}
			solarSystem.remove(preferredStargate);
		}*/
		
		return dialingStargate.resetStargate(Stargate.Feedback.UNKNOWN_ERROR);
	}
	
	private static Stargate.Feedback dialStargate(AbstractStargateEntity dialingStargate, AbstractStargateEntity dialedStargate, Address.Type addressType, boolean doKawoosh)
	{
		Level level = dialingStargate.getLevel();
		return StargateNetwork.get(level).createConnection(level.getServer(), dialingStargate, dialedStargate, addressType, doKawoosh);
	}
}
