package net.povstalec.sgjourney.stargate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.LevelChunk;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.data.BlockEntityList;
import net.povstalec.sgjourney.data.StargateNetwork;
import net.povstalec.sgjourney.init.StructureTagInit;

public class Dialing
{
	public static AbstractStargateEntity dialStargate(Level level, int[] address)
	{
		switch(address.length)
		{
		case 6:
			return get7ChevronStargate(level, address);
		case 7:
			return get8ChevronStargate(level, address);
		case 8:
			return get9ChevronStargate(level, address);
		default:
			return null;
		}
	}
	
	private static AbstractStargateEntity get7ChevronStargate(Level level, int[] address)
	{
		if(!StargateNetwork.get(level).getPlanets().getCompound(level.dimension().location().toString()).contains("Galaxy"))
		{
			StargateJourney.LOGGER.info("Planet is outside the Stargate Network");
			return null;
		}
		
		int galaxyNumber = StargateNetwork.get(level).getPlanets().getCompound(level.dimension().location().toString()).getInt("Galaxy");
		String galaxy = "Galaxy" + galaxyNumber;
		
		String addressString = Addressing.addressIntArrayToString(address);
		
		if(StargateNetwork.get(level).getDimensionFromAddress(galaxyNumber, addressString) == null)
		{
			StargateJourney.LOGGER.info("Invalid Address");
			return null;
		}
		
		Level targetLevel = level.getServer().getLevel(StargateNetwork.get(level).getDimensionFromAddress(galaxyNumber, addressString));
		
		return getPrimaryStargate(targetLevel, galaxy, addressString);
	}
	
	private static AbstractStargateEntity get8ChevronStargate(Level level, int[] address)
	{
		String galaxy = "Galaxy" + address[0];
		
		String addressString = Addressing.addressIntArrayToString(Addressing.convertTo7chevronAddress(address));
		
		if(StargateNetwork.get(level).getDimensionFromAddress(address[0], addressString) == null)
		{
			StargateJourney.LOGGER.info("Invalid Address");
			return null;
		}
		
		Level targetLevel = level.getServer().getLevel(StargateNetwork.get(level).getDimensionFromAddress(address[0], addressString));
		
		return getPrimaryStargate(targetLevel, galaxy, addressString);
	}
	
	private static AbstractStargateEntity getPrimaryStargate(Level level, String galaxy, String address)
	{
		CompoundTag planet = getPlanet(level, galaxy, address);
		
		if(planet.isEmpty())
		{
			if(!findStargates(level))
				return null;
			planet = getPlanet(level, galaxy, address);
		}
		
		String stargateID = StargateNetwork.get(level).getPrimaryStargate(galaxy, address);
		
		CompoundTag stargate = planet.getCompound(stargateID);
		
		int x = stargate.getIntArray("Coordinates")[0];
		int y = stargate.getIntArray("Coordinates")[1];
		int z = stargate.getIntArray("Coordinates")[2];
		MinecraftServer server = level.getServer();
		BlockPos pos = new BlockPos(x, y, z);
		
		if(server.getLevel(stringToDimension(stargate.getString("Dimension"))).getBlockEntity(pos) instanceof AbstractStargateEntity stargateEntity)
		{
			StargateJourney.LOGGER.info("Getting primary Stargate " + stargateID);
			return stargateEntity;
		}
		
		StargateJourney.LOGGER.info("Dimension has no registered Stargates");
		return null;
	}
	
	private static CompoundTag getPlanet(Level level, String galaxy, String address)
	{
		return StargateNetwork.get(level).getStargates().getCompound(galaxy).getCompound(address);
	}
	
	private static boolean findStargates(Level level)
	{
		System.out.println("No Stargates found, attempting to locate the Stargate Structure");

		//Nearest Structure that potentially has a Stargate
		BlockPos blockpos = ((ServerLevel) level).findNearestMapStructure(StructureTagInit.HAS_STARGATE, new BlockPos(0, 0, 0), 150, false);
		if(blockpos == null)
		{
			System.out.println("Stargate Structure not found");
			return false;
		}
		System.out.println("X: " + blockpos.getX() + " Y: " + blockpos.getY() + " Z: " + blockpos.getZ());
		//Map of Block Entities that might contain a Stargate
		LevelChunk chunk = level.getChunkAt(blockpos);
		int chunkX = chunk.getPos().x;
		int chunkZ = chunk.getPos().z;
		Map<BlockPos, BlockEntity> blockentityMap = chunk.getBlockEntities();
		for(int i = chunkX - 2; i <= chunkX + 2; i++)
		{
			for(int j = chunkZ - 2; j <= chunkZ + 2; j++)
			{
				blockentityMap.putAll(level.getChunk(i, j).getBlockEntities());
			}
		}
		
		if(blockentityMap.isEmpty())
		{
			System.out.println("No Stargates found in Stargate Structure");
			return false;
		}
		System.out.println("Block Entity Map: " + blockentityMap);
		blockentityMap.forEach((pos, stargate) -> loadStargate(pos, stargate));
		return true;
	}
	
	private static void loadStargate(BlockPos pos, BlockEntity entity)
	{
		if(entity instanceof AbstractStargateEntity stargate)
		{
			stargate.onLoad();
			StargateJourney.LOGGER.info("Adding Stargate " + stargate.getID() + " to " + entity.getLevel().dimension().location().toString());
		}
	}
	
	public static AbstractStargateEntity getStargateFromID(Level level, String id)
	{
		CompoundTag stargateList = BlockEntityList.get(level).getBlockEntities("Stargates");
		
		if(!stargateList.contains(id))
		{
			StargateJourney.LOGGER.info("Address is not valid");
			return null;
		}
		
		int x = stargateList.getCompound(id).getIntArray("Coordinates")[0];
		int y = stargateList.getCompound(id).getIntArray("Coordinates")[1];
		int z = stargateList.getCompound(id).getIntArray("Coordinates")[2];
		
		MinecraftServer server = level.getServer();
		BlockPos pos = new BlockPos(x, y, z);
		
		if(server.getLevel(stringToDimension(stargateList.getCompound(id).getString("Dimension"))).getBlockEntity(pos) instanceof AbstractStargateEntity stargate)
		{
			StargateJourney.LOGGER.info("Getting Stargate from 9 Chevron address " + stargate);
			return stargate;
		}
		StargateJourney.LOGGER.info("Failed to get Stargate from 9 Chevron address");
		return null;
	}
	
	public static AbstractStargateEntity get9ChevronStargate(Level level, int[] address)
	{
		String id = Addressing.addressIntArrayToString(address);
		return getStargateFromID(level, id);
	}
	
	public static ResourceKey<Level> stringToDimension(String dimensionString)
	{
		String[] split = dimensionString.split(":");
		return ResourceKey.create(ResourceKey.createRegistryKey(new ResourceLocation("minecraft", "dimension")), new ResourceLocation(split[0], split[1]));
	}
	
	private static String getFirstStargate(CompoundTag planet)
	{
		Set<String> stargateKeys = planet.getAllKeys();
		List<String> stargateList = new ArrayList<>(stargateKeys);
		return stargateList.get(0);
	}
}
