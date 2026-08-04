package net.povstalec.sgjourney.common.structures;

import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

//Structure class is mostly copy-pasted from https://github.com/TelepathicGrunt/StructureTutorialMod/blob/1.19.0-Forge-Jigsaw/src/main/java/com/telepathicgrunt/structuretutorial/StructureTutorialMain.java
public class GoauldTemple extends SGJourneyStructure<SGJourneyStructure.Configuration>
{
    public GoauldTemple()
    {
        super(Configuration.CODEC, config -> findGenerationPoint(config, GoauldTemple::extraSpawningChecks));
    }
	
	public static boolean extraSpawningChecks(PieceGeneratorSupplier.Context<SGJourneyStructure.Configuration> context)
	{
		ChunkPos chunkpos = context.chunkPos();
		
		int landHeight = context.chunkGenerator().getFirstOccupiedHeight(
			chunkpos.getMinBlockX(),
			chunkpos.getMinBlockZ(),
			Heightmap.Types.WORLD_SURFACE,
			context.heightAccessor());
		
		NoiseColumn columnOfBlocks = context.chunkGenerator().getBaseColumn(chunkpos.getMinBlockX(), chunkpos.getMinBlockZ(), context.heightAccessor());
		
		return columnOfBlocks.getBlock(landHeight).getFluidState().isEmpty();
	}
}
