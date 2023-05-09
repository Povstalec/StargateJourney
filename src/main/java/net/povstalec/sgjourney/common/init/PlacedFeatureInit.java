package net.povstalec.sgjourney.common.init;

import java.util.List;

import com.google.common.collect.ImmutableList;

import net.minecraft.core.Registry;
import net.minecraft.data.worldgen.placement.PlacementUtils;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.levelgen.placement.BiomeFilter;
import net.minecraft.world.level.levelgen.placement.InSquarePlacement;
import net.minecraft.world.level.levelgen.placement.PlacedFeature;
import net.minecraft.world.level.levelgen.placement.PlacementModifier;
import net.minecraft.world.level.levelgen.placement.SurfaceWaterDepthFilter;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;

public class PlacedFeatureInit
{
	public static final DeferredRegister<PlacedFeature> PLACED_FEATURES = DeferredRegister.create(Registry.PLACED_FEATURE_REGISTRY, StargateJourney.MODID);
	
	public static final ResourceKey<PlacedFeature> STONE_NAQUADAH_SPIRE_PLACED_KEY = createKey("stone_naquadah_spire");
	public static final ResourceKey<PlacedFeature> BLACKSTONE_NAQUADAH_SPIRE_PLACED_KEY = createKey("blackstone_naquadah_spire");
    public static final RegistryObject<PlacedFeature> STONE_NAQUADAH_SPIRE_PLACED = PLACED_FEATURES.register("stone_naquadah_spire",
    		() -> new PlacedFeature(FeatureInit.STONE_NAQUADAH_SPIRE.getHolder().get(), oreSpirePlacement(PlacementUtils.countExtra(0, 0.01F, 1))))));
    	    public static final RegistryObject<PlacedFeature> BLACKSTONE_NAQUADAH_SPIRE_PLACED = PLACED_FEATURES.register("blackstone_naquadah_spire",
    	    		() -> new PlacedFeature(FeatureInit.BLACKSTONE_NAQUADAH_SPIRE.getHolder().get(), oreSpirePlacement(PlacementUtils.countExtra(0, 0.01F, 1))))));

    private static ResourceKey<PlacedFeature> createKey(String name)
    {
        return ResourceKey.create(Registry.PLACED_FEATURE_REGISTRY, new ResourceLocation(StargateJourney.MODID, name));
    }
    
	//============================================================================================
	//******************************************Placement*****************************************
	//============================================================================================
    
    private static final PlacementModifier ORE_SPIRE_TRESHOLD = SurfaceWaterDepthFilter.forMaxDepth(0);
    
	private static List<PlacementModifier> oreSpirePlacement(PlacementModifier modifier)
	{
    	return ImmutableList.<PlacementModifier>builder().add(modifier).add(InSquarePlacement.spread()).add(ORE_SPIRE_TRESHOLD).add(PlacementUtils.HEIGHTMAP_OCEAN_FLOOR).add(BiomeFilter.biome()).build();
    }
	
	public static void register(IEventBus eventBus)
	{
		PLACED_FEATURES.register(eventBus);
	}
}
