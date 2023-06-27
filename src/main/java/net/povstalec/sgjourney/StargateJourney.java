package net.povstalec.sgjourney;

import java.util.function.BiFunction;

import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.ConfigScreenHandler;
import net.minecraftforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.registries.NewRegistryEvent;
import net.minecraftforge.registries.RegistryBuilder;
import net.povstalec.sgjourney.client.Layers;
import net.povstalec.sgjourney.client.render.block_entity.ClassicStargateRenderer;
import net.povstalec.sgjourney.client.render.block_entity.MilkyWayStargateRenderer;
import net.povstalec.sgjourney.client.render.block_entity.PegasusStargateRenderer;
import net.povstalec.sgjourney.client.render.block_entity.SandstoneSymbolRenderer;
import net.povstalec.sgjourney.client.render.block_entity.StoneSymbolRenderer;
import net.povstalec.sgjourney.client.render.block_entity.TollanStargateRenderer;
import net.povstalec.sgjourney.client.render.block_entity.TransportRingsRenderer;
import net.povstalec.sgjourney.client.render.block_entity.UniverseStargateRenderer;
import net.povstalec.sgjourney.client.render.entity.GoauldRenderer;
import net.povstalec.sgjourney.client.render.entity.PlasmaProjectileRenderer;
import net.povstalec.sgjourney.client.render.level.SGJourneyDimensionSpecialEffects;
import net.povstalec.sgjourney.client.render.level.StellarViewRendering;
import net.povstalec.sgjourney.client.screens.BasicInterfaceScreen;
import net.povstalec.sgjourney.client.screens.ClassicDHDScreen;
import net.povstalec.sgjourney.client.screens.CrystalInterfaceScreen;
import net.povstalec.sgjourney.client.screens.DHDCrystalScreen;
import net.povstalec.sgjourney.client.screens.MilkyWayDHDScreen;
import net.povstalec.sgjourney.client.screens.NaquadahGeneratorScreen;
import net.povstalec.sgjourney.client.screens.PegasusDHDScreen;
import net.povstalec.sgjourney.client.screens.RingPanelScreen;
import net.povstalec.sgjourney.client.screens.ZPMHubScreen;
import net.povstalec.sgjourney.client.screens.config.ConfigScreen;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.init.BlockEntityInit;
import net.povstalec.sgjourney.common.init.BlockInit;
import net.povstalec.sgjourney.common.init.EntityInit;
import net.povstalec.sgjourney.common.init.FeatureInit;
import net.povstalec.sgjourney.common.init.FluidInit;
import net.povstalec.sgjourney.common.init.FluidTypeInit;
import net.povstalec.sgjourney.common.init.GalaxyInit;
import net.povstalec.sgjourney.common.init.ItemInit;
import net.povstalec.sgjourney.common.init.MenuInit;
import net.povstalec.sgjourney.common.init.MiscInit;
import net.povstalec.sgjourney.common.init.PacketHandlerInit;
import net.povstalec.sgjourney.common.init.SoundInit;
import net.povstalec.sgjourney.common.init.StructureInit;
import net.povstalec.sgjourney.common.init.VillagerInit;
import net.povstalec.sgjourney.common.items.properties.LiquidNaquadahPropertyFunction;
import net.povstalec.sgjourney.common.items.properties.WeaponStatePropertyFunction;
import net.povstalec.sgjourney.common.stargate.AddressTable;
import net.povstalec.sgjourney.common.stargate.Galaxy;
import net.povstalec.sgjourney.common.stargate.PointOfOrigin;
import net.povstalec.sgjourney.common.stargate.SolarSystem;
import net.povstalec.sgjourney.common.stargate.SymbolSet;
import net.povstalec.sgjourney.common.stargate.Symbols;
import net.povstalec.sgjourney.common.world.biomemod.BiomeModifiers;

@Mod(StargateJourney.MODID)
public class StargateJourney
{
    public static final String MODID = "sgjourney";
    public static final String EMPTY = MODID + ":empty";
    
