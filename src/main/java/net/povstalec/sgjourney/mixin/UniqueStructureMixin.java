package net.povstalec.sgjourney.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.ref.LocalDoubleRef;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.datafixers.util.Pair;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderSet;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.chunk.ChunkGenerator;
import net.minecraft.world.level.levelgen.feature.ConfiguredStructureFeature;
import net.minecraft.world.level.levelgen.structure.placement.StructurePlacement;
import net.povstalec.sgjourney.common.world.UniqueStructurePlacement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Mixin(ChunkGenerator.class)
public class UniqueStructureMixin
{
	@Inject(method = "findNearestMapFeature", at = @At(value = "INVOKE", target = "Ljava/util/List;isEmpty()Z", ordinal = 0))
	public void findNearestMapFeature(ServerLevel level, HolderSet<ConfiguredStructureFeature<?, ?>> features, BlockPos pos, int maxDistance, boolean skipLoadedChunks,
									  CallbackInfoReturnable<Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>>> cir,
									  @Local(ordinal = 0) LocalRef<Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>>> pair,
									  @Local(ordinal = 0) LocalDoubleRef d0,
									  @Local(ordinal = 0) Map<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>> map)
	{
		List<Map.Entry<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>>> uniqueStructurePlacements = new ArrayList<>();
		
		for(Map.Entry<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>> placementEntry : map.entrySet())
		{
			if(placementEntry.getKey() instanceof UniqueStructurePlacement)
				uniqueStructurePlacements.add(placementEntry);
		}
		
		if(!uniqueStructurePlacements.isEmpty())
		{
			for(Map.Entry<StructurePlacement, Set<Holder<ConfiguredStructureFeature<?, ?>>>> placementEntry : uniqueStructurePlacements)
			{
				UniqueStructurePlacement uniqueStructurePlacement = (UniqueStructurePlacement) placementEntry.getKey();
				Pair<BlockPos, Holder<ConfiguredStructureFeature<?, ?>>> pair1 = UniqueStructurePlacement.getNearestGeneratedStructure(placementEntry.getValue(), level, level.structureFeatureManager(), skipLoadedChunks, level.getSeed(), uniqueStructurePlacement);
				if(pair1 != null)
				{
					double d2 = pos.distSqr(pair1.getFirst());
					if (d2 < d0.get()) {
						d0.set(d2);
						pair.set(pair1);
					}
				}
			}
		}
	}
}
