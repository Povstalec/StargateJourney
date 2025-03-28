package net.povstalec.sgjourney.common.structures;

import java.util.Optional;
import java.util.Random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.povstalec.sgjourney.common.config.CommonGenerationConfig;
import net.povstalec.sgjourney.common.init.StructureInit;

public class BuriedStargate extends StargateStructure
{
    public static final Codec<BuriedStargate> CODEC = RecordCodecBuilder.<BuriedStargate>mapCodec(instance ->
            instance.group(BuriedStargate.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
					Codec.BOOL.optionalFieldOf("common_stargates").forGetter(structure -> Optional.ofNullable(structure.commonStargates)),
					StargateStructure.StargateModifiers.CODEC.optionalFieldOf("stargate_modifiers").forGetter(structure -> Optional.ofNullable(structure.stargateModifiers))
            ).apply(instance, BuriedStargate::new)).codec();

    private static Optional<Long> currentSeed = Optional.empty();
    private static Optional<Integer> x = Optional.empty();
    private static Optional<Integer> z = Optional.empty();
    
    public BuriedStargate(Structure.StructureSettings config, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName,
						  int size, HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter,
						  Optional<Boolean> commonStargates, Optional<StargateModifiers> stargateModifiers)
    {
    	super(config, startPool, startJigsawName, size, startHeight, projectStartToHeightmap, maxDistanceFromCenter, commonStargates, stargateModifiers);
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
    
    public static int getX(long seed)
    {
    	checkSeed(seed);
    	if(x.isEmpty())
    	{
            Random random = new Random(seed);
            int xOffset = CommonGenerationConfig.stargate_generation_center_x_chunk_offset.get();
            int xBound = CommonGenerationConfig.buried_stargate_generation_x_bound.get();
            

            int chunkX = xBound <= 0 ? xOffset : xOffset + random.nextInt(-xBound, xBound + 1);
            
            x = Optional.of(chunkX);
    	}

    	return x.get();
    }
    
    public static int getZ(long seed)
    {
    	checkSeed(seed);
    	if(z.isEmpty())
    	{
            Random random = new Random(seed + 1);
            int zOffset = CommonGenerationConfig.stargate_generation_center_z_chunk_offset.get();
            int zBound = CommonGenerationConfig.buried_stargate_generation_z_bound.get();
            

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
	
	@Override
	public Optional<Structure.GenerationStub> findValidGenerationPoint(Structure.GenerationContext context)
	{
		//TODO See if there's a way to check for dimension
		//context.chunkGenerator().getBiomeSource().findBiomeHorizontal(maxDistanceFromCenter, maxDistanceFromCenter, size, maxDistanceFromCenter, null, null, null);
		return super.findValidGenerationPoint(context);
	}

    @Override
    public StructureType<?> type()
    {
        return StructureInit.BURIED_STARGATE.get(); // Helps the game know how to turn this structure back to json to save to chunks
    }
}
