package net.povstalec.sgjourney.common.structures;

import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

//Structure class is mostly copy-pasted from https://github.com/TelepathicGrunt/StructureTutorialMod/blob/1.19.0-Forge-Jigsaw/src/main/java/com/telepathicgrunt/structuretutorial/StructureTutorialMain.java
public class Cartouche extends SGJourneyStructure<SGJourneyStructure.Configuration>
{
    public Cartouche()
    {
        super(Configuration.CODEC, config -> findGenerationPoint(config, Cartouche::extraSpawningChecks));
    }
	
	public static boolean extraSpawningChecks(PieceGeneratorSupplier.Context<SGJourneyStructure.Configuration> context)
	{
		return true;
	}
}
