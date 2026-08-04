package net.povstalec.sgjourney.common.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureSpawnOverride;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.povstalec.sgjourney.common.config.CommonGenerationConfig;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Random;
import java.util.function.Predicate;

public class SGJourneyConfiguredStructureFeature<FC extends SGJourneyStructure.Configuration, F extends SGJourneyStructure<FC>> extends ConfiguredStructureFeature<FC, F>
{
	public SGJourneyConfiguredStructureFeature(F structureFeature, FC config, HolderSet<Biome> biomes, boolean adaptNoise, Map<MobCategory, StructureSpawnOverride> spawnOverrides)
	{
		super(structureFeature, config, biomes, adaptNoise, spawnOverrides);
	}
	
	@Override
	public @NotNull StructureStart generate(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, BiomeSource biomeSource, StructureManager structureManager, long seed,
											ChunkPos chunkPos, int references, LevelHeightAccessor heightAccessor, Predicate<Holder<Biome>> biomesPredicate)
	{
		Optional<PieceGenerator<FC>> optional = this.feature.pieceGeneratorSupplier().createGenerator(
			new PieceGeneratorSupplier.Context<>(chunkGenerator, biomeSource, seed, chunkPos, this.config, heightAccessor, biomesPredicate, structureManager, registryAccess));
		
		if(optional.isPresent())
		{
			StructurePiecesBuilder structurepiecesbuilder = new StructurePiecesBuilder();
			WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
			
			worldgenrandom.setLargeFeatureSeed(seed, chunkPos.x, chunkPos.z);
			optional.get().generatePieces(structurepiecesbuilder, new PieceGenerator.Context<>(this.config, chunkGenerator, structureManager, chunkPos, heightAccessor, worldgenrandom, seed));
			
			StructureStart structurestart = new StructureStart(this, chunkPos, references, structurepiecesbuilder.build());
			if(structurestart.isValid())
				return structurestart;
		}
		
		return StructureStart.INVALID_START;
	}
	
	@Override
	public @NotNull HolderSet<Biome> biomes()
	{
		if(config.commonStargates == null)
			return super.biomes();
		
		if(config.commonStargates != CommonGenerationConfig.common_stargate_generation.get())
			return HolderSet.direct();
		
		return super.biomes();
	}
	
	
	
	public static class Start<FC extends SGJourneyStructure.Configuration> extends StructureStart
	{
		private final FC config;
		
		public Start(ConfiguredStructureFeature<?, ?> configuredStructureFeature, ChunkPos chunkPos, int references, PiecesContainer piecesContainer, FC config)
		{
			super(configuredStructureFeature, chunkPos, references, piecesContainer);
			
			this.config = config;
		}
		
		public void placeInChunk(@NotNull WorldGenLevel level, @NotNull StructureFeatureManager structureFeatureManager, @NotNull ChunkGenerator chunkGenerator,
								 @NotNull Random randomSource, @NotNull BoundingBox boundingBox, @NotNull ChunkPos chunkPos)
		{
			List<StructurePiece> structurePieces = getPieces();
			if(!structurePieces.isEmpty())
			{
				BoundingBox boundingbox = (structurePieces.get(0)).getBoundingBox();
				BlockPos blockpos = boundingbox.getCenter();
				BlockPos blockpos1 = new BlockPos(blockpos.getX(), boundingbox.minY(), blockpos.getZ());
				
				for(StructurePiece structurepiece : structurePieces)
				{
					if(structurepiece.getBoundingBox().intersects(boundingBox))
						structurepiece.postProcess(level, structureFeatureManager, chunkGenerator, randomSource, boundingBox, chunkPos, blockpos1);
				}
				
				this.getFeature().feature.getPostPlacementProcessor().afterPlace(level, structureFeatureManager, chunkGenerator, randomSource, boundingBox, chunkPos, this.pieceContainer);
				config.afterPlace(level, structureFeatureManager, chunkGenerator, randomSource, boundingBox, chunkPos, this.pieceContainer);
			}
		}
	}
}
