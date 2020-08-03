package woldericz_junior.stargatejourney;

import init.StargateJourneyDimensions;
import net.minecraft.world.dimension.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.world.RegisterDimensionsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber.Bus;

@Mod.EventBusSubscriber(modid = StargateJourney.MODID, bus = Bus.FORGE)
public class ForgeEventBusSubscriber 
{
	@SubscribeEvent
	public static void registerDimensions(final RegisterDimensionsEvent event)
	{
		if(DimensionType.byName(StargateJourneyRegistries.ABYDOS_DIM_TYPE) == null)
		{
			DimensionManager.registerDimension(StargateJourneyRegistries.ABYDOS_DIM_TYPE, StargateJourneyDimensions.ABYDOS_DIMENSION.get(), null, true);
		}
		StargateJourney.LOGGER.info("Dimensions registered");
	}
}
