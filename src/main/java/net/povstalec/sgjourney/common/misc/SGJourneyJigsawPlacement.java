package net.povstalec.sgjourney.common.misc;

import java.util.Deque;
import java.util.List;
import java.util.Optional;

import org.apache.commons.lang3.mutable.MutableObject;
import org.slf4j.Logger;

import com.google.common.collect.Lists;
import com.google.common.collect.Queues;
import com.mojang.logging.LogUtils;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.Vec3i;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.Pools;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.LevelHeightAccessor;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.block.JigsawBlock;
import net.minecraft.world.level.block.Rotation;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.RandomState;
import net.minecraft.world.level.levelgen.WorldgenRandom;
import net.minecraft.world.level.levelgen.structure.BoundingBox;
import net.minecraft.world.level.levelgen.structure.PoolElementStructurePiece;
import net.minecraft.world.level.levelgen.structure.Structure;
import net.minecraft.world.level.levelgen.structure.StructurePiece;
import net.minecraft.world.level.levelgen.structure.pieces.StructurePiecesBuilder;
import net.minecraft.world.level.levelgen.structure.pools.EmptyPoolElement;
import net.minecraft.world.level.levelgen.structure.pools.JigsawJunction;
import net.minecraft.world.level.levelgen.structure.pools.JigsawPlacement;
import net.minecraft.world.level.levelgen.structure.pools.StructurePoolElement;
import net.minecraft.world.level.levelgen.structure.pools.StructureTemplatePool;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplate;
import net.minecraft.world.level.levelgen.structure.templatesystem.StructureTemplateManager;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.BooleanOp;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.povstalec.sgjourney.StargateJourney;

/**
 * Pretty much just a copy paste of the vanilla Jigsaw placement, but with rotation set to none
 * @author Povstalec
 *
 */
public class SGJourneyJigsawPlacement extends JigsawPlacement
{
	static final Logger LOGGER = LogUtils.getLogger();
	
	public static Optional<Structure.GenerationStub> addPieces(Structure.GenerationContext p_227239_, Holder<StructureTemplatePool> p_227240_, Optional<ResourceLocation> p_227241_, int p_227242_, BlockPos p_227243_, boolean p_227244_, Optional<Heightmap.Types> p_227245_, int p_227246_) {
	      RegistryAccess registryaccess = p_227239_.registryAccess();
	      ChunkGenerator chunkgenerator = p_227239_.chunkGenerator();
	      StructureTemplateManager structuretemplatemanager = p_227239_.structureTemplateManager();
	      LevelHeightAccessor levelheightaccessor = p_227239_.heightAccessor();
	      WorldgenRandom worldgenrandom = p_227239_.random();
	      Registry<StructureTemplatePool> registry = registryaccess.registryOrThrow(Registries.TEMPLATE_POOL);
	      Rotation rotation = Rotation.COUNTERCLOCKWISE_90;
	      StructureTemplatePool structuretemplatepool = p_227240_.value();
	      StructurePoolElement structurepoolelement = structuretemplatepool.getRandomTemplate(worldgenrandom);
	      if (structurepoolelement == EmptyPoolElement.INSTANCE) {
	         return Optional.empty();
	      } else {
	         BlockPos blockpos;
	         if (p_227241_.isPresent()) {
	            ResourceLocation resourcelocation = p_227241_.get();
	            Optional<BlockPos> optional = getRandomNamedJigsaw(structurepoolelement, resourcelocation, p_227243_, rotation, structuretemplatemanager, worldgenrandom);
	            if (optional.isEmpty()) {
	               LOGGER.error("No starting jigsaw {} found in start pool {}", resourcelocation, p_227240_.unwrapKey().get().location());
	               return Optional.empty();
	            }

	            blockpos = optional.get();
	         } else {
	            blockpos = p_227243_;
	         }

	         Vec3i vec3i = blockpos.subtract(p_227243_);
	         BlockPos blockpos1 = p_227243_.subtract(vec3i);
	         PoolElementStructurePiece poolelementstructurepiece = new PoolElementStructurePiece(structuretemplatemanager, structurepoolelement, blockpos1, structurepoolelement.getGroundLevelDelta(), rotation, structurepoolelement.getBoundingBox(structuretemplatemanager, blockpos1, rotation));
	         BoundingBox boundingbox = poolelementstructurepiece.getBoundingBox();
	         int i = (boundingbox.maxX() + boundingbox.minX()) / 2;
	         int j = (boundingbox.maxZ() + boundingbox.minZ()) / 2;
	         int k;
	         if (p_227245_.isPresent()) {
	            k = p_227243_.getY() + chunkgenerator.getFirstFreeHeight(i, j, p_227245_.get(), levelheightaccessor, p_227239_.randomState());
	         } else {
	            k = blockpos1.getY();
	         }

	         int l = boundingbox.minY() + poolelementstructurepiece.getGroundLevelDelta();
	         poolelementstructurepiece.move(0, k - l, 0);
	         int i1 = k + vec3i.getY();
	         return Optional.of(new Structure.GenerationStub(new BlockPos(i, i1, j), (p_227237_) -> {
	            List<PoolElementStructurePiece> list = Lists.newArrayList();
	            list.add(poolelementstructurepiece);
	            if (p_227242_ > 0) {
	               AABB aabb = new AABB((double)(i - p_227246_), (double)(i1 - p_227246_), (double)(j - p_227246_), (double)(i + p_227246_ + 1), (double)(i1 + p_227246_ + 1), (double)(j + p_227246_ + 1));
	               VoxelShape voxelshape = Shapes.join(Shapes.create(aabb), Shapes.create(AABB.of(boundingbox)), BooleanOp.ONLY_FIRST);
	               addPieces(p_227239_.randomState(), p_227242_, p_227244_, chunkgenerator, structuretemplatemanager, levelheightaccessor, worldgenrandom, registry, poolelementstructurepiece, list, voxelshape);
	               list.forEach(p_227237_::addPiece);
	            }
	         }));
	      }
	   }
	
