package woldericz_junior.stargatejourney.world.biomes;

import net.minecraft.block.Blocks;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.DefaultBiomeFeatures;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.carver.WorldCarver;
import net.minecraft.world.gen.feature.ProbabilityConfig;
import net.minecraft.world.gen.surfacebuilders.ConfiguredSurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilder;
import net.minecraft.world.gen.surfacebuilders.SurfaceBuilderConfig;
import woldericz_junior.stargatejourney.StargateJourneyRegistries;

public class AbydosDesert extends Biome 
{

	public AbydosDesert() 
	{
		super((new Biome.Builder())
				.surfaceBuilder(new ConfiguredSurfaceBuilder<SurfaceBuilderConfig>(SurfaceBuilder.DEFAULT, new SurfaceBuilderConfig(Blocks.SAND.getDefaultState(), Blocks.SAND.getDefaultState(), Blocks.SAND.getDefaultState())))
				.precipitation(RainType.NONE)
				.category(Category.DESERT)
				.downfall(0.0f)
				.depth(0.125f)
				.scale(0.05f)
				.temperature(2.0f)
				.waterColor(4159204)
				.waterFogColor(329011)
				.parent((null)));
		
		DefaultBiomeFeatures.addCarvers(this);
		this.addCarver(GenerationStage.Carving.AIR, Biome.createCarver(WorldCarver.CAVE, new ProbabilityConfig(0.014285715F)));
	    this.addCarver(GenerationStage.Carving.AIR, Biome.createCarver(WorldCarver.CANYON, new ProbabilityConfig(0.002F)));
		DefaultBiomeFeatures.addOres(this);
		DefaultBiomeFeatures.addStoneVariants(this);
		
		this.setRegistryName(StargateJourneyRegistries.location("abydos_desert"));
	}

}
