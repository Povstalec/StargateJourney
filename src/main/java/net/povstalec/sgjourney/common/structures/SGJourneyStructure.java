package net.povstalec.sgjourney.common.structures;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.StructureFeatureManager;
import net.minecraft.world.level.WorldGenLevel;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.*;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.heightproviders.HeightProvider;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pieces.PiecesContainer;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.povstalec.sgjourney.common.block_entities.StructureGenEntity;
import net.povstalec.sgjourney.common.block_entities.transporter.AbstractTransporterEntity;
import net.povstalec.sgjourney.common.block_entities.transporter_controller.TransporterControllerEntity;
import net.povstalec.sgjourney.common.misc.SGJourneyJigsawPlacement;
import org.jetbrains.annotations.NotNull;

import javax.annotation.Nullable;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;

//Structure class is mostly copy-pasted from https://github.com/TelepathicGrunt/StructureTutorialMod/blob/1.19.0-Forge-Jigsaw/src/main/java/com/telepathicgrunt/structuretutorial/StructureTutorialMain.java
public abstract class SGJourneyStructure<T extends SGJourneyStructure.Configuration> extends StructureFeature<T>
{
	public SGJourneyStructure(Codec<T> config, PieceGeneratorSupplier<T> pieceGeneratorSupplier)
	{
		super(config, pieceGeneratorSupplier);
	}
	
	public static <C extends SGJourneyStructure.Configuration> Optional<PieceGenerator<C>> findGenerationPoint(PieceGeneratorSupplier.Context<C> context, Predicate<PieceGeneratorSupplier.Context<C>> predicate)
	{
		if(!predicate.test(context))
			return Optional.empty();
		
		int startY = context.config().startHeight.sample(new WorldgenRandom(new LegacyRandomSource(0L)), new WorldGenerationContext(context.chunkGenerator(), context.heightAccessor()));
		
		// Turns the chunk coordinates into actual coordinates we can use. (Gets corner of that chunk)
		ChunkPos chunkPos = context.chunkPos();
		BlockPos blockPos = new BlockPos(chunkPos.getMinBlockX(), startY, chunkPos.getMinBlockZ());
		
		return SGJourneyJigsawPlacement.addPieces(
				context,
				PoolElementStructurePiece::new,
				blockPos,
				false,
				true,
				context.config().rotation,
				context.config().projectStartToHeightmap);
	}
	
	@Override
	public GenerationStep.@NotNull Decoration step()
	{
		return GenerationStep.Decoration.SURFACE_STRUCTURES;
	}
	
	
	
	public enum Rot implements StringRepresentable
	{
		NONE("none", Rotation.NONE),
		CLOCKWISE_90("clockwise_90", Rotation.CLOCKWISE_90),
		CLOCKWISE_180("180", Rotation.CLOCKWISE_180),
		COUNTERCLOCKWISE_90("counterclockwise_90", Rotation.COUNTERCLOCKWISE_90);
		
		public static final Codec<Rot> CODEC = StringRepresentable.fromEnum(Rot::values, Rot::byName);
		
		private static final Rot[] VALUES = values();
		private static final Map<String, Rot> BY_NAME = Arrays.stream(VALUES).collect(Collectors.toMap(Rot::getSerializedName, rotation -> rotation));
		
		public final String name;
		public final Rotation rotation;
		
		Rot(String name, Rotation rotation)
		{
			this.name = name;
			this.rotation = rotation;
		}
		
		@Nullable
		public static Rot byName(@Nullable String name)
		{
			return name == null ? null : BY_NAME.get(name.toLowerCase(Locale.ROOT));
		}
		
		@Override
		public @NotNull String getSerializedName()
		{
			return this.name;
		}
	}
	
