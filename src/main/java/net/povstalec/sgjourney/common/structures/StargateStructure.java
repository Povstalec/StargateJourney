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
	import net.minecraft.world.level.block.Rotation;
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
	@Nullable
	protected final Holder<StructureTemplatePool> obstructedStartPool;
	
	@Nullable
	protected StargateModifiers stargateModifiers;
	@Nullable
	protected DHDModifiers dhdModifiers;
	
	public StargateStructure(Structure.StructureSettings config, Holder<StructureTemplatePool> startPool, Optional<Holder<StructureTemplatePool>> obstructedStartPool, Optional<ResourceLocation> startJigsawName,
							 int size, HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter, Optional<Rotation> rotation,
							 Optional<Boolean> commonStargates, Optional<StargateModifiers> stargateModifiers, Optional<DHDModifiers> dhdModifiers)
	{
		super(config, startPool, startJigsawName, size, startHeight, projectStartToHeightmap, maxDistanceFromCenter, rotation, commonStargates);
		
		this.obstructedStartPool = obstructedStartPool.orElse(null);
		
		this.stargateModifiers = stargateModifiers.orElse(null);
		this.dhdModifiers = dhdModifiers.orElse(null);
	}
	
	@Override
	public Holder<StructureTemplatePool> getStartPool()
	{
		return obstructedStartPool != null && CommonGenerationConfig.generate_obstructed_stargates.get() ? obstructedStartPool : startPool;
	}
	
	@Override
	protected void generateBlockEntity(WorldGenLevel level, BlockPos startPos, RandomSource randomSource, StructureGenEntity generatedEntity)
	{
		super.generateBlockEntity(level, startPos, randomSource, generatedEntity);
		
		if(stargateModifiers != null && generatedEntity instanceof AbstractStargateEntity<?> stargate)
			stargateModifiers.modifyStargate(stargate);
		else if(dhdModifiers != null && generatedEntity instanceof AbstractDHDEntity dhd)
			dhdModifiers.modifyDHD(dhd);
	}
	
	
	
	public static class StargateModifiers
	{
		private final boolean displayID;
		private final boolean upgraded;
		private final boolean localPointOfOrigin;
		
		private final boolean primary;
		private final boolean isProtected;
		
		public static final Codec<StargateModifiers> CODEC = RecordCodecBuilder.create(instance -> instance.group(
				Codec.BOOL.optionalFieldOf("display_id").forGetter(modifiers -> Optional.of(modifiers.displayID)),
				Codec.BOOL.optionalFieldOf("upgraded").forGetter(modifiers -> Optional.of(modifiers.upgraded)),
				
				Codec.BOOL.optionalFieldOf("local_point_of_origin").forGetter(modifiers -> Optional.of(modifiers.localPointOfOrigin)),
				
				Codec.BOOL.optionalFieldOf("primary").forGetter(modifiers -> Optional.of(modifiers.primary)),
				Codec.BOOL.optionalFieldOf("protected").forGetter(modifiers -> Optional.of(modifiers.isProtected))
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
		
		public void modifyStargate(AbstractStargateEntity<?> stargate)
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
		private final boolean isProtected;
		
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