	private static Optional<BlockPos> getRandomNamedJigsaw(StructurePoolElement p_227248_, ResourceLocation p_227249_, BlockPos p_227250_, Rotation p_227251_, StructureTemplateManager p_227252_, WorldgenRandom p_227253_) {
	      List<StructureTemplate.StructureBlockInfo> list = p_227248_.getShuffledJigsawBlocks(p_227252_, p_227250_, p_227251_, p_227253_);
	      Optional<BlockPos> optional = Optional.empty();

	      for(StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : list) {
	         ResourceLocation resourcelocation = ResourceLocation.tryParse(structuretemplate$structureblockinfo.nbt.getString("name"));
	         if (p_227249_.equals(resourcelocation)) {
	            optional = Optional.of(structuretemplate$structureblockinfo.pos);
	            break;
	         }
	      }

	      return optional;
	   }

	   private static void addPieces(RandomState p_227211_, int p_227212_, boolean p_227213_, ChunkGenerator p_227214_, StructureTemplateManager p_227215_, LevelHeightAccessor p_227216_, RandomSource p_227217_, Registry<StructureTemplatePool> p_227218_, PoolElementStructurePiece p_227219_, List<PoolElementStructurePiece> p_227220_, VoxelShape p_227221_) {
		   SGJourneyJigsawPlacement.Placer jigsawplacement$placer = new SGJourneyJigsawPlacement.Placer(p_227218_, p_227212_, p_227214_, p_227215_, p_227220_, p_227217_);
	      jigsawplacement$placer.placing.addLast(new SGJourneyJigsawPlacement.PieceState(p_227219_, new MutableObject<>(p_227221_), 0));

	      while(!jigsawplacement$placer.placing.isEmpty()) {
	    	  SGJourneyJigsawPlacement.PieceState jigsawplacement$piecestate = jigsawplacement$placer.placing.removeFirst();
	         jigsawplacement$placer.tryPlacingChildren(jigsawplacement$piecestate.piece, jigsawplacement$piecestate.free, jigsawplacement$piecestate.depth, p_227213_, p_227216_, p_227211_);
	      }

	   }

