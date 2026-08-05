package net.povstalec.sgjourney.common.structures;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.StructureStart;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Random;

public class SGJourneyStructureStart<FC extends SGJourneyStructure.Configuration> extends StructureStart
{
	private final FC config;
	
	public SGJourneyStructureStart(ConfiguredStructureFeature<?, ?> configuredStructureFeature, ChunkPos chunkPos, int references, PiecesContainer piecesContainer, FC config)
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
