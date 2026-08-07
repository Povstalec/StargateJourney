package net.povstalec.sgjourney.mixin;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.RegistryAccess;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.BiomeSource;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.povstalec.sgjourney.common.config.CommonGenerationConfig;
import net.povstalec.sgjourney.common.structures.SGJourneyStructure;
import net.povstalec.sgjourney.common.structures.SGJourneyStructureStart;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Optional;
import java.util.function.Predicate;

@Mixin(ConfiguredStructureFeature.class)
public class ConfiguredStructureMixin
{
	private ConfiguredStructureFeature self()
	{
		return (ConfiguredStructureFeature) (Object) this;
	}
	
	@Inject(method = "generate", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/structure/pieces/PieceGeneratorSupplier;createGenerator(Lnet/minecraft/world/level/levelgen/structure/pieces/PieceGeneratorSupplier$Context;)Ljava/util/Optional;",
		shift = At.Shift.AFTER),
		locals = LocalCapture.CAPTURE_FAILEXCEPTION,
		cancellable = true)
	public void generate(RegistryAccess registryAccess, ChunkGenerator chunkGenerator, BiomeSource biomeSource, StructureManager structureManager, long seed,
						 ChunkPos chunkPos, int references, LevelHeightAccessor heightAccessor, Predicate<Holder<Biome>> biomesPredicate,
						 CallbackInfoReturnable<StructureStart> cir)
	{
		if(self().config instanceof SGJourneyStructure.Configuration sgjourneyConfig)
		{
			Optional optional = self().feature.pieceGeneratorSupplier().createGenerator(new PieceGeneratorSupplier.Context<>(chunkGenerator, biomeSource, seed, chunkPos, self().config, heightAccessor, biomesPredicate, structureManager, registryAccess));
			if(optional.isPresent())
			{
				StructurePiecesBuilder structurepiecesbuilder = new StructurePiecesBuilder();
				WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
				
				worldgenrandom.setLargeFeatureSeed(seed, chunkPos.x, chunkPos.z);
				((PieceGenerator<FeatureConfiguration>) optional.get()).generatePieces(structurepiecesbuilder, new PieceGenerator.Context<>(self().config, chunkGenerator, structureManager, chunkPos, heightAccessor, worldgenrandom, seed));
				
				StructureStart structurestart = new SGJourneyStructureStart<>(self(), chunkPos, references, structurepiecesbuilder.build(), sgjourneyConfig);
				if(structurestart.isValid())
					cir.setReturnValue(structurestart);
				else
					cir.setReturnValue(StructureStart.INVALID_START);
			}
			else
				cir.setReturnValue(StructureStart.INVALID_START);
		}
		
	}
	
	@Inject(method = "biomes", at = @At("HEAD"), cancellable = true)
	public void biomes(CallbackInfoReturnable<HolderSet<Biome>> cir)
	{
		if(self().config instanceof SGJourneyStructure.Configuration sgjourneyConfig)
		{
			if(sgjourneyConfig.commonStargates() == null)
				cir.setReturnValue(self().biomes);
			else if(sgjourneyConfig.commonStargates() != CommonGenerationConfig.common_stargate_generation.get())
				cir.setReturnValue(HolderSet.direct());
		}
	}
}
