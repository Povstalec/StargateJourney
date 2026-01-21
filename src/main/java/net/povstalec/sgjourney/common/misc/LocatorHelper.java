package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

import javax.annotation.Nullable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;

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
	
	public static List<Transporter> findNearestTransporters(ServerLevel level, Vec3 centerPos, float maxDistance, int frequency)
	{
		List<Transporter> transporters = TransporterNetwork.get(level).getTransportersFromDimension(level.dimension());
		transporters.sort(Comparator.comparing(transporter -> centerPos.distanceToSqr(Objects.requireNonNull(transporter.getPosition(level.getServer())))));
		
		transporters.removeIf(transporter -> transporter.getPosition(level.getServer()) == null || transporter.getDimension() == null || !transporter.acceptsFrequency(frequency)); //TODO Max distance
		
		return transporters;
	}
	
	public static List<Transporter> findNearestTransporters(ServerLevel level, BlockPos centerPos, float maxDistance, int frequency)
	{
		return findNearestTransporters(level, centerPos.getCenter(), maxDistance, frequency);
	}
}