	public static class Configuration extends JigsawConfiguration
	{
		public static final Codec<Configuration> CODEC = RecordCodecBuilder.<Configuration>mapCodec(instance ->
			instance.group(StructureTemplatePool.CODEC.fieldOf("start_pool").forGetter(structure -> structure.startPool),
				ResourceLocation.CODEC.optionalFieldOf("start_jigsaw_name").forGetter(structure -> Optional.ofNullable(structure.startJigsawName)),
				Codec.intRange(0, 30).fieldOf("size").forGetter(structure -> structure.size),
				HeightProvider.CODEC.fieldOf("start_height").forGetter(structure -> structure.startHeight),
				Heightmap.Types.CODEC.optionalFieldOf("project_start_to_heightmap").forGetter(structure -> Optional.ofNullable(structure.projectStartToHeightmap)),
				//Codec.intRange(1, 128).fieldOf("max_distance_from_center").forGetter(structure -> structure.maxDistanceFromCenter),
				Rot.CODEC.optionalFieldOf("rotation").forGetter(structure -> Optional.ofNullable(structure.rotation)),
				Codec.BOOL.optionalFieldOf("common_stargates").forGetter(structure -> Optional.ofNullable(structure.commonStargates)),
				TransporterModifiers.CODEC.optionalFieldOf("transporter_modifiers").forGetter(structure -> Optional.ofNullable(structure.transporterModifiers)),
				TransporterControllerModifiers.CODEC.optionalFieldOf("transporter_controller_modifiers").forGetter(structure -> Optional.ofNullable(structure.transporterControllerModifiers))
			).apply(instance, Configuration::new)).codec();
		
		protected final Holder<StructureTemplatePool> startPool;
		@Nullable
		protected final ResourceLocation startJigsawName;
		protected final int size;
		protected final HeightProvider startHeight;
		@Nullable
		protected final Heightmap.Types projectStartToHeightmap;
		//protected final int maxDistanceFromCenter;
		@Nullable
		protected Rot rotation;
		@Nullable
		protected Boolean commonStargates; // Decides whether this Structure should generate while Common Stargate Generation config setting is set to true of false
		
		@Nullable
		protected TransporterModifiers transporterModifiers;
		@Nullable
		protected TransporterControllerModifiers transporterControllerModifiers;
		
		public Configuration(Holder<StructureTemplatePool> startPool, Optional<ResourceLocation> startJigsawName,
							 int size, HeightProvider startHeight, Optional<Heightmap.Types> projectStartToHeightmap, /*int maxDistanceFromCenter, */Optional<Rot> rotation,
							 Optional<Boolean> commonStargates, Optional<TransporterModifiers> transporterModifiers, Optional<TransporterControllerModifiers> transporterControllerModifiers)
		{
			super(startPool, size);
			
			this.startPool = startPool;
			this.startJigsawName = startJigsawName.orElse(null);
			this.size = size;
			this.startHeight = startHeight;
			this.projectStartToHeightmap = projectStartToHeightmap.orElse(null);
			//this.maxDistanceFromCenter = maxDistanceFromCenter;
			this.rotation = rotation.orElse(null);
			
			this.commonStargates = commonStargates.orElse(null);
			
			this.transporterModifiers = transporterModifiers.orElse(null);
			this.transporterControllerModifiers = transporterControllerModifiers.orElse(null);
		}
		
		public Boolean commonStargates()
		{
			return commonStargates;
		}
		
		public void afterPlace(WorldGenLevel level, StructureFeatureManager structureManager, ChunkGenerator chunkGenerator, Random randomSource,
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
		
		protected void generateBlockEntities(WorldGenLevel level, BlockPos startPos, Random randomSource)
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
		
		protected void generateBlockEntity(WorldGenLevel level, BlockPos startPos, Random randomSource, StructureGenEntity generatedEntity)
		{
			generatedEntity.generateInStructure(level, randomSource);
			
			if(transporterModifiers != null && generatedEntity instanceof AbstractTransporterEntity<?> transporter)
				transporterModifiers.modifyTransporter(level, randomSource, transporter);
			else if(transporterControllerModifiers != null && generatedEntity instanceof TransporterControllerEntity transporterController)
				transporterControllerModifiers.modifyTransporterController(level, randomSource, transporterController);
		}
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
		
		public void modifyTransporter(WorldGenLevel level, Random randomSource, AbstractTransporterEntity<?> transporter)
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
		
		public void modifyTransporterController(WorldGenLevel level, Random randomSource, TransporterControllerEntity transporterController)
		{
			if(isProtected)
				transporterController.setProtected(true);
		}
	}
}
