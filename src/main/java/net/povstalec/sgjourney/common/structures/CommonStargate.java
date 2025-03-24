package net.povstalec.sgjourney.common.structures;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.povstalec.sgjourney.common.init.StructureInit;
import net.povstalec.sgjourney.common.misc.SGJourneyJigsawPlacement;

//Structure class is mostly copy-pasted from https://github.com/TelepathicGrunt/StructureTutorialMod/blob/1.19.0-Forge-Jigsaw/src/main/java/com/telepathicgrunt/structuretutorial/StructureTutorialMain.java
public class CommonStargate extends StargateStructure
{
    public static final Codec<CommonStargate> CODEC = RecordCodecBuilder.<CommonStargate>mapCodec(instance ->
            instance.group(CommonStargate.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
                    StargateStructure.StargateModifiers.CODEC.optionalFieldOf("stargate_modifiers").forGetter(structure -> Optional.ofNullable(structure.stargateModifiers))
            ).apply(instance, CommonStargate::new)).codec();

    public CommonStargate(Structure.StructureSettings config, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName,
                          int size, HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter,
                          Optional<StargateModifiers> stargateModifiers)
    {
        super(config, startPool, startJigsawName, size, startHeight, projectStartToHeightmap, maxDistanceFromCenter, stargateModifiers);
    }
	
	@Override
	public HolderSet<Biome> biomes()
	{
		if(false) //TODO Write a config check
			return HolderSet.direct();
		
		return super.biomes();
	}
    
    @Override
	protected boolean extraSpawningChecks(Structure.GenerationContext context)
	{
    	return true;
	}

    @Override
    public StructureType<?> type()
    {
        return StructureInit.COMMON_STARGATE.get(); // Helps the game know how to turn this structure back to json to save to chunks
    }
}
