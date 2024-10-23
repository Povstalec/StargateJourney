package net.povstalec.sgjourney;

import com.mojang.blaze3d.shaders.FogShape;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Camera;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.renderer.FogRenderer;
import net.minecraft.client.renderer.ItemBlockRenderTypes;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderers;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraft.client.renderer.item.ItemProperties;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.Event;
import net.neoforged.fml.ModList;
import net.neoforged.fml.ModLoadingContext;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterClientReloadListenersEvent;
import net.neoforged.neoforge.client.event.RegisterDimensionSpecialEffectsEvent;
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent;
import net.neoforged.neoforge.client.extensions.common.IClientFluidTypeExtensions;
import net.neoforged.neoforge.client.extensions.common.RegisterClientExtensionsEvent;
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
import net.povstalec.sgjourney.common.fluids.NaquadahFluidType;
import net.povstalec.sgjourney.common.fluids.HeavyNaquadahFluidType;
import net.povstalec.sgjourney.common.init.*;
import net.povstalec.sgjourney.common.items.properties.LiquidNaquadahPropertyFunction;
import net.povstalec.sgjourney.common.items.properties.WeaponStatePropertyFunction;
import net.povstalec.sgjourney.common.stargate.*;
import net.povstalec.sgjourney.common.world.biomemod.BiomeModifiers;
import org.jetbrains.annotations.NotNull;
import org.joml.Vector3f;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;

import javax.annotation.Nullable;
import java.util.Optional;
import java.util.function.BiFunction;

// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(StargateJourney.MODID)
public class StargateJourney
{
    public static final String MODID = "sgjourney";
    public static final ResourceLocation EMPTY_LOCATION = ResourceLocation.fromNamespaceAndPath(MODID, "empty");
    public static final String EMPTY = EMPTY_LOCATION.toString();

    public static final String STELLAR_VIEW_MODID = "stellarview";
    public static final String OCULUS_MODID = "oculus";
    public static final String COMPUTERCRAFT_MODID = "computercraft";

    private static Optional<Boolean> isOculusLoaded = Optional.empty();

    public static final Logger LOGGER = LogUtils.getLogger();

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

        modContainer.registerConfig(ModConfig.Type.CLIENT, StargateJourneyConfig.CLIENT_CONFIG, "sgjourney-client.toml");
        modContainer.registerConfig(ModConfig.Type.COMMON, StargateJourneyConfig.COMMON_CONFIG, "sgjourney-common.toml");
    
