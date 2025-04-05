package net.povstalec.sgjourney.common.structures;

import java.util.Optional;
import java.util.Random;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.dhd.AbstractDHDEntity;
import net.povstalec.sgjourney.common.block_entities.stargate.AbstractStargateEntity;
import net.povstalec.sgjourney.common.config.CommonGenerationConfig;
import org.jetbrains.annotations.Nullable;

public abstract class StargateStructure extends SGJourneyStructure
{
    private static Optional<Long> currentSeed = Optional.empty();
    private static Optional<Integer> x = Optional.empty();
    private static Optional<Integer> z = Optional.empty();
	
	@Nullable
	protected StargateModifiers stargateModifiers;
	@Nullable
	protected DHDModifiers dhdModifiers;
    
    public StargateStructure(Structure.StructureSettings config, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName,
							 int size, HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter,
							 Optional<Boolean> commonStargates, Optional<StargateModifiers> stargateModifiers, Optional<DHDModifiers> dhdModifiers)
    {
    	super(config, startPool, startJigsawName, size, startHeight, projectStartToHeightmap, maxDistanceFromCenter, commonStargates);
		
		this.stargateModifiers = stargateModifiers.orElse(null);
		this.dhdModifiers = dhdModifiers.orElse(null);
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
            Random random = new Random(seed + 2);
            int xOffset = CommonGenerationConfig.stargate_generation_center_x_chunk_offset.get();
            int xBound = CommonGenerationConfig.stargate_generation_x_bound.get();
            

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
	
	@Override
	protected void generateBlockEntity(WorldGenLevel level, BlockPos startPos, RandomSource randomSource, StructureGenEntity generatedEntity)
	{
		super.generateBlockEntity(level, startPos, randomSource, generatedEntity);
		
		if(stargateModifiers != null && generatedEntity instanceof AbstractStargateEntity stargate)
			stargateModifiers.modifyStargate(stargate);
		else if(dhdModifiers != null && generatedEntity instanceof AbstractDHDEntity dhd)
			dhdModifiers.modifyDHD(dhd);
	}
	
	
	
	public static class StargateModifiers
	{
		private boolean displayID;
		private boolean upgraded;
		private boolean localPointOfOrigin;
		
		private boolean primary;
		private boolean isProtected;
		
		public static final Codec<StargateModifiers> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.BOOL.optionalFieldOf("display_id").forGetter(modifiers -> Optional.ofNullable(modifiers.displayID)),
				Codec.BOOL.optionalFieldOf("upgraded").forGetter(modifiers -> Optional.ofNullable(modifiers.upgraded)),
				
				Codec.BOOL.optionalFieldOf("local_point_of_origin").forGetter(modifiers -> Optional.ofNullable(modifiers.localPointOfOrigin)),
				
				Codec.BOOL.optionalFieldOf("primary").forGetter(modifiers -> Optional.ofNullable(modifiers.primary)),
				Codec.BOOL.optionalFieldOf("protected").forGetter(modifiers -> Optional.ofNullable(modifiers.isProtected))
		).apply(instance, StargateModifiers::new));
		
		public StargateModifiers(Optional<Boolean> displayID, Optional<Boolean> upgraded, Optional<Boolean> localPointOfOrigin,
								 Optional<Boolean> primary, Optional<Boolean> isProtected)
		{
			this.displayID = displayID.orElse(false);
			this.upgraded = upgraded.orElse(false);
			
			this.localPointOfOrigin = localPointOfOrigin.orElse(false);
			
			this.primary = primary.orElse(false);
			this.isProtected = isProtected.orElse(false);
		}
		
		public void modifyStargate(AbstractStargateEntity stargate)
		{
			if(displayID)
				stargate.displayID();
			
			if(upgraded)
				stargate.upgraded();
			
			if(localPointOfOrigin)
				stargate.localPointOfOrigin();
			
			if(primary)
				stargate.setPrimary();
			
			if(isProtected)
				stargate.setProtected(true);
		}
	}
	
	
	
	public static class DHDModifiers
	{
		private boolean isProtected;
		
		public static final Codec<DHDModifiers> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.BOOL.optionalFieldOf("protected").forGetter(modifiers -> Optional.ofNullable(modifiers.isProtected))
		).apply(instance, DHDModifiers::new));
		
		public DHDModifiers(Optional<Boolean> isProtected)
		{
			this.isProtected = isProtected.orElse(false);
		}
		
		public void modifyDHD(AbstractDHDEntity dhd)
		{
			if(isProtected)
				dhd.setProtected(true);
		}
	}
}
