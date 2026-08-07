package net.povstalec.sgjourney.mixin;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.povstalec.sgjourney.common.structures.SGJourneyStructure;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Random;

@Mixin(StructureStart.class)
public class StructureStartMixin
{
	@Unique
	private StructureStart stargatejourney_1_18_2$self()
	{
		return (StructureStart) (Object) this;
	}
	
	@Inject(method = "placeInChunk", at = @At(
		value = "INVOKE",
		target = "Lnet/minecraft/world/level/levelgen/structure/PostPlacementProcessor;afterPlace(Lnet/minecraft/world/level/WorldGenLevel;Lnet/minecraft/world/level/StructureFeatureManager;Lnet/minecraft/world/level/chunk/ChunkGenerator;Ljava/util/Random;Lnet/minecraft/world/level/levelgen/structure/BoundingBox;Lnet/minecraft/world/level/ChunkPos;Lnet/minecraft/world/level/levelgen/structure/pieces/PiecesContainer;)V",
		shift = At.Shift.AFTER))
	public void placeInChunk(WorldGenLevel level, StructureFeatureManager structureFeatureManager, ChunkGenerator chunkGenerator, Random randomSource, BoundingBox boundingBox, ChunkPos chunkPos, CallbackInfo ci)
	{
		if(stargatejourney_1_18_2$self().getFeature().config instanceof SGJourneyStructure.Configuration sgjourneyConfig)
			sgjourneyConfig.afterPlace(level, structureFeatureManager, chunkGenerator, randomSource, boundingBox, chunkPos, stargatejourney_1_18_2$self().pieceContainer);
	}
}
