package net.povstalec.sgjourney.common.structures;

import java.util.Optional;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.WorldGenerationContext;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;

//Structure class is mostly copy-pasted from https://github.com/TelepathicGrunt/StructureTutorialMod/blob/1.19.0-Forge-Jigsaw/src/main/java/com/telepathicgrunt/structuretutorial/StructureTutorialMain.java
public abstract class SGJourneyStructure extends Structure
{
	protected final Holder<StructureTemplatePool> startPool;
    protected final Optional<ResourceLocation> startJigsawName;
    protected final int size;
    protected final HeightProvider startHeight;
    protected final Optional<Heightmap.Types> projectStartToHeightmap;
    protected final int maxDistanceFromCenter;

    public SGJourneyStructure(Structure.StructureSettings config,
                         Holder<StructureTemplatePool> startPool,
                         Optional<ResourceLocation> startJigsawName,
                         int size,
                         HeightProvider startHeight,
                         Optional<Heightmap.Types> projectStartToHeightmap,
                         int maxDistanceFromCenter)
    {
        super(config);
        this.startPool = startPool;
        this.startJigsawName = startJigsawName;
        this.size = size;
        this.startHeight = startHeight;
        this.projectStartToHeightmap = projectStartToHeightmap;
        this.maxDistanceFromCenter = maxDistanceFromCenter;
    }
    
    protected boolean extraSpawningChecks(Structure.GenerationContext context)
    {
        return true;
    }

    @Override
    public Optional<Structure.GenerationStub> findGenerationPoint(Structure.GenerationContext context)
    {
        if(!extraSpawningChecks(context))
            return Optional.empty();
        
        int startY = this.startHeight.sample(context.random(), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));

        // Turns the chunk coordinates into actual coordinates we can use. (Gets corner of that chunk)
        ChunkPos chunkPos = context.chunkPos();
        BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());

        Optional<Structure.GenerationStub> structurePiecesGenerator =
                JigsawPlacement.addPieces(
                        context,
                        this.startPool,
                        this.startJigsawName,
                        this.size,
                        blockPos,
                        false,
                        this.projectStartToHeightmap,
                        this.maxDistanceFromCenter);
        
        return structurePiecesGenerator;
    }
    
    @Override
    public void afterPlace(WorldGenLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, RandomSource randomSource,
                           BoundingBox boundingBox, ChunkPos chunkPos, PiecesContainer piecesContainer)
    {
        BlockPos.MutableBlockPos startPos = new BlockPos.MutableBlockPos();
        int minX = boundingBox.minX();
        int maxX = boundingBox.maxX();
        int minZ = boundingBox.minZ();
        int maxZ = boundingBox.maxZ();
        
        for(int x = minX; x <= maxX; x += 16)
        {
            for(int z = minZ; z <= maxZ; z += 16)
            {
                generateBlockEntities(level, startPos.set(x, 0, z), randomSource);
            }
        }
    }
    
    protected void generateBlockEntities(WorldGenLevel level, BlockPos startPos, RandomSource randomSource)
    {
        for(BlockPos pos : level.getChunk(startPos).getBlockEntitiesPos())
        {
            BlockEntity blockEntity = level.getBlockEntity(pos);
            if(blockEntity instanceof StructureGenEntity generatedEntity)
            {
                generateBlockEntity(level, startPos, randomSource, generatedEntity);
                blockEntity.setChanged();
            }
        }
    }
    
    protected void generateBlockEntity(WorldGenLevel level, BlockPos startPos, RandomSource randomSource, StructureGenEntity generatedEntity)
    {
        generatedEntity.generateInStructure(level, randomSource);
    }
}
