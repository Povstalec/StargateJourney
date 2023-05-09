package net.povstalec.sgjourney.common.init;

import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.feature.Feature;
import net.minecraft.world.level.levelgen.feature.configurations.FeatureConfiguration;
import net.minecraft.world.level.levelgen.feature.stateproviders.BlockStateProvider;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.world.features.SpireFeature;
import net.povstalec.sgjourney.common.world.features.configuration.SpireConfiguration;

public class FeatureInit
{
	//============================================================================================
	//***************************************Basic Features***************************************
	//============================================================================================
	
    public static final DeferredRegister<Feature<?>> FEATURES = DeferredRegister.create(Registry.FEATURE_REGISTRY, StargateJourney.MODID);
    
    
	public static final Feature<SpireConfiguration> ORE_SPIRE_FEATURE = register("ore_spire", new SpireFeature(SpireConfiguration.CODEC));
	
	
	
	private static <C extends FeatureConfiguration, F extends Feature<C>> F register(String name, F feature)
    {
		FEATURES.register(name, () -> feature);
        return feature;
    }
	
	//============================================================================================
	//****************************************Feature Keys****************************************
	//============================================================================================
	
    public static final DeferredRegister<ConfiguredFeature<?, ?>> CONFIGURED_FEATURES = DeferredRegister.create(Registry.CONFIGURED_FEATURE_REGISTRY, StargateJourney.MODID);
	
    public static final ResourceKey<ConfiguredFeature<?, ?>> STONE_NAQUADAH_SPIRE_KEY = createKey("stone_naquadah_spire");
    public static final ResourceKey<ConfiguredFeature<?, ?>> BLACKSTONE_NAQUADAH_SPIRE_KEY = createKey("blackstone_naquadah_spire");
	
	public static final RegistryObject<ConfiguredFeature<?, ?>> STONE_NAQUADAH_SPIRE =
			CONFIGURED_FEATURES.register("stone_spire_naquadah",() ->
            new ConfiguredFeature<>(ORE_SPIRE_FEATURE, 
            		new SpireConfiguration.SpireConfigurationBuilder(
            				BlockStateProvider.simple(Blocks.STONE), 
            				BlockStateProvider.simple(BlockInit.NAQUADAH_ORE.get()), 
            				TagInit.Blocks.STONE_SPIRE_PROTRUDES_THROUGH).build()));
	
	public static final RegistryObject<ConfiguredFeature<?, ?>> BLACKSTONE_NAQUADAH_SPIRE =
			CONFIGURED_FEATURES.register("blackstone_spire_naquadah",() ->
            new ConfiguredFeature<>(ORE_SPIRE_FEATURE, 
            		new SpireConfiguration.SpireConfigurationBuilder(
            				BlockStateProvider.simple(Blocks.BLACKSTONE), 
            				BlockStateProvider.simple(BlockInit.NAQUADAH_ORE.get()), 
            				TagInit.Blocks.STONE_SPIRE_PROTRUDES_THROUGH).build()));
	
	
	public static ResourceKey<ConfiguredFeature<?, ?>> createKey(String name)
    {
        return ResourceKey.create(Registry.CONFIGURED_FEATURE_REGISTRY, new ResourceLocation(StargateJourney.MODID, name));
    }
	
	public static void register(IEventBus eventBus)
	{
		FEATURES.register(eventBus);
		CONFIGURED_FEATURES.register(eventBus);
	}
}
