package net.povstalec.sgjourney;

import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.neoforge.registries.*;
import net.povstalec.sgjourney.client.Layers;
import net.povstalec.sgjourney.client.render.block_entity.*;
import net.povstalec.sgjourney.client.render.entity.PlasmaProjectileRenderer;
import net.povstalec.sgjourney.client.render.level.SGJourneyDimensionSpecialEffects;
import net.povstalec.sgjourney.client.render.level.StellarViewRendering;
import net.povstalec.sgjourney.client.resourcepack.ResourcepackReloadListener;
import net.povstalec.sgjourney.client.screens.*;
import net.povstalec.sgjourney.client.screens.config.ConfigScreen;
import net.povstalec.sgjourney.common.config.StargateJourneyConfig;
import net.povstalec.sgjourney.common.init.*;
import net.povstalec.sgjourney.common.items.properties.LiquidNaquadahPropertyFunction;
import net.povstalec.sgjourney.common.items.properties.WeaponStatePropertyFunction;
import net.povstalec.sgjourney.common.stargate.*;
import net.povstalec.sgjourney.common.world.biomemod.BiomeModifiers;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.CreativeModeTabs;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;

import java.util.Optional;
import java.util.function.BiFunction;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(StargateJourney.MODID)
public class StargateJourney
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "sgjourney";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "examplemod" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "examplemod" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "examplemod:example_block", combining the namespace and path
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // Creates a new BlockItem with the id "examplemod:example_block", combining the namespace and path
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // Creates a new food item with the id "examplemod:example_id", nutrition 1 and saturation 2
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // Creates a creative tab with the id "examplemod:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.examplemod")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public StargateJourney(IEventBus eventBus, ModContainer modContainer)
    {
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
        TabInit.register(eventBus);
        RecipeTypeInit.register(eventBus);
        StatisticsInit.register(eventBus);
    
        GalaxyInit.register(eventBus);
    
        AdvancementInit.register();
    
        eventBus.addListener((DataPackRegistryEvent.NewRegistry event) ->
        {
            //TODO Move Galaxy above Point of Origin
            // DON'T DELETE THIS COMMENT UNTIL I APPLY THE CHANGE TO OTHER VERSIONS OR I MIGHT FORGET
            event.dataPackRegistry(SymbolSet.REGISTRY_KEY, SymbolSet.CODEC, SymbolSet.CODEC);
            event.dataPackRegistry(Symbols.REGISTRY_KEY, Symbols.CODEC, Symbols.CODEC);
            event.dataPackRegistry(Galaxy.REGISTRY_KEY, Galaxy.CODEC, Galaxy.CODEC);
            event.dataPackRegistry(PointOfOrigin.REGISTRY_KEY, PointOfOrigin.CODEC, PointOfOrigin.CODEC);
            event.dataPackRegistry(SolarSystem.REGISTRY_KEY, SolarSystem.CODEC, SolarSystem.CODEC);
            event.dataPackRegistry(AddressTable.REGISTRY_KEY, AddressTable.CODEC, AddressTable.CODEC);
            event.dataPackRegistry(StargateVariant.REGISTRY_KEY, StargateVariant.CODEC, StargateVariant.CODEC);
        });
    
        eventBus.addListener(this::commonSetup);
        eventBus.addListener(Layers::registerLayers);
        eventBus.addListener(TabInit::addCreative);
    
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
            StatisticsInit.register();
            PacketHandlerInit.register();
            //VillagerInit.registerPOIs();
        });
    }
    
    // BECAUSE OCULUS MESSES WITH RENDERING TOO MUCH
    public static boolean isOculusLoaded()
    {
        if(isOculusLoaded.isEmpty())
            isOculusLoaded = Optional.of(ModList.get().isLoaded(OCULUS_MODID));
        
        return isOculusLoaded.get();
    }
    
    @Mod.EventBusSubscriber(modid = StargateJourney.MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            ItemProperties.register(ItemInit.VIAL.get(), new ResourceLocation(StargateJourney.MODID, "liquid_naquadah"), new LiquidNaquadahPropertyFunction());
            ItemProperties.register(ItemInit.MATOK.get(), new ResourceLocation(StargateJourney.MODID, "open"), new WeaponStatePropertyFunction());
            
            ItemBlockRenderTypes.setRenderLayer(FluidInit.LIQUID_NAQUADAH_SOURCE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(FluidInit.LIQUID_NAQUADAH_FLOWING.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(FluidInit.HEAVY_LIQUID_NAQUADAH_FLOWING.get(), RenderType.translucent());
            
            MenuScreens.register(MenuInit.INTERFACE.get(), InterfaceScreen::new);
            
            MenuScreens.register(MenuInit.RING_PANEL.get(), RingPanelScreen::new);
            
            MenuScreens.register(MenuInit.DHD_CRYSTAL.get(), DHDCrystalScreen::new);
            MenuScreens.register(MenuInit.MILKY_WAY_DHD.get(), MilkyWayDHDScreen::new);
            MenuScreens.register(MenuInit.PEGASUS_DHD.get(), PegasusDHDScreen::new);
            MenuScreens.register(MenuInit.CLASSIC_DHD.get(), ClassicDHDScreen::new);
            
            MenuScreens.register(MenuInit.NAQUADAH_GENERATOR.get(), NaquadahGeneratorScreen::new);
            
            MenuScreens.register(MenuInit.ZPM_HUB.get(), ZPMHubScreen::new);
            
            MenuScreens.register(MenuInit.NAQUADAH_LIQUIDIZER.get(), LiquidizerScreen.LiquidNaquadah::new);
            MenuScreens.register(MenuInit.HEAVY_NAQUADAH_LIQUIDIZER.get(), LiquidizerScreen.HeavyLiquidNaquadah::new);
            MenuScreens.register(MenuInit.CRYSTALLIZER.get(), CrystallizerScreen::new);
            
            EntityRenderers.register(EntityInit.JAFFA_PLASMA.get(), PlasmaProjectileRenderer::new);
            
            MenuScreens.register(MenuInit.TRANSCEIVER.get(), TransceiverScreen::new);
            
            //EntityRenderers.register(EntityInit.GOAULD.get(), GoauldRenderer::new);
            
            BlockEntityRenderers.register(BlockEntityInit.SANDSTONE_CARTOUCHE.get(), CartoucheRenderer.Sandstone::new);
            BlockEntityRenderers.register(BlockEntityInit.RED_SANDSTONE_CARTOUCHE.get(), CartoucheRenderer.RedSandstone::new);
            BlockEntityRenderers.register(BlockEntityInit.STONE_CARTOUCHE.get(), CartoucheRenderer.Stone::new);
            
            BlockEntityRenderers.register(BlockEntityInit.SANDSTONE_SYMBOL.get(), SymbolBlockRenderer.Sandstone::new);
            BlockEntityRenderers.register(BlockEntityInit.RED_SANDSTONE_SYMBOL.get(), SymbolBlockRenderer.RedSandstone::new);
            BlockEntityRenderers.register(BlockEntityInit.STONE_SYMBOL.get(), SymbolBlockRenderer.Stone::new);
            
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
                SGJourneyDimensionSpecialEffects.registerStargateJourneyEffects(event);
        }
        
        @SubscribeEvent
        public static void registerClientReloadListener(RegisterClientReloadListenersEvent event)
        {
            ResourcepackReloadListener.ReloadListener.registerReloadListener(event);
        }
    }
}
