package net.povstalec.sgjourney.common.world;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.init.FeatureInit;
import net.povstalec.sgjourney.common.init.PlacedFeatureInit;
import net.povstalec.sgjourney.common.init.StructurePlacementInit;

public class WorldGenProvider extends DatapackBuiltinEntriesProvider
{
	public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.CONFIGURED_FEATURE, FeatureInit::bootstrap)
			.add(Registries.PLACED_FEATURE, PlacedFeatureInit::bootstrap);
	
	public WorldGenProvider(PackOutput output, CompletableFuture<Provider> registries)
	{
		super(output, registries, BUILDER, Set.of(StargateJourney.MODID));
	}
	
}
