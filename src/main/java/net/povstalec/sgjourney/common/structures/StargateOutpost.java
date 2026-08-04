package net.povstalec.sgjourney.common.structures;

import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;

public class StargateOutpost extends StargateStructure<StargateStructure.Configuration>
{
    public StargateOutpost()
    {
    	super(Configuration.CODEC, config -> findGenerationPoint(config, StargateOutpost::extraSpawningChecks));
    }
	
	public static boolean extraSpawningChecks(PieceGeneratorSupplier.Context<StargateStructure.Configuration> context)
	{
		return true;
	}
}