        ModLoadingContext.get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(new BiFunction<Minecraft, Screen, Screen>()
                {
                    @Override
                    public Screen apply(Minecraft mc, Screen screen)
                    {
                        return new ConfigScreen(screen);
                    }
                }));

        NeoForge.EVENT_BUS.register(this);
        NeoForge.EVENT_BUS.addListener(MiscInit::registerCommands);
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
    
    @EventBusSubscriber(modid = StargateJourney.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {
        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            ItemProperties.register(ItemInit.VIAL.get(), sgjourneyLocation("liquid_naquadah"), new LiquidNaquadahPropertyFunction());
            ItemProperties.register(ItemInit.MATOK.get(), sgjourneyLocation("open"), new WeaponStatePropertyFunction());
            
            ItemBlockRenderTypes.setRenderLayer(FluidInit.LIQUID_NAQUADAH_SOURCE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(FluidInit.LIQUID_NAQUADAH_FLOWING.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(FluidInit.HEAVY_LIQUID_NAQUADAH_SOURCE.get(), RenderType.translucent());
            ItemBlockRenderTypes.setRenderLayer(FluidInit.HEAVY_LIQUID_NAQUADAH_FLOWING.get(), RenderType.translucent());

            EntityRenderers.register(EntityInit.JAFFA_PLASMA.get(), PlasmaProjectileRenderer::new);
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
        public static void registerMenuScreens(RegisterMenuScreensEvent event)
        {
            event.register(MenuInit.INTERFACE.get(), InterfaceScreen::new);

            event.register(MenuInit.RING_PANEL.get(), RingPanelScreen::new);

            event.register(MenuInit.DHD_CRYSTAL.get(), DHDCrystalScreen::new);
            event.register(MenuInit.MILKY_WAY_DHD.get(), MilkyWayDHDScreen::new);
            event.register(MenuInit.PEGASUS_DHD.get(), PegasusDHDScreen::new);
            event.register(MenuInit.CLASSIC_DHD.get(), ClassicDHDScreen::new);

            event.register(MenuInit.NAQUADAH_GENERATOR.get(), NaquadahGeneratorScreen::new);

            event.register(MenuInit.ZPM_HUB.get(), ZPMHubScreen::new);

            event.register(MenuInit.NAQUADAH_LIQUIDIZER.get(), LiquidizerScreen.LiquidNaquadah::new);
            event.register(MenuInit.HEAVY_NAQUADAH_LIQUIDIZER.get(), LiquidizerScreen.HeavyLiquidNaquadah::new);
            event.register(MenuInit.CRYSTALLIZER.get(), CrystallizerScreen::new);

            event.register(MenuInit.TRANSCEIVER.get(), TransceiverScreen::new);
        }

        @SubscribeEvent
        public static void registerClientExtensions(RegisterClientExtensionsEvent event)
        {
            event.registerFluidType(new IClientFluidTypeExtensions()
            {
                @Override
                public ResourceLocation getStillTexture()
                {
                    return NaquadahFluidType.STILL_TEXTURE;
                }

                @Override
                public ResourceLocation getFlowingTexture()
                {
                    return NaquadahFluidType.FLOWING_TEXTURE;
                }

                @Override
                public @Nullable ResourceLocation getOverlayTexture()
                {
                    return NaquadahFluidType.OVERLAY_TEXTURE;
                }

                @Override
                public int getTintColor()
                {
                    return 0xffb0f329;
                }

                @Override
                public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
                {
                    return new Vector3f(115.0F / 255.0F, 197.0F / 255.0F, 34.0F / 255.0F);
                }

                @Override
                public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape)
                {
                    RenderSystem.setShaderFogStart(1f);
                    RenderSystem.setShaderFogEnd(6f);
                }
            }, FluidTypeInit.LIQUID_NAQUADAH_FLUID_TYPE.get());

            event.registerFluidType(new IClientFluidTypeExtensions()
            {
                @Override
                public ResourceLocation getStillTexture()
                {
                    return HeavyNaquadahFluidType.STILL_TEXTURE;
                }

                @Override
                public ResourceLocation getFlowingTexture()
                {
                    return HeavyNaquadahFluidType.FLOWING_TEXTURE;
                }

                @Override
                public @Nullable ResourceLocation getOverlayTexture()
                {
                    return HeavyNaquadahFluidType.OVERLAY_TEXTURE;
                }

                @Override
                public int getTintColor()
                {
                    return 0xff096c00;
                }

                @Override
                public @NotNull Vector3f modifyFogColor(Camera camera, float partialTick, ClientLevel level, int renderDistance, float darkenWorldAmount, Vector3f fluidFogColor)
                {
                    return new Vector3f(115.0F / 255.0F, 197.0F / 255.0F, 34.0F / 255.0F);
                }

                @Override
                public void modifyFogRender(Camera camera, FogRenderer.FogMode mode, float renderDistance, float partialTick, float nearDistance, float farDistance, FogShape shape)
                {
                    RenderSystem.setShaderFogStart(1f);
                    RenderSystem.setShaderFogEnd(6f);
                }
            }, FluidTypeInit.HEAVY_LIQUID_NAQUADAH_FLUID_TYPE.get());
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

    public static ResourceLocation location(String path)
    {
        return ResourceLocation.withDefaultNamespace(path);
    }

    public static ResourceLocation location(String namespace, String path)
    {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    public static ResourceLocation sgjourneyLocation(String path)
    {
        return location(StargateJourney.MODID, path);
    }
}
