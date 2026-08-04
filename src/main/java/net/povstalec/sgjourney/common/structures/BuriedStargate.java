package net.povstalec.sgjourney.common.structures;

import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class BuriedStargate extends StargateStructure<StargateStructure.Configuration>
{
    public BuriedStargate()
    {
    	super(Configuration.CODEC, config -> findGenerationPoint(config, BuriedStargate::extraSpawningChecks));
    }
	
	public static boolean extraSpawningChecks(PieceGeneratorSupplier.Context<StargateStructure.Configuration> context)
	{
		return true;
	}
}
