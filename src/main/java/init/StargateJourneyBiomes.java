package init;

import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.common.BiomeManager;

public class StargateJourneyBiomes 
{
	public static Biome abydos_desert;
	public static Biome abydos_desert_hills;
	
	public static void registerBiomes()
	{
		registerBiome(abydos_desert, Type.DRY, Type.SANDY);
		registerBiome(abydos_desert_hills, Type.DRY, Type.SANDY);
	}
	
	public static void registerBiome(Biome biome, Type... type)
	{
		BiomeDictionary.addTypes(biome, type);
		BiomeManager.addSpawnBiome(biome);
	}

}
