package net.povstalec.sgjourney.common.structures;

import java.util.Optional;

import com.mojang.serialization.Codec;
import com.mojang.serialization.MapCodec;
import com.mojang.serialization.codecs.RecordCodecBuilder;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructureType;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.pools.alias.PoolAliasLookup;
import net.minecraft.world.level.levelgen.structure.structures.JigsawStructure;
import net.povstalec.sgjourney.common.config.CommonGenerationConfig;
import net.povstalec.sgjourney.common.init.StructureInit;
import net.povstalec.sgjourney.common.misc.SGJourneyJigsawPlacement;

//Structure class is mostly copy-pasted from https://github.com/TelepathicGrunt/StructureTutorialMod/blob/1.19.0-Forge-Jigsaw/src/main/java/com/telepathicgrunt/structuretutorial/StructureTutorialMain.java
public class StargateVoidStructure extends StargateStructure
{
    public static final MapCodec<StargateVoidStructure> CODEC = RecordCodecBuilder.<StargateVoidStructure>mapCodec(instance ->
            instance.group(StargateVoidStructure.settingsCodec(instance),
                    StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
                    ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> structure.startJigsawName),
                    Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
                    HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
                    Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> structure.projectStartToHeightmap),
                    Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
                    Codec.BOOL.optionalFieldOf("common_stargates").forGetter(structure -> Optional.ofNullable(structure.commonStargates)),
                    StargateStructure.StargateModifiers.CODEC.optionalFieldOf("stargate_modifiers").forGetter(structure -> Optional.ofNullable(structure.stargateModifiers)),
                    DHDModifiers.CODEC.optionalFieldOf("dhd_modifiers").forGetter(structure -> Optional.ofNullable(structure.dhdModifiers))
            ).apply(instance, StargateVoidStructure::new));

    public StargateVoidStructure(Structure.StructureSettings config, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName,
                                 int size, HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter,
                                 Optional<Boolean> commonStargates, Optional<StargateModifiers> stargateModifiers, Optional<DHDModifiers> dhdModifiers)
    {
        super(config, startPool, startJigsawName, size, startHeight, projectStartToHeightmap, maxDistanceFromCenter, commonStargates, stargateModifiers, dhdModifiers);
    }
    
    @Override
    public boolean extraSpawningChecks(Structure.GenerationContext context)
    {
        // Grabs the chunk position we are at
    	 ChunkPos chunkpos = context.chunkPos();
         
         int xOffset = CommonGenerationConfig.stargate_generation_center_x_chunk_offset.get();
         int zOffset = CommonGenerationConfig.stargate_generation_center_z_chunk_offset.get();
         
         int chunkX = xOffset;
         int chunkZ = zOffset;
         
         if(chunkpos.x == chunkX && chunkpos.z == chunkZ)
         	return true;
         else
         	return false;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context)
    {
        if(!extraSpawningChecks(context))
            return Optional.empty();
        
        int startY = 70;

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
                        PoolAliasLookup.EMPTY,
                        JigsawStructure.DEFAULT_DIMENSION_PADDING,
                        JigsawStructure.DEFAULT_LIQUID_SETTINGS);
        
        return structurePiecesGenerator;
    }

    @Override
    public StructureType<?> type()
    {
        return StructureInit.STARGATE_VOID_STRUCTURE.get(); // Helps the game know how to turn this structure back to json to save to chunks
    }
}
