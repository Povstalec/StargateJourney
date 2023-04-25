package net.povstalec.sgjourney.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.world.features.SpireFeature;
import net.povstalec.sgjourney.common.world.features.configuration.SpireConfiguration;

public class FeatureInit
{
	//============================================================================================
	//***************************************Basic Features***************************************
	//============================================================================================
	
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registries.FEATURE, StargateJourney.MODID);
	
    
    
	public static final Feature<SpireConfiguration> ORE_SPIRE_FEATURE = register("ore_spire", new SpireFeature(SpireConfiguration.CODEC));
	
	
	
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
	
    
    
	public static void bootstrap(BootstapContext<ConfiguredFeature<?, ?>> context)
	{
		register(context, STONE_NAQUADAH_SPIRE_KEY, ORE_SPIRE_FEATURE, new SpireConfiguration.SpireConfigurationBuilder(BlockStateProvider.simple(Blocks.STONE), BlockStateProvider.simple(BlockInit.NAQUADAH_ORE.get()), TagInit.Blocks.STONE_SPIRE_PROTRUDES_THROUGH).build());
		register(context, BLACKSTONE_NAQUADAH_SPIRE_KEY, ORE_SPIRE_FEATURE, new SpireConfiguration.SpireConfigurationBuilder(BlockStateProvider.simple(Blocks.BLACKSTONE), BlockStateProvider.simple(BlockInit.NAQUADAH_ORE.get()), TagInit.Blocks.STONE_SPIRE_PROTRUDES_THROUGH).build());//TODO Change to Blackstone Naquadah
	}
	
	
	
	public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name)
    {
        return ResourceKey.create(Registries.CONFIGURED_FEATURE, new ResourceLocation(StargateJourney.MODID, name));
    }
	
	private static <FC extends FeatureConfiguration, F extends Feature<FC>> void register(BootstapContext<ConfiguredFeature<?, ?>> context, ResourceKey<ConfiguredFeature<?, ?>> configuredFeatureKey, F feature, FC configuration)
    {
        context.register(configuredFeatureKey, new ConfiguredFeature<>(feature, configuration));
    }
}
