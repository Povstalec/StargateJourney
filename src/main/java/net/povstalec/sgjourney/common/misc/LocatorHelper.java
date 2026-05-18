package net.povstalec.sgjourney.common.misc;

import net.minecraft.core.BlockPos;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.phys.Vec3;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.data.TransporterNetwork;
import net.povstalec.sgjourney.common.sgjourney.transporter.Transporter;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;

public class LocatorHelper
{
	//============================================================================================
	//******************************************Stargate******************************************
	//============================================================================================
	
	public static List<AbstractStargateEntity<?>> getNearbyStargates(Level level, BlockPos centerPos, double maxDistance)
	{
		List<AbstractStargateEntity<?>> stargates = new ArrayList<>();
		
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
					if(level.getBlockEntity(pos) instanceof AbstractStargateEntity<?> stargate && CoordinateHelper.Relative.distanceSqr(centerPos, stargate.getBlockPos()) <= maxDistance * maxDistance)
						stargates.add(stargate);
				}
			}
		}
		
		return stargates;
	}
	
	public static List<AbstractStargateEntity<?>> getNearbyStargatesByDistance(Level level, BlockPos centerPos, double maxDistance)
	{
		List<AbstractStargateEntity<?>> stargates = getNearbyStargates(level, centerPos, maxDistance);
		stargates.sort(Comparator.comparing(stargate -> CoordinateHelper.Relative.distanceSqr(centerPos, stargate.getBlockPos())));
		
		return stargates;
	}
	
	@Nullable
	public static AbstractStargateEntity<?> getNearestStargate(Level level, BlockPos centerPos, double maxDistance)
	{
		List<AbstractStargateEntity<?>> stargates = getNearbyStargatesByDistance(level, centerPos, maxDistance);
		
		if(stargates.isEmpty())
			return null;
		
		return stargates.get(0);
	}
	
	//============================================================================================
	//****************************************Transporter*****************************************
	//============================================================================================
	
	@SuppressWarnings("unchecked")
	@Nullable
	public static <T extends BlockEntity> T getNearestBlockEntityOfClass(Class<T> clazz, Level level, BlockPos centerPos, double maxDistance, Predicate<T> filter)
	{
		double bestDistance = Double.POSITIVE_INFINITY;
		T closest = null;
		
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
					BlockEntity blockEntity = level.getBlockEntity(pos);
					double distanceSqr = CoordinateHelper.Relative.distanceSqr(centerPos, blockEntity.getBlockPos());
					if(distanceSqr < bestDistance && clazz.isInstance(blockEntity))
					{
						T t = (T) blockEntity;
						if(filter.test(t))
						{
							bestDistance = distanceSqr;
							closest = t;
						}
					}
				}
			}
		}
		
		return closest;
	}
	
	@Nullable
	public static <T extends BlockEntity> T getNearest(List<T> blockEntities, BlockPos centerPos)
	{
		double bestDistance = Double.POSITIVE_INFINITY;
		T closest = null;
		for(T blockEntity : blockEntities)
		{
			double distanceSqr = CoordinateHelper.Relative.distanceSqr(centerPos, blockEntity.getBlockPos());
			if(distanceSqr < bestDistance)
			{
				bestDistance = distanceSqr;
				closest = blockEntity;
			}
		}
		
		return closest;
	}
	
	@SuppressWarnings("unchecked")
	public static <T extends BlockEntity> List<T> getBlockEntitiesOfClass(Class<T> clazz, Level level, BlockPos centerPos, double maxDistance, Predicate<T> filter)
	{
		double maxDistanceSqr = maxDistance * maxDistance;
		List<T> blockEntities = new ArrayList<>();
		
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
					BlockEntity blockEntity = level.getBlockEntity(pos);
					if(CoordinateHelper.Relative.distanceSqr(centerPos, blockEntity.getBlockPos()) < maxDistanceSqr && clazz.isInstance(blockEntity))
					{
						T t = (T) blockEntity;
						if(filter.test(t))
							blockEntities.add(t);
					}
				}
			}
		}
		
		return blockEntities;
	}
	
	public static <T extends BlockEntity> void sortByDistance(List<T> blockEntities, BlockPos centerPos)
	{
		blockEntities.sort(Comparator.comparing(blockEntity -> CoordinateHelper.Relative.distanceSqr(centerPos, blockEntity.getBlockPos())));
	}
	
	
	
	public static List<AbstractTransporterEntity<?>> getNearbyTransporters(Level level, BlockPos centerPos, double maxDistance)
	{
		List<AbstractTransporterEntity<?>> transporters = new ArrayList<>();
		
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
					if(level.getBlockEntity(pos) instanceof AbstractTransporterEntity<?> transporter)
						transporters.add(transporter);
				}
			}
		}
		
		return transporters;
	}
	
	public static List<AbstractTransporterEntity<?>> getNearbyTransportersByDistance(Level level, BlockPos centerPos, double maxDistance)
	{
		List<AbstractTransporterEntity<?>> transportRings = getNearbyTransporters(level, centerPos, maxDistance);
		transportRings.sort(Comparator.comparing(transporter -> CoordinateHelper.Relative.distanceSqr(centerPos, transporter.getBlockPos())));
		
		return transportRings;
	}
	
	@Nullable
	public static AbstractTransporterEntity<?> getNearestTransporter(Level level, BlockPos centerPos, double maxDistance)
	{
		List<AbstractTransporterEntity<?>> transportRings = getNearbyTransportersByDistance(level, centerPos, maxDistance);
		
		if(transportRings.isEmpty())
			return null;
		
		return transportRings.get(0);
	}
	
	public static List<Transporter> findNearestTransporters(ServerLevel level, Vec3 centerPos, double maxDistance, Predicate<Transporter> filter)
	{
		double maxDistSqr = maxDistance * maxDistance;
		
		return TransporterNetwork.get(level).getTransportersFromDimension(level.dimension()).stream()
				.filter(transporter -> transporter.getPosition(level.getServer()) != null &&
						centerPos.distanceToSqr(transporter.getPosition(level.getServer())) <= maxDistSqr &&
						transporter.getDimension() != null && filter.test(transporter))
				.sorted(Comparator.comparing(transporter -> centerPos.distanceToSqr(transporter.getPosition(level.getServer())))).toList();
	}
	
	public static List<Transporter> findNearestTransporters(ServerLevel level, BlockPos centerPos, double maxDistance, Predicate<Transporter> filter)
	{
		return findNearestTransporters(level, centerPos.getCenter(), maxDistance, filter);
	}
}
