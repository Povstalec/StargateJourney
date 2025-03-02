package net.povstalec.sgjourney.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.world.features.SpireFeature;
import net.povstalec.sgjourney.common.world.features.CrystalPatchFeature;
import net.povstalec.sgjourney.common.world.features.configuration.SpireConfiguration;
import net.povstalec.sgjourney.common.world.features.configuration.CrystalPatchConfiguration;

public class FeatureInit
{
	//============================================================================================
	//***************************************Basic Features***************************************
	//============================================================================================
	
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, StargateJourney.MODID);
	
    
    
	public static final Feature<SpireConfiguration> ORE_SPIRE_FEATURE = register("ore_spire", new SpireFeature(SpireConfiguration.CODEC));
	public static final Feature<CrystalPatchConfiguration> CRYSTAL_PATCH_FEATURE = register("crystal_patch", new CrystalPatchFeature(CrystalPatchConfiguration.CODEC));
	
	
	
	private static <C extends FeatureConfiguration, F extends Feature<C>> F register(String name, F feature)
    {
		FEATURES.register(name, () -> feature);
        return feature;
    }
	
	public static void register(IEventBus eventBus)
	{
		FEATURES.register(eventBus);
	}
	
	//============================================================================================
	//****************************************Feature Keys****************************************
	//============================================================================================
	
    public static final ResourceKey<ConfiguredFeature<?, ?>> STONE_NAQUADAH_SPIRE_KEY = createKey("stone_naquadah_spire");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLACKSTONE_NAQUADAH_SPIRE_KEY = createKey("blackstone_naquadah_spire");
	
    
    
	public static void bootstrap(BootstrapContext<ConfiguredFeature<?, ?>> context)
	{
		register(context, STONE_NAQUADAH_SPIRE_KEY, ORE_SPIRE_FEATURE, new SpireConfiguration.SpireConfigurationBuilder(BlockStateProvider.simple(Blocks.STONE), BlockStateProvider.simple(BlockInit.NAQUADAH_ORE.get()), TagInit.Blocks.STONE_SPIRE_PROTRUDES_THROUGH).build());
		register(context, BLACKSTONE_NAQUADAH_SPIRE_KEY, ORE_SPIRE_FEATURE, new SpireConfiguration.SpireConfigurationBuilder(BlockStateProvider.simple(Blocks.BLACKSTONE), BlockStateProvider.simple(BlockInit.NAQUADAH_ORE.get()), TagInit.Blocks.STONE_SPIRE_PROTRUDES_THROUGH).build());//TODO Change to Blackstone Naquadah
	}
	
	
	
	public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name)
    {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, StargateJourney.sgjourneyLocation(name));
    }
	
	private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstrapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey, F feature, FC configuration)
    {
        context.register(configuredFeatureKey, new ConfiguredFeature<>(feature, configuration));
    }
}
