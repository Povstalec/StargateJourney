package woldericz_junior.stargatejourney;

import java.util.HashMap;
import java.util.Map;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import init.StargateBlocks;
import init.StargateItems;
import init.StargateJourneyDimensions;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.RenderTypeLookup;
import net.minecraft.client.renderer.entity.model.BipedModel;
import net.minecraft.item.Item;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.loading.FMLPaths;
import woldericz_junior.stargatejourney.config.Config;
import woldericz_junior.stargatejourney.models.HorusArmorModel;
import woldericz_junior.stargatejourney.models.JackalArmorModel;
import woldericz_junior.stargatejourney.setup.ClientProxy;
import woldericz_junior.stargatejourney.setup.IProxy;
import woldericz_junior.stargatejourney.setup.ModSetup;
import woldericz_junior.stargatejourney.setup.ServerProxy;
import woldericz_junior.stargatejourney.world.OreGeneration;

@Mod("sgjourney")
public class StargateJourney 
{
	public static IProxy proxy = DistExecutor.runForDist(() -> () -> new ClientProxy(), () -> () -> new ServerProxy());
	
	public static ModSetup setup = new ModSetup();
	
	public static StargateJourney instance;
	public static final String MODID = "sgjourney";
	public static final Logger LOGGER = LogManager.getLogger(MODID);
	
	public StargateJourney() 
	{
		instance = this;
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.server_config);
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, Config.client_config);
		
		final IEventBus sgjourneyEventBus = FMLJavaModLoadingContext.get().getModEventBus();
		
		sgjourneyEventBus.addListener(this::setup);
		sgjourneyEventBus.addListener(this::clientRegistries);
		
		StargateJourneyDimensions.MOD_DIMENSIONS.register(sgjourneyEventBus);
		
		Config.loadConfig(Config.client_config, FMLPaths.CONFIGDIR.get().resolve("sgjourney-client.toml").toString());
		Config.loadConfig(Config.server_config, FMLPaths.CONFIGDIR.get().resolve("sgjourney-server.toml").toString());
		
		MinecraftForge.EVENT_BUS.register(this);
	}
	
	private void setup(final FMLCommonSetupEvent event)
	{
		setup.init();
		proxy.init();
		LOGGER.info("Setup method registered.");
		OreGeneration.setupOreGeneration();
	}
	
	@SuppressWarnings("rawtypes")
	public static final Map<Item, BipedModel> armorModels = new HashMap<Item, BipedModel>();
	
	private void clientRegistries(final FMLClientSetupEvent event)
	{
		RenderTypeLookup.setRenderLayer(StargateBlocks.movie_stargate, RenderType.getCutout());
		RenderTypeLookup.setRenderLayer(StargateBlocks.fire_pit, RenderType.getCutout());
		
        JackalArmorModel jackalArmor = new JackalArmorModel(1.0F);
        HorusArmorModel horusArmor = new HorusArmorModel(1.0F);
        
		armorModels.put(StargateItems.jackal_helmet, jackalArmor);
		armorModels.put(StargateItems.horus_helmet, horusArmor);
		LOGGER.info("Client method registered.");
	}
}