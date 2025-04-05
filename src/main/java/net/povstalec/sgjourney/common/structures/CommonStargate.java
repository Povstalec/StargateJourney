package net.povstalec.sgjourney.common.structures;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.NoiseColumn;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.povstalec.sgjourney.common.init.StructureInit;

//Structure class is mostly copy-pasted from https://github.com/TelepathicGrunt/StructureTutorialMod/blob/1.19.0-Forge-Jigsaw/src/main/java/com/telepathicgrunt/structuretutorial/StructureTutorialMain.java
public class CommonStargate extends StargateStructure
{
    public static final MapCodec<CommonStargate> CODEC = RecordCodecBuilder.<CommonStargate>mapCodec(instance ->
            instance.group(CommonStargate.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
					Codec.BOOL.optionalFieldOf("common_stargates").forGetter(structure -> Optional.ofNullable(structure.commonStargates)),
                    StargateStructure.StargateModifiers.CODEC.optionalFieldOf("stargate_modifiers").forGetter(structure -> Optional.ofNullable(structure.stargateModifiers)),
					DHDModifiers.CODEC.optionalFieldOf("dhd_modifiers").forGetter(structure -> Optional.ofNullable(structure.dhdModifiers))
            ).apply(instance, CommonStargate::new));

    public CommonStargate(Structure.StructureSettings config, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName,
                          int size, HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter,
						  Optional<Boolean> commonStargates, Optional<StargateModifiers> stargateModifiers, Optional<DHDModifiers> dhdModifiers)
    {
        super(config, startPool, startJigsawName, size, startHeight, projectStartToHeightmap, maxDistanceFromCenter, commonStargates, stargateModifiers, dhdModifiers);
    }
	
	@Override
	protected boolean extraSpawningChecks(Structure.GenerationContext context)
	{
		// Grabs the chunk position we are at
		ChunkPos chunkpos = context.chunkPos();
		
		int landHeight = context.chunkGenerator().getFirstOccupiedHeight(
				chunkpos.getMinBlockX(),
				chunkpos.getMinBlockZ(),
				Heightmap.Types.WORLD_SURFACE,
				context.heightAccessor(),
				context.randomState());
		
		NoiseColumn columnOfBlocks = context.chunkGenerator().getBaseColumn(chunkpos.getMinBlockX(), chunkpos.getMinBlockZ(), context.heightAccessor(), context.randomState());
		
		return !columnOfBlocks.getBlock(landHeight).isAir();
	}

    @Override
    public StructureType<?> type()
    {
        return StructureInit.COMMON_STARGATE.get(); // Helps the game know how to turn this structure back to json to save to chunks
    }
}