	   public static boolean generateJigsaw(ServerLevel p_227204_, Holder<StructureTemplatePool> p_227205_, ResourceLocation p_227206_, int p_227207_, BlockPos p_227208_, boolean p_227209_) {
	      ChunkGenerator chunkgenerator = p_227204_.getChunkSource().getGenerator();
	      StructureTemplateManager structuretemplatemanager = p_227204_.getStructureManager();
	      StructureManager structuremanager = p_227204_.structureManager();
	      RandomSource randomsource = p_227204_.getRandom();
	      Structure.GenerationContext structure$generationcontext = new Structure.GenerationContext(p_227204_.registryAccess(), chunkgenerator, chunkgenerator.getBiomeSource(), p_227204_.getChunkSource().randomState(), structuretemplatemanager, p_227204_.getSeed(), new ChunkPos(p_227208_), p_227204_, (p_227255_) -> {
	         return true;
	      });
	      Optional<Structure.GenerationStub> optional = addPieces(structure$generationcontext, p_227205_, Optional.of(p_227206_), p_227207_, p_227208_, false, Optional.empty(), 128);
	      if (optional.isPresent()) {
	         StructurePiecesBuilder structurepiecesbuilder = optional.get().getPiecesBuilder();

	         for(StructurePiece structurepiece : structurepiecesbuilder.build().pieces()) {
	            if (structurepiece instanceof PoolElementStructurePiece) {
	               PoolElementStructurePiece poolelementstructurepiece = (PoolElementStructurePiece)structurepiece;
	               poolelementstructurepiece.place(p_227204_, structuremanager, chunkgenerator, randomsource, BoundingBox.infinite(), p_227208_, p_227209_);
	            }
	         }

	         return true;
	      } else {
	         return false;
	      }
	   }

	   static final class PieceState {
	      final PoolElementStructurePiece piece;
	      final MutableObject<VoxelShape> free;
	      final int depth;

	      PieceState(PoolElementStructurePiece p_210311_, MutableObject<VoxelShape> p_210312_, int p_210313_) {
	         this.piece = p_210311_;
	         this.free = p_210312_;
	         this.depth = p_210313_;
	      }
	   }

	   static final class Placer {
	      private final Registry<StructureTemplatePool> pools;
	      private final int maxDepth;
	      private final ChunkGenerator chunkGenerator;
	      private final StructureTemplateManager structureTemplateManager;
	      private final List<? super PoolElementStructurePiece> pieces;
	      private final RandomSource random;
	      final Deque<SGJourneyJigsawPlacement.PieceState> placing = Queues.newArrayDeque();

	      Placer(Registry<StructureTemplatePool> p_227258_, int p_227259_, ChunkGenerator p_227260_, StructureTemplateManager p_227261_, List<? super PoolElementStructurePiece> p_227262_, RandomSource p_227263_) {
	         this.pools = p_227258_;
	         this.maxDepth = p_227259_;
	         this.chunkGenerator = p_227260_;
	         this.structureTemplateManager = p_227261_;
	         this.pieces = p_227262_;
	         this.random = p_227263_;
	      }

