package net.povstalec.sgjourney.common.structures;

import java.util.Optional;
import java.util.Random;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.povstalec.sgjourney.common.config.CommonGenerationConfig;

public abstract class StargateStructure extends SGJourneyStructure
{
    private static Optional<Long> currentSeed = Optional.empty();
    private static Optional<Integer> x = Optional.empty();
    private static Optional<Integer> z = Optional.empty();
    
    public StargateStructure(Structure.StructureSettings config,
			Holder<StructureTemplatePool> startPool,
			Optional<ResourceLocation> startJigsawName,
			int size,
			HeightProvider startHeight,
			Optional<Heightmap.Types> projectStartToHeightmap,
			int maxDistanceFromCenter)
    {
    	super(config, startPool, startJigsawName, size, startHeight, projectStartToHeightmap, maxDistanceFromCenter);
    }
    
    private static final void checkSeed(long seed)
    {
    	if(currentSeed.isEmpty() || (currentSeed.isPresent() && currentSeed.get() != seed))
    	{
    		currentSeed = Optional.of(seed);
            x = Optional.empty();
            z = Optional.empty();
    	}
    }
    
    public static final int getX(long seed)
    {
    	checkSeed(seed);
    	if(x.isEmpty())
    	{
            Random random = new Random(seed + 2);
            int xOffset = CommonGenerationConfig.stargate_generation_center_x_chunk_offset.get();
            int xBound = CommonGenerationConfig.stargate_generation_x_bound.get();
            

            int chunkX = xBound <= 0 ? xOffset : xOffset + random.nextInt(-xBound, xBound + 1);
            
            x = Optional.of(chunkX);
    	}

    	return x.get();
    }
    
    public static final int getZ(long seed)
    {
    	checkSeed(seed);
    	if(z.isEmpty())
    	{
            Random random = new Random(seed + 3);
            int zOffset = CommonGenerationConfig.stargate_generation_center_z_chunk_offset.get();
            int zBound = CommonGenerationConfig.stargate_generation_z_bound.get();
            

            int chunkZ = zBound <= 0 ? zOffset : zOffset + random.nextInt(-zBound, zBound + 1);
            
            z = Optional.of(chunkZ);
    	}

    	return z.get();
    }
    
	@Override
	protected boolean extraSpawningChecks(Structure.GenerationContext context)
	{
		// Grabs the chunk position we are at
		ChunkPos chunkpos = context.chunkPos();
		long seed = context.seed();
		
		if(chunkpos.x == getX(seed) && chunkpos.z == getZ(seed))
			return true;
		else
			return false;
	}
}
