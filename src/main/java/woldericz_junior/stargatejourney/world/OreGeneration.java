package woldericz_junior.stargatejourney.world;

import init.StargateBlocks;
import init.StargateJourneyBiomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.feature.OreFeatureConfig.FillerBlockType;
import net.minecraft.world.gen.placement.ConfiguredPlacement;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;
import woldericz_junior.stargatejourney.config.OreGenConfig;

public class OreGeneration 
{
	public static void setupOreGeneration()
	{
		if(OreGenConfig.generate_abydos.get())
		{
			Biome biome = StargateJourneyBiomes.abydos_desert;
			ConfiguredPlacement<CountRangeConfig> naquadahConfig = Placement.COUNT_RANGE.configure(new CountRangeConfig(8, 31, 0, 127));
			biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,Feature.ORE.withConfiguration(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, StargateBlocks.naquadah_ore.getDefaultState(), OreGenConfig.naquadah_ore_chance.get())).withPlacement(naquadahConfig));
		}
		else if(OreGenConfig.generate_overworld.get())
		{
			for(Biome biome : ForgeRegistries.BIOMES)
			{
				ConfiguredPlacement<CountRangeConfig> naquadahConfig = Placement.COUNT_RANGE.configure(new CountRangeConfig(8, 31, 0, 127));
				biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES,Feature.ORE.withConfiguration(new OreFeatureConfig(FillerBlockType.NATURAL_STONE, StargateBlocks.naquadah_ore.getDefaultState(), OreGenConfig.naquadah_ore_chance.get())).withPlacement(naquadahConfig));
			}
			
		}
	}
}