    public static final String STELLAR_VIEW_MODID = "stellarview";
    
    public static final Logger LOGGER = LogUtils.getLogger();

    public StargateJourney()
    {
    	IEventBus eventBus = FMLJavaModLoadingContext.get().getModEventBus();
    	
    	ItemInit.register(eventBus);
        BlockInit.register(eventBus);
        FluidInit.register(eventBus);
        FluidTypeInit.register(eventBus);
        BlockEntityInit.register(eventBus);
        MenuInit.register(eventBus);
        VillagerInit.register(eventBus);
        FeatureInit.register(eventBus);
        StructureInit.register(eventBus);
        BiomeModifiers.register(eventBus);
        EntityInit.register(eventBus);
        SoundInit.register(eventBus);
        
        GalaxyInit.register(eventBus);
        
        eventBus.addListener((NewRegistryEvent event) -> 
        {
        	
        	RegistryBuilder<SymbolSet> symbolSetBuilder = new RegistryBuilder<>();
        	symbolSetBuilder.setName(new ResourceLocation(MODID, "symbol_set"));
        	event.create(symbolSetBuilder);
        	
        	RegistryBuilder<Symbols> symbolBuilder = new RegistryBuilder<>();
        	symbolBuilder.setName(new ResourceLocation(MODID, "symbols"));
        	event.create(symbolBuilder);

        	RegistryBuilder<PointOfOrigin> pointOfOriginBuilder = new RegistryBuilder<>();
        	pointOfOriginBuilder.setName(new ResourceLocation(MODID, "point_of_origin"));
        	event.create(pointOfOriginBuilder);

        	RegistryBuilder<SolarSystem> solarSystemBuilder = new RegistryBuilder<>();
        	solarSystemBuilder.setName(new ResourceLocation(MODID, "solar_system"));
        	event.create(solarSystemBuilder);

        	RegistryBuilder<Galaxy> galaxyBuilder = new RegistryBuilder<>();
        	galaxyBuilder.setName(new ResourceLocation(MODID, "galaxy"));
        	event.create(galaxyBuilder);

        	RegistryBuilder<AddressTable> addressTableBuilder = new RegistryBuilder<>();
        	addressTableBuilder.setName(new ResourceLocation(MODID, "address_table"));
        	event.create(addressTableBuilder);

        	symbolSetBuilder.dataPackRegistry(SymbolSet.CODEC, SymbolSet.CODEC);
        	symbolBuilder.dataPackRegistry(Symbols.CODEC, Symbols.CODEC);
        	pointOfOriginBuilder.dataPackRegistry(PointOfOrigin.CODEC, PointOfOrigin.CODEC);
        	solarSystemBuilder.dataPackRegistry(SolarSystem.CODEC, SolarSystem.CODEC);
        	galaxyBuilder.dataPackRegistry(Galaxy.CODEC, Galaxy.CODEC);
        	addressTableBuilder.dataPackRegistry(AddressTable.CODEC, AddressTable.CODEC);
        });
        
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(Layers::registerLayers);
		
		ModLoadingContext.get().registerConfig(ModConfig.Type.CLIENT, StargateJourneyConfig.CLIENT_CONFIG, "sgjourney-client.toml");
		ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, StargateJourneyConfig.COMMON_CONFIG, "sgjourney-common.toml");
		
		ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class, 
				() -> new ConfigScreenHandler.ConfigScreenFactory(new BiFunction<Minecraft, Screen, Screen>()
				{
					@Override
					public Screen apply(Minecraft mc, Screen screen)
					{
						return new ConfigScreen(screen);
					}
				}));
        
		MinecraftForge.EVENT_BUS.register(this);
		MinecraftForge.EVENT_BUS.addListener(MiscInit::registerCommands);
    }
    
    private void commonSetup(final FMLCommonSetupEvent event)
    {
    	event.enqueueWork(() -> 
    	{
    		PacketHandlerInit.register();
    		VillagerInit.registerPOIs();
    	});
    }

    @Mod.EventBusSubscriber(modid = StargateJourney.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
        	ItemProperties.register(ItemInit.LIQUID_NAQUADAH_BOTTLE.get(), new ResourceLocation(StargateJourney.MODID, "liquid_naquadah"), new LiquidNaquadahPropertyFunction());
        	ItemProperties.register(ItemInit.MATOK.get(), new ResourceLocation(StargateJourney.MODID, "open"), new WeaponStatePropertyFunction());
        	
            ItemBlockRenderTypes.setRenderLayer(FluidInit.LIQUID_NAQUADAH_SOURCE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(FluidInit.LIQUID_NAQUADAH_FLOWING.get(), RenderType.translucent());

        	MenuScreens.register(MenuInit.BASIC_INTERFACE.get(), BasicInterfaceScreen::new);
        	MenuScreens.register(MenuInit.CRYSTAL_INTERFACE.get(), CrystalInterfaceScreen::new);
            
        	MenuScreens.register(MenuInit.RING_PANEL.get(), RingPanelScreen::new);

        	MenuScreens.register(MenuInit.DHD_CRYSTAL.get(), DHDCrystalScreen::new);
        	MenuScreens.register(MenuInit.MILKY_WAY_DHD.get(), MilkyWayDHDScreen::new);
        	MenuScreens.register(MenuInit.PEGASUS_DHD.get(), PegasusDHDScreen::new);
        	MenuScreens.register(MenuInit.CLASSIC_DHD.get(), ClassicDHDScreen::new);

        	MenuScreens.register(MenuInit.NAQUADAH_GENERATOR.get(), NaquadahGeneratorScreen::new);

        	MenuScreens.register(MenuInit.ZPM_HUB.get(), ZPMHubScreen::new);
        	
        	EntityRenderers.register(EntityInit.JAFFA_PLASMA.get(), PlasmaProjectileRenderer::new);
        	
        	EntityRenderers.register(EntityInit.GOAULD.get(), GoauldRenderer::new);
        	
        	BlockEntityRenderers.register(BlockEntityInit.SANDSTONE_SYMBOL.get(), SandstoneSymbolRenderer::new);
        	BlockEntityRenderers.register(BlockEntityInit.STONE_SYMBOL.get(), StoneSymbolRenderer::new);
        	
        	BlockEntityRenderers.register(BlockEntityInit.TRANSPORT_RINGS.get(), TransportRingsRenderer::new);

        	BlockEntityRenderers.register(BlockEntityInit.UNIVERSE_STARGATE.get(), UniverseStargateRenderer::new);
        	BlockEntityRenderers.register(BlockEntityInit.MILKY_WAY_STARGATE.get(), MilkyWayStargateRenderer::new);
        	BlockEntityRenderers.register(BlockEntityInit.PEGASUS_STARGATE.get(), PegasusStargateRenderer::new);
        	BlockEntityRenderers.register(BlockEntityInit.CLASSIC_STARGATE.get(), ClassicStargateRenderer::new);
        	BlockEntityRenderers.register(BlockEntityInit.TOLLAN_STARGATE.get(), TollanStargateRenderer::new);
        }
        
        @SubscribeEvent
        public static void registerDimensionEffects(RegisterDimensionSpecialEffectsEvent event)
        {
        	if(ModList.get().isLoaded(STELLAR_VIEW_MODID))
        		StellarViewRendering.registerStellarViewEffects(event);
        	else
        	{
        		event.register(SGJourneyDimensionSpecialEffects.ABYDOS_EFFECTS, new SGJourneyDimensionSpecialEffects.Abydos());
            	event.register(SGJourneyDimensionSpecialEffects.CHULAK_EFFECTS, new SGJourneyDimensionSpecialEffects.Chulak());
            	event.register(SGJourneyDimensionSpecialEffects.LANTEA_EFFECTS, new SGJourneyDimensionSpecialEffects.Lantea());
        	}
        }
    }
    
}
