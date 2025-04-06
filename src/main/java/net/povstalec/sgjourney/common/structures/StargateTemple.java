package net.povstalec.sgjourney.common.structures;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.DimensionPadding;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.templatesystem.LiquidSettings;
import net.povstalec.sgjourney.common.init.StructureInit;
import net.povstalec.sgjourney.common.misc.SGJourneyJigsawPlacement;

//Structure class is mostly copy-pasted from https://github.com/TelepathicGrunt/StructureTutorialMod/blob/1.19.0-Forge-Jigsaw/src/main/java/com/telepathicgrunt/structuretutorial/StructureTutorialMain.java
public class StargateTemple extends StargateStructure
{
	public static final MapCodec<StargateTemple> CODEC = RecordCodecBuilder.<StargateTemple>mapCodec(instance ->
    instance.group(StargateTemple.settingsCodec(instance),
            StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
            ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
            Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
            HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
            Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
            Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
			Codec.BOOL.optionalFieldOf("common_stargates").forGetter(structure -> Optional.ofNullable(structure.commonStargates)),
			StargateModifiers.CODEC.optionalFieldOf("stargate_modifiers").forGetter(structure -> Optional.ofNullable(structure.stargateModifiers)),
			DHDModifiers.CODEC.optionalFieldOf("dhd_modifiers").forGetter(structure -> Optional.ofNullable(structure.dhdModifiers))
    ).apply(instance, StargateTemple::new));

	public StargateTemple(Structure.StructureSettings config, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName,
						  int size, HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter,
						  Optional<Boolean> commonStargates, Optional<StargateModifiers> stargateModifiers, Optional<DHDModifiers> dhdModifiers)
	{
		super(config, startPool, startJigsawName, size, startHeight, projectStartToHeightmap, maxDistanceFromCenter, commonStargates, stargateModifiers, dhdModifiers);
	}

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context)
    {
        if (!extraSpawningChecks(context))
            return Optional.empty();
        
        int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));

        // Turns the chunk coordinates into actual coordinates we can use. (Gets corner of that chunk)
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());

        Optional<Structure.GenerationStub> structurePiecesGenerator =
        		SGJourneyJigsawPlacement.addPieces(
                        context,
                        this.startPool,
                        this.startJigsawName,
                        this.size,
                        blockPos,
                        false,
                        this.projectStartToHeightmap,
                        this.maxDistanceFromCenter,
                        Rotation.NONE,
						PoolAliasLookup.EMPTY,
						DimensionPadding.ZERO,
						LiquidSettings.APPLY_WATERLOGGING);
        
        return structurePiecesGenerator;
    }

    @Override
    public StructureType<?> type()
    {
        return StructureInit.STARGATE_TEMPLE.get(); // Helps the game know how to turn this structure back to json to save to chunks
    }
}
