package net.povstalec.sgjourney.common.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
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
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.block_entities.transporter_controller.TransporterControllerEntity;
import net.povstalec.sgjourney.common.config.CommonGenerationConfig;
import net.povstalec.sgjourney.common.misc.SGJourneyJigsawPlacement;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.Optional;

//Structure class is mostly copy-pasted from https://github.com/TelepathicGrunt/StructureTutorialMod/blob/1.19.0-Forge-Jigsaw/src/main/java/com/telepathicgrunt/structuretutorial/StructureTutorialMain.java
public abstract class SGJourneyStructure extends Structure
{
	protected final Holder<StructureTemplatePool> startPool;
	@Nullable
	protected final ResourceLocation startJigsawName;
	protected final int size;
	protected final HeightProvider startHeight;
	@Nullable
	protected final Heightmap.Types projectStartToHeightmap;
	protected final int maxDistanceFromCenter;
	@Nullable
	protected Rotation rotation;
	@Nullable
	protected Boolean commonStargates; // Decides whether this Structure should generate while Common Stargate Generation config setting is set to true of false
	
	@Nullable
	protected TransporterModifiers transporterModifiers;
	@Nullable
	protected TransporterControllerModifiers transporterControllerModifiers;
	
	
	public SGJourneyStructure(Structure.StructureSettings config, Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName,
	                          int size, HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, int maxDistanceFromCenter, Optional<Rotation> rotation,
	                          Optional<Boolean> commonStargates, Optional<TransporterModifiers> transporterModifiers, Optional<TransporterControllerModifiers> transporterControllerModifiers)
	{
		super(config);
		this.startPool = startPool;
		this.startJigsawName = startJigsawName.orElse(null);
		this.size = size;
		this.startHeight = startHeight;
		this.projectStartToHeightmap = projectStartToHeightmap.orElse(null);
		this.maxDistanceFromCenter = maxDistanceFromCenter;
		this.rotation = rotation.orElse(null);
		
		this.commonStargates = commonStargates.orElse(null);
		
		this.transporterModifiers = transporterModifiers.orElse(null);
		this.transporterControllerModifiers = transporterControllerModifiers.orElse(null);
	}
	
	public Holder<StructureTemplatePool> getStartPool()
	{
		return startPool;
	}
	
	@Override
	public @NotNull HolderSet<Biome> biomes()
	{
		if(commonStargates == null)
			return super.biomes();
		
		if(commonStargates != CommonGenerationConfig.common_stargate_generation.get())
			return HolderSet.direct();
		
		return super.biomes();
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
		
		return this.rotation == null ?
				JigsawPlacement.addPieces(
						context,
						getStartPool(),
						Optional.ofNullable(this.startJigsawName),
						this.size,
						blockPos,
						false,
						Optional.ofNullable(this.projectStartToHeightmap),
						this.maxDistanceFromCenter) :
				SGJourneyJigsawPlacement.addPieces(
						context,
						getStartPool(),
						Optional.ofNullable(this.startJigsawName),
						this.size,
						blockPos,
						false,
						Optional.ofNullable(this.projectStartToHeightmap),
						this.maxDistanceFromCenter,
						this.rotation);
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
		
		if(transporterModifiers != null && generatedEntity instanceof AbstractTransporterEntity<?> transporter)
			transporterModifiers.modifyTransporter(level, randomSource, transporter);
		else if(transporterControllerModifiers != null && generatedEntity instanceof TransporterControllerEntity transporterController)
			transporterControllerModifiers.modifyTransporterController(level, randomSource, transporterController);
	}
	
	
	
	public static class TransporterModifiers
	{
		private final boolean isProtected;
		
		public static final Codec<TransporterModifiers> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("protected").forGetter(modifiers -> Optional.of(modifiers.isProtected))
		).apply(instance, TransporterModifiers::new));
		
		public TransporterModifiers(Optional<Boolean> isProtected)
		{
			this.isProtected = isProtected.orElse(false);
		}
		
		public void modifyTransporter(WorldGenLevel level, RandomSource randomSource, AbstractTransporterEntity<?> transporter)
		{
			if(isProtected)
				transporter.setProtected(true);
		}
	}
	
	public static class TransporterControllerModifiers
	{
		private final boolean isProtected;
		
		public static final Codec<TransporterControllerModifiers> CODEC = RecordCodecBuilder.create(instance -> instance.group(
			Codec.BOOL.optionalFieldOf("protected").forGetter(modifiers -> Optional.of(modifiers.isProtected))
		).apply(instance, TransporterControllerModifiers::new));
		
		public TransporterControllerModifiers(Optional<Boolean> isProtected)
		{
			this.isProtected = isProtected.orElse(false);
		}
		
		public void modifyTransporterController(WorldGenLevel level, RandomSource randomSource, TransporterControllerEntity transporterController)
		{
			if(isProtected)
				transporterController.setProtected(true);
		}
	}
}