	      void tryPlacingChildren(PoolElementStructurePiece p_227265_, MutableObject<VoxelShape> p_227266_, int p_227267_, boolean p_227268_, LevelHeightAccessor p_227269_, RandomState p_227270_) {
	          StructurePoolElement structurepoolelement = p_227265_.getElement();
	          BlockPos blockpos = p_227265_.getPosition();
	          Rotation rotation = p_227265_.getRotation();
	          StructureTemplatePool.Projection structuretemplatepool$projection = structurepoolelement.getProjection();
	          boolean flag = structuretemplatepool$projection == StructureTemplatePool.Projection.RIGID;
	          MutableObject<VoxelShape> mutableobject = new MutableObject<>();
	          BoundingBox boundingbox = p_227265_.getBoundingBox();
	          int i = boundingbox.minY();

	          label129:
	          for(StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo : structurepoolelement.getShuffledJigsawBlocks(this.structureTemplateManager, blockpos, rotation, this.random)) {
	             Direction direction = JigsawBlock.getFrontFacing(structuretemplate$structureblockinfo.state);
	             BlockPos blockpos1 = structuretemplate$structureblockinfo.pos;
	             BlockPos blockpos2 = blockpos1.relative(direction);
	             int j = blockpos1.getY() - i;
	             int k = -1;
	             ResourceKey<StructureTemplatePool> resourcekey = readPoolName(structuretemplate$structureblockinfo);
	             Optional<? extends Holder<StructureTemplatePool>> optional = this.pools.getHolder(resourcekey);
	             if (optional.isEmpty()) {
	            	 StargateJourney.LOGGER.warn("Empty or non-existent pool: {}", (Object)resourcekey.location());
	             } else {
	                Holder<StructureTemplatePool> holder = optional.get();
	                if (holder.value().size() == 0 && !holder.is(Pools.EMPTY)) {
	                   StargateJourney.LOGGER.warn("Empty or non-existent pool: {}", (Object)resourcekey.location());
	                } else {
	                   Holder<StructureTemplatePool> holder1 = holder.value().getFallback();
	                   if (holder1.value().size() == 0 && !holder1.is(Pools.EMPTY)) {
	                	   StargateJourney.LOGGER.warn("Empty or non-existent fallback pool: {}", holder1.unwrapKey().map((p_255599_) -> {
	                         return p_255599_.location().toString();
	                      }).orElse("<unregistered>"));
	                   } else {
	                      boolean flag1 = boundingbox.isInside(blockpos2);
	                      MutableObject<VoxelShape> mutableobject1;
	                      if (flag1) {
	                         mutableobject1 = mutableobject;
	                         if (mutableobject.getValue() == null) {
	                            mutableobject.setValue(Shapes.create(AABB.of(boundingbox)));
	                         }
	                      } else {
	                         mutableobject1 = p_227266_;
	                      }

	                      List<StructurePoolElement> list = Lists.newArrayList();
	                      if (p_227267_ != this.maxDepth) {
	                         list.addAll(holder.value().getShuffledTemplates(this.random));
	                      }

	                      list.addAll(holder1.value().getShuffledTemplates(this.random));

	                      for(StructurePoolElement structurepoolelement1 : list) {
	                         if (structurepoolelement1 == EmptyPoolElement.INSTANCE) {
	                            break;
	                         }

	                         for(Rotation rotation1 : Rotation.getShuffled(this.random)) {
	                            List<StructureTemplate.StructureBlockInfo> list1 = structurepoolelement1.getShuffledJigsawBlocks(this.structureTemplateManager, BlockPos.ZERO, rotation1, this.random);
	                            BoundingBox boundingbox1 = structurepoolelement1.getBoundingBox(this.structureTemplateManager, BlockPos.ZERO, rotation1);
	                            int l;
	                            if (p_227268_ && boundingbox1.getYSpan() <= 16) {
	                               l = list1.stream().mapToInt((p_255598_) -> {
	                                  if (!boundingbox1.isInside(p_255598_.pos.relative(JigsawBlock.getFrontFacing(p_255598_.state)))) {
	                                     return 0;
	                                  } else {
	                                     ResourceKey<StructureTemplatePool> resourcekey1 = readPoolName(p_255598_);
	                                     Optional<? extends Holder<StructureTemplatePool>> optional1 = this.pools.getHolder(resourcekey1);
	                                     Optional<Holder<StructureTemplatePool>> optional2 = optional1.map((p_255600_) -> {
	                                        return p_255600_.value().getFallback();
	                                     });
	                                     int j3 = optional1.map((p_255596_) -> {
	                                        return p_255596_.value().getMaxSize(this.structureTemplateManager);
	                                     }).orElse(0);
	                                     int k3 = optional2.map((p_255601_) -> {
	                                        return p_255601_.value().getMaxSize(this.structureTemplateManager);
	                                     }).orElse(0);
	                                     return Math.max(j3, k3);
	                                  }
	                               }).max().orElse(0);
	                            } else {
	                               l = 0;
	                            }

	                            for(StructureTemplate.StructureBlockInfo structuretemplate$structureblockinfo1 : list1) {
	                               if (JigsawBlock.canAttach(structuretemplate$structureblockinfo, structuretemplate$structureblockinfo1)) {
	                                  BlockPos blockpos3 = structuretemplate$structureblockinfo1.pos;
	                                  BlockPos blockpos4 = blockpos2.subtract(blockpos3);
	                                  BoundingBox boundingbox2 = structurepoolelement1.getBoundingBox(this.structureTemplateManager, blockpos4, rotation1);
	                                  int i1 = boundingbox2.minY();
	                                  StructureTemplatePool.Projection structuretemplatepool$projection1 = structurepoolelement1.getProjection();
	                                  boolean flag2 = structuretemplatepool$projection1 == StructureTemplatePool.Projection.RIGID;
	                                  int j1 = blockpos3.getY();
	                                  int k1 = j - j1 + JigsawBlock.getFrontFacing(structuretemplate$structureblockinfo.state).getStepY();
	                                  int l1;
	                                  if (flag && flag2) {
	                                     l1 = i + k1;
	                                  } else {
	                                     if (k == -1) {
	                                        k = this.chunkGenerator.getFirstFreeHeight(blockpos1.getX(), blockpos1.getZ(), Heightmap.Types.WORLD_SURFACE_WG, p_227269_, p_227270_);
	                                     }

	                                     l1 = k - j1;
	                                  }

	                                  int i2 = l1 - i1;
	                                  BoundingBox boundingbox3 = boundingbox2.moved(0, i2, 0);
	                                  BlockPos blockpos5 = blockpos4.offset(0, i2, 0);
	                                  if (l > 0) {
	                                     int j2 = Math.max(l + 1, boundingbox3.maxY() - boundingbox3.minY());
	                                     boundingbox3.encapsulate(new BlockPos(boundingbox3.minX(), boundingbox3.minY() + j2, boundingbox3.minZ()));
	                                  }

	                                  if (!Shapes.joinIsNotEmpty(mutableobject1.getValue(), Shapes.create(AABB.of(boundingbox3).deflate(0.25D)), BooleanOp.ONLY_SECOND)) {
	                                     mutableobject1.setValue(Shapes.joinUnoptimized(mutableobject1.getValue(), Shapes.create(AABB.of(boundingbox3)), BooleanOp.ONLY_FIRST));
	                                     int i3 = p_227265_.getGroundLevelDelta();
	                                     int k2;
	                                     if (flag2) {
	                                        k2 = i3 - k1;
	                                     } else {
	                                        k2 = structurepoolelement1.getGroundLevelDelta();
	                                     }

	                                     PoolElementStructurePiece poolelementstructurepiece = new PoolElementStructurePiece(this.structureTemplateManager, structurepoolelement1, blockpos5, k2, rotation1, boundingbox3);
	                                     int l2;
	                                     if (flag) {
	                                        l2 = i + j;
	                                     } else if (flag2) {
	                                        l2 = l1 + j1;
	                                     } else {
	                                        if (k == -1) {
	                                           k = this.chunkGenerator.getFirstFreeHeight(blockpos1.getX(), blockpos1.getZ(), Heightmap.Types.WORLD_SURFACE_WG, p_227269_, p_227270_);
	                                        }

	                                        l2 = k + k1 / 2;
	                                     }

	                                     p_227265_.addJunction(new JigsawJunction(blockpos2.getX(), l2 - j + i3, blockpos2.getZ(), k1, structuretemplatepool$projection1));
	                                     poolelementstructurepiece.addJunction(new JigsawJunction(blockpos1.getX(), l2 - j1 + k2, blockpos1.getZ(), -k1, structuretemplatepool$projection));
	                                     this.pieces.add(poolelementstructurepiece);
	                                     if (p_227267_ + 1 <= this.maxDepth) {
	                                        this.placing.addLast(new SGJourneyJigsawPlacement.PieceState(poolelementstructurepiece, mutableobject1, p_227267_ + 1));
	                                     }
	                                     continue label129;
	                                  }
	                               }
	                            }
	                         }
	                      }
	                   }
	                }
	             }
	          }

	       }

	       private static ResourceKey<StructureTemplatePool> readPoolName(StructureTemplate.StructureBlockInfo p_256491_) {
	          return ResourceKey.create(Registries.TEMPLATE_POOL, new ResourceLocation(p_256491_.nbt.getString("pool")));
	       }
	    }

}
