package net.povstalec.sgjourney.common.structures;

import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

//Structure class is mostly copy-pasted from https://github.com/TelepathicGrunt/StructureTutorialMod/blob/1.19.0-Forge-Jigsaw/src/main/java/com/telepathicgrunt/structuretutorial/StructureTutorialMain.java
public class StargateTemple extends StargateStructure<StargateStructure.Configuration>
{
	public StargateTemple()
	{
		super(Configuration.CODEC, config -> findGenerationPoint(config, StargateTemple::extraSpawningChecks));
	}
	
	public static boolean extraSpawningChecks(PieceGeneratorSupplier.Context<StargateStructure.Configuration> context)
	{
		return true;
	}
}
