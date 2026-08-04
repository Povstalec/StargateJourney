package net.povstalec.sgjourney.common.misc;

import com.google.common.collect.Lists;
import net.minecraft.core.*;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.LegacyRandomSource;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.feature.StructureFeature;
import net.minecraft.world.level.levelgen.feature.configurations.JigsawConfiguration;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGenerator;
import net.minecraft.world.level.levelgen.structure.pieces.PieceGeneratorSupplier;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.povstalec.sgjourney.common.structures.SGJourneyStructure;
import org.apache.commons.lang3.mutable.MutableObject;

import java.util.List;
import java.util.Optional;
import java.util.function.Predicate;

/**
 * Pretty much just a copy paste of the vanilla Jigsaw placement, but with the ability to specify rotation
 * @author Povstalec
 *
 */
public class SGJourneyJigsawPlacement
{
	public static <C extends SGJourneyStructure.Configuration> Optional<PieceGenerator<C>> addPieces(PieceGeneratorSupplier.Context<C> p_210285_, JigsawPlacement.PieceFactory p_210286_, BlockPos p_210287_, boolean p_210288_, boolean p_210289_, SGJourneyStructure.Rot rot)
	{
		WorldgenRandom worldgenrandom = new WorldgenRandom(new LegacyRandomSource(0L));
		worldgenrandom.setLargeFeatureSeed(p_210285_.seed(), p_210285_.chunkPos().x, p_210285_.chunkPos().z);
		RegistryAccess registryaccess = p_210285_.registryAccess();
		JigsawConfiguration jigsawconfiguration = p_210285_.config();
		ChunkGenerator chunkgenerator = p_210285_.chunkGenerator();
		StructureManager structuremanager = p_210285_.structureManager();
		LevelHeightAccessor levelheightaccessor = p_210285_.heightAccessor();
		Predicate<Holder<Biome>> predicate = p_210285_.validBiome();
		StructureFeature.bootstrap();
		Registry<StructureTemplatePool> registry = registryaccess.registryOrThrow(Registry.TEMPLATE_POOL_REGISTRY);
		Rotation rotation = rot != null ? rot.rotation : Rotation.getRandom(worldgenrandom);
		StructureTemplatePool structuretemplatepool = jigsawconfiguration.startPool().value();
		StructurePoolElement structurepoolelement = structuretemplatepool.getRandomTemplate(worldgenrandom);
		if (structurepoolelement == EmptyPoolElement.INSTANCE) {
			return Optional.empty();
		} else {
			PoolElementStructurePiece poolelementstructurepiece = p_210286_.create(structuremanager, structurepoolelement, p_210287_, structurepoolelement.getGroundLevelDelta(), rotation, structurepoolelement.getBoundingBox(structuremanager, p_210287_, rotation));
			BoundingBox boundingbox = poolelementstructurepiece.getBoundingBox();
			int i = (boundingbox.maxX() + boundingbox.minX()) / 2;
			int j = (boundingbox.maxZ() + boundingbox.minZ()) / 2;
			int k;
			if (p_210289_) {
				k = p_210287_.getY() + chunkgenerator.getFirstFreeHeight(i, j, Heightmap.Types.WORLD_SURFACE_WG, levelheightaccessor);
			} else {
				k = p_210287_.getY();
			}
			
			if (!predicate.test(chunkgenerator.getNoiseBiome(QuartPos.fromBlock(i), QuartPos.fromBlock(k), QuartPos.fromBlock(j)))) {
				return Optional.empty();
			} else {
				int l = boundingbox.minY() + poolelementstructurepiece.getGroundLevelDelta();
				poolelementstructurepiece.move(0, k - l, 0);
				return Optional.of((p_210282_, p_210283_) -> {
					List<PoolElementStructurePiece> list = Lists.newArrayList();
					list.add(poolelementstructurepiece);
					if (jigsawconfiguration.maxDepth() > 0) {
						int i1 = 80;
						AABB aabb = new AABB((double)(i - 80), (double)(k - 80), (double)(j - 80), (double)(i + 80 + 1), (double)(k + 80 + 1), (double)(j + 80 + 1));
						JigsawPlacement.Placer jigsawplacement$placer = new JigsawPlacement.Placer(registry, jigsawconfiguration.maxDepth(), p_210286_, chunkgenerator, structuremanager, list, worldgenrandom);
						jigsawplacement$placer.placing.addLast(new JigsawPlacement.PieceState(poolelementstructurepiece, new MutableObject<>(Shapes.join(Shapes.create(aabb), Shapes.create(AABB.of(boundingbox)), BooleanOp.ONLY_FIRST)), 0));
						
						while(!jigsawplacement$placer.placing.isEmpty()) {
							JigsawPlacement.PieceState jigsawplacement$piecestate = jigsawplacement$placer.placing.removeFirst();
							jigsawplacement$placer.tryPlacingChildren(jigsawplacement$piecestate.piece, jigsawplacement$piecestate.free, jigsawplacement$piecestate.depth, p_210288_, levelheightaccessor);
						}
						
						list.forEach(p_210282_::addPiece);
					}
				});
			}
		}
	}
}
