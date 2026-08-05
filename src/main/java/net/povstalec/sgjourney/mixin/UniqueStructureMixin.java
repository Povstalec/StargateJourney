package net.povstalec.sgjourney.mixin;

import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.core.SectionPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.placement.RandomSpreadStructurePlacement;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.povstalec.sgjourney.common.world.UniqueStructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(ChunkGenerator.class)
public class UniqueStructureMixin
{

	@Inject(method = "findNearestMapFeature", at = @At(value = "INVOKE", target = "Ljava/util/Map;entrySet()Ljava/util/Set;", shift = At.Shift.BEFORE), locals = LocalCapture.CAPTURE_FAILEXCEPTION, cancellable = true)
	public void findNearestMapFeature(ServerLevel level, HolderSet<ConfiguredStructureFeature<?, ?>> features, BlockPos pos, int maxDistance, boolean skipLoadedChunks,
									  CallbackInfoReturnable<Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>>> cir,
									  Set set,
									  Set set1,
									  Pair pair,
									  double d0,
									  Map map,
									  List list)
	{
		List<Map.Entry<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>>> uniqueStructurePlacements = new ArrayList<>();
		
		Map<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>> placementMap = (Map<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>>) map;
		for(Map.Entry<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>> placementEntry : placementMap.entrySet())
		{
			if(placementEntry.getKey() instanceof UniqueStructurePlacement)
				uniqueStructurePlacements.add(placementEntry);
		}
		
		if(!uniqueStructurePlacements.isEmpty())
		{
			boolean found = false;
			
			Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> foundPair = null;
			for(Map.Entry<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>> placementEntry : uniqueStructurePlacements)
			{
				UniqueStructurePlacement uniqueStructurePlacement = (UniqueStructurePlacement) placementEntry.getKey();
				Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> pair1 = UniqueStructurePlacement.getNearestGeneratedStructure(placementEntry.getValue(), level, level.structureFeatureManager(), skipLoadedChunks, level.getSeed(), uniqueStructurePlacement);
				if (pair1 != null) {
					found = true;
					double d2 = pos.distSqr(pair1.getFirst());
					if (d2 < d0) {
						d0 = d2;
						foundPair = pair1;
					}
				}
			}
			
			if(found)
				cir.setReturnValue(foundPair);
		}
	}
}
