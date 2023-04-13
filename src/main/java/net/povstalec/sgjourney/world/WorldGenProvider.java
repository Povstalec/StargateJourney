package net.povstalec.sgjourney.world;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

import net.minecraft.core.HolderLookup.Provider;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.init.FeatureInit;
import net.povstalec.sgjourney.init.PlacedFeatureInit;

public class WorldGenProvider extends DatapackBuiltinEntriesProvider
{
	public static final RegistrySetBuilder BUILDER = new RegistrySetBuilder()
			.add(Registries.CONFIGURED_FEATURE, (context) -> FeatureInit.bootstrap(context))
			.add(Registries.PLACED_FEATURE, (context) -> PlacedFeatureInit.bootstrap(context));
	
	public WorldGenProvider(PackOutput output, CompletableFuture<Provider> registries)
	{
		super(output, registries, BUILDER, Set.of(StargateJourney.MODID));
	}
	
}
