package net.povstalec.sgjourney.common.init;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.Holder;
import net.minecraft.core.HolderGetter;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.feature.ConfiguredFeature;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import net.povstalec.sgjourney.StargateJourney;

public class PlacedFeatureInit
{
	public static final ResourceKey<PlacedFeature> STONE_NAQUADAH_SPIRE_PLACED_KEY = createKey("stone_naquadah_spire");
	public static final ResourceKey<PlacedFeature> BLACKSTONE_NAQUADAH_SPIRE_PLACED_KEY = createKey("blackstone_naquadah_spire");

    public static void bootstrap(BootstapContext<PlacedFeature> context)
    {
        HolderGetter<ConfiguredFeature<?, ?>> configuredFeatures = context.lookup(Registries.CONFIGURED_FEATURE);
        
        register(context, STONE_NAQUADAH_SPIRE_PLACED_KEY, configuredFeatures.getOrThrow(FeatureInit.STONE_NAQUADAH_SPIRE_KEY), 
        		oreSpirePlacement(PlacementUtils.countExtra(0, 0.01F, 1)));
        register(context, BLACKSTONE_NAQUADAH_SPIRE_PLACED_KEY, configuredFeatures.getOrThrow(FeatureInit.BLACKSTONE_NAQUADAH_SPIRE_KEY), 
        		oreSpirePlacement(PlacementUtils.countExtra(0, 0.01F, 1)));
    }


    private static ResourceKey<PlacedFeature> createKey(String name)
    {
        return ResourceKey.create(Registries.PLACED_FEATURE, new ResourceLocation(StargateJourney.MODID, name));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, 
    		Holder<ConfiguredFeature<?, ?>> configuration, List<PlacementModifier> modifiers)
    {
        context.register(key, new PlacedFeature(configuration, List.copyOf(modifiers)));
    }

    private static void register(BootstapContext<PlacedFeature> context, ResourceKey<PlacedFeature> key, 
    		Holder<ConfiguredFeature<?, ?>> configuration, PlacementModifier... modifiers)
    {
        register(context, key, configuration, List.of(modifiers));
    }
    
	//============================================================================================
	//******************************************Placement*****************************************
	//============================================================================================
    
    private static final PlacementModifier ORE_SPIRE_TRESHOLD = SurfaceWaterDepthFilter.forMaxDepth(0);
    
	private static List<PlacementModifier> oreSpirePlacement(PlacementModifier modifier)
	{
    	return ImmutableList.<PlacementModifier>builder().add(modifier).add(InSquarePlacement.spread()).add(ORE_SPIRE_TRESHOLD).add(PlacementUtils.HEIGHTMAP_OCEAN_FLOOR).add(BiomeFilter.biome()).build();
    }
}
