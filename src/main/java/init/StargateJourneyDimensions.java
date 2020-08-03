package init;

import net.minecraftforge.common.ModDimension;
import net.minecraftforge.fml.RegistryObject;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import woldericz_junior.stargatejourney.StargateJourney;
import woldericz_junior.stargatejourney.world.dimensions.StargateJourneyDimension;

public class StargateJourneyDimensions 
{
public static final DeferredRegister<ModDimension> MOD_DIMENSIONS = new DeferredRegister<>(ForgeRegistries.MOD_DIMENSIONS, StargateJourney.MODID);
	
	public static final RegistryObject<ModDimension> ABYDOS_DIMENSION = MOD_DIMENSIONS.register("abydos_dimension", () -> new StargateJourneyDimension());
}
