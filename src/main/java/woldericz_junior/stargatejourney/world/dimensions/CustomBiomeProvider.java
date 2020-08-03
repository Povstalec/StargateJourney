package woldericz_junior.stargatejourney.world.dimensions;

import java.util.Random;
import java.util.Set;

import com.google.common.collect.ImmutableSet;

import init.StargateJourneyBiomes;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.biome.provider.BiomeProvider;

public class CustomBiomeProvider extends BiomeProvider 
{
	private Random rand;
	
	public CustomBiomeProvider() {
		super(biomeList);
		rand = new Random();
	}
	
	private static final Set<Biome> biomeList = ImmutableSet.of(StargateJourneyBiomes.abydos_desert, StargateJourneyBiomes.abydos_desert_hills);

	@Override
	public Biome getNoiseBiome(int arg0, int arg1, int arg2) {
		return StargateJourneyBiomes.abydos_desert;
	}
	
}
