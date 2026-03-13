package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.TransportRingsEntity;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

import javax.annotation.Nullable;
import java.util.*;

public class LocatorHelper
{
	//============================================================================================
	//******************************************Stargate******************************************
	//============================================================================================
	
	public static List<AbstractStargateEntity> getNearbyStargates(Level level, BlockPos centerPos, long maxDistance)
	{
		List<AbstractStargateEntity> stargates = new ArrayList<>();
		
		int chunkX = SectionPos.blockToSectionCoord(centerPos.getX());
		int chunkZ = SectionPos.blockToSectionCoord(centerPos.getZ());
		int chunkDistance = SectionPos.blockToSectionCoord(maxDistance);
		
		for(int x = chunkX - chunkDistance; x <= chunkX + chunkDistance; x++)
		{
			for(int z = chunkZ - chunkDistance; z <= chunkZ + chunkDistance; z++)
			{
				ChunkAccess chunk = level.getChunk(x, z);
				for(BlockPos pos : chunk.getBlockEntitiesPos())
				{
					if(level.getBlockEntity(pos) instanceof AbstractStargateEntity stargate && CoordinateHelper.Relative.distanceSqr(centerPos, stargate.getBlockPos()) <= maxDistance * maxDistance)
						stargates.add(stargate);
				}
			}
		}
		
		return stargates;
	}
	
	public static List<AbstractStargateEntity> getNearbyStargatesByDistance(Level level, BlockPos centerPos, long maxDistance)
	{
		List<AbstractStargateEntity> stargates = getNearbyStargates(level, centerPos, maxDistance);
		stargates.sort(Comparator.comparing(stargate -> CoordinateHelper.Relative.distance(centerPos, stargate.getBlockPos())));
		
		return stargates;
	}
	
	@Nullable
	public static AbstractStargateEntity findNearestStargate(Level level, BlockPos centerPos, long maxDistance)
	{
		List<AbstractStargateEntity> stargates = getNearbyStargatesByDistance(level, centerPos, maxDistance);
		
		if(stargates.isEmpty())
			return null;
		
		return stargates.get(0);
	}
	
	//============================================================================================
	//****************************************Transporter*****************************************
	//============================================================================================
	
	public static List<TransportRingsEntity> getNearbyTransportRings(Level level, BlockPos blockPos, int maxDistance)
	{
		List<TransportRingsEntity> transporters = new ArrayList<TransportRingsEntity>();
		
		for(int x = -maxDistance / 16; x <= maxDistance / 16; x++)
		{
			for(int z = -maxDistance / 16; z <= maxDistance / 16; z++)
			{
				ChunkAccess chunk = level.getChunk(blockPos.east(16 * x).south(16 * z));
				chunk.getBlockEntitiesPos().forEach(pos ->
				{
					if(level.getBlockEntity(pos) instanceof TransportRingsEntity transportRings)
						transporters.add(transportRings);
				});
			}
		}
		
		return transporters;
	}
	
	public static List<Transporter> findNearestTransporters(ServerLevel level, Vec3 centerPos, float maxDistance, int frequency)
	{
		float maxDistSqr = maxDistance * maxDistance;
		
		return TransporterNetwork.get(level).getTransportersFromDimension(level.dimension()).stream()
				.filter(transporter -> transporter.getPosition(level.getServer()) != null &&
						transporter.getPosition(level.getServer()).distanceTo(centerPos) <= maxDistSqr &&
						transporter.getDimension() != null &&
						transporter.acceptsFrequency(frequency))
				.sorted(Comparator.comparing(transporter -> centerPos.distanceToSqr(transporter.getPosition(level.getServer())))).toList();
	}
	
	public static List<Transporter> findNearestTransporters(ServerLevel level, BlockPos centerPos, float maxDistance, int frequency)
	{
		return findNearestTransporters(level, centerPos.getCenter(), maxDistance, frequency);
	}
}
