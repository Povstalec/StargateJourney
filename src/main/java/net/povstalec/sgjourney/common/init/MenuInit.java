package net.povstalec.sgjourney.common.init;

import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.common.extensions.IForgeMenuType;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.network.IContainerFactory;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.povstalec.sgjourney.StargateJourney;
import net.povstalec.sgjourney.common.block_entities.tech_interface.AdvancedCrystalInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.BasicInterfaceEntity;
import net.povstalec.sgjourney.common.block_entities.tech_interface.CrystalInterfaceEntity;
import net.povstalec.sgjourney.common.menu.*;
import net.povstalec.sgjourney.common.menu.dhd.DHDCrystalMenu;
import net.povstalec.sgjourney.common.menu.dhd.MilkyWayDHDMenu;
import net.povstalec.sgjourney.common.menu.dhd.PegasusDHDMenu;
import net.povstalec.sgjourney.common.menu.dhd.UniverseDHDMenu;
import net.povstalec.sgjourney.common.menu.graver.CartoucheEngravingMenu;
import net.povstalec.sgjourney.common.menu.graver.DHDEngravingMenu;
import net.povstalec.sgjourney.common.menu.graver.StargateEngravingMenu;
import net.povstalec.sgjourney.common.menu.graver.SymbolBlockEngravingMenu;

public class MenuInit 
{
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(ForgeRegistries.MENU_TYPES, StargateJourney.MODID);

	public static final RegistryObject<MenuType<InterfaceMenu<BasicInterfaceEntity>>> BASIC_INTERFACE =
            registerMenuType(InterfaceMenu.Basic::new, "basic_interface");
	public static final RegistryObject<MenuType<InterfaceMenu<CrystalInterfaceEntity>>> CRYSTAL_INTERFACE =
		registerMenuType(InterfaceMenu.Crystal::new, "crystal_interface");
	public static final RegistryObject<MenuType<InterfaceMenu<AdvancedCrystalInterfaceEntity>>> ADVANCED_CRYSTAL_INTERFACE =
			registerMenuType(InterfaceMenu.AdvancedCrystal::new, "advnaced_crystal_interface");
	
	public static final RegistryObject<MenuType<TransportRingsMenu.Ancient>> ANCIENT_TRANSPORT_RINGS =
			registerMenuType(TransportRingsMenu.Ancient::new, "ancient_transport_rings");
	public static final RegistryObject<MenuType<TransportRingsMenu.Goauld>> GOAULD_TRANSPORT_RINGS =
			registerMenuType(TransportRingsMenu.Goauld::new, "goauld_transport_rings");
	
	public static final RegistryObject<MenuType<RingPanelMenu.Protected>> RING_PANEL_PROTECTED = registerMenuType(RingPanelMenu.Protected::new, "ring_panel_protected");
	public static final RegistryObject<MenuType<RingPanelMenu.Unprotected>> RING_PANEL_UNPROTECTED = registerMenuType(RingPanelMenu.Unprotected::new, "ring_panel_unprotected");
	
	public static final RegistryObject<MenuType<UniverseDHDMenu>> UNIVERSE_DHD =
		registerMenuType(UniverseDHDMenu::new, "universe_dhd");
	public static final RegistryObject<MenuType<DHDCrystalMenu.Universe>> UNIVERSE_DHD_CRYSTAL =
		registerMenuType(DHDCrystalMenu.Universe::new, "universe_dhd_crystal");
	
	public static final RegistryObject<MenuType<MilkyWayDHDMenu>> MILKY_WAY_DHD =
            registerMenuType(MilkyWayDHDMenu::new, "milky_way_dhd");
	public static final RegistryObject<MenuType<DHDCrystalMenu.MilkyWay>> MILKY_WAY_DHD_CRYSTAL =
			registerMenuType(DHDCrystalMenu.MilkyWay::new, "milky_way_dhd_crystal");
	
	public static final RegistryObject<MenuType<PegasusDHDMenu>> PEGASUS_DHD =
            registerMenuType(PegasusDHDMenu::new, "pegasus_dhd");
	public static final RegistryObject<MenuType<DHDCrystalMenu.Pegasus>> PEGASUS_DHD_CRYSTAL =
			registerMenuType(DHDCrystalMenu.Pegasus::new, "pegasus_dhd_crystal");
	
	public static final RegistryObject<MenuType<ClassicDHDMenu>> CLASSIC_DHD =
            registerMenuType(ClassicDHDMenu::new, "classic_dhd");
	public static final RegistryObject<MenuType<DHDCrystalMenu.Classic>> CLASSIC_DHD_CRYSTAL =
			registerMenuType(DHDCrystalMenu.Classic::new, "classic_dhd_crystal");
	
	public static final RegistryObject<MenuType<NaquadahGeneratorMenu>> NAQUADAH_GENERATOR =
            registerMenuType(NaquadahGeneratorMenu::new, "naquadah_generator");
	
	public static final RegistryObject<MenuType<CrystallizerMenu.Crystallizer>> CRYSTALLIZER =
            registerMenuType(CrystallizerMenu.Crystallizer::new, "crystallizer");
	
	public static final RegistryObject<MenuType<CrystallizerMenu.AdvancedCrystallizer>> ADVANCED_CRYSTALLIZER =
			registerMenuType(CrystallizerMenu.AdvancedCrystallizer::new, "advanced_crystallizer");
	
	public static final RegistryObject<MenuType<LiquidizerMenu.LiquidNaquadah>> NAQUADAH_LIQUIDIZER =
            registerMenuType(LiquidizerMenu.LiquidNaquadah::new, "naquadah_liquidizer");
	
	public static final RegistryObject<MenuType<LiquidizerMenu.HeavyLiquidNaquadah>> HEAVY_NAQUADAH_LIQUIDIZER =
            registerMenuType(LiquidizerMenu.HeavyLiquidNaquadah::new, "heavy_naquadah_liquidizer");
	
	public static final RegistryObject<MenuType<TransceiverMenu>> TRANSCEIVER =
            registerMenuType(TransceiverMenu::new, "transceiver");
	
	public static final RegistryObject<MenuType<BatteryMenu>> NAQUADAH_BATTERY =
			registerMenuType(BatteryMenu::new, "naquadah_battery");
	
	// Engraving
	
	public static final RegistryObject<MenuType<CartoucheEngravingMenu.Stone>> ENGRAVING_STONE_CARTOUCHE =
		registerMenuType(CartoucheEngravingMenu.Stone::new, "engraving_stone_cartouche");
	public static final RegistryObject<MenuType<CartoucheEngravingMenu.Sandstone>> ENGRAVING_SANDSTONE_CARTOUCHE =
		registerMenuType(CartoucheEngravingMenu.Sandstone::new, "engraving_sandstone_cartouche");
	public static final RegistryObject<MenuType<CartoucheEngravingMenu.RedSandstone>> ENGRAVING_RED_SANDSTONE_CARTOUCHE =
		registerMenuType(CartoucheEngravingMenu.RedSandstone::new, "engraving_red_sandstone_cartouche");
	
	public static final RegistryObject<MenuType<SymbolBlockEngravingMenu.Stone>> ENGRAVING_STONE_SYMBOL =
		registerMenuType(SymbolBlockEngravingMenu.Stone::new, "engraving_stone_symbol");
	public static final RegistryObject<MenuType<SymbolBlockEngravingMenu.Sandstone>> ENGRAVING_SANDSTONE_SYMBOL =
		registerMenuType(SymbolBlockEngravingMenu.Sandstone::new, "engraving_sandstone_symbol");
	public static final RegistryObject<MenuType<SymbolBlockEngravingMenu.RedSandstone>> ENGRAVING_RED_SANDSTONE_SYMBOL =
		registerMenuType(SymbolBlockEngravingMenu.RedSandstone::new, "engraving_red_sandstone_symbol");
	
	public static final RegistryObject<MenuType<StargateEngravingMenu.Universe>> ENGRAVING_UNIVERSE_STARGATE =
		registerMenuType(StargateEngravingMenu.Universe::new, "engraving_universe_stargate");
	public static final RegistryObject<MenuType<StargateEngravingMenu.MilkyWay>> ENGRAVING_MILKY_WAY_STARGATE =
		registerMenuType(StargateEngravingMenu.MilkyWay::new, "engraving_milky_way_stargate");
	public static final RegistryObject<MenuType<StargateEngravingMenu.Classic>> ENGRAVING_CLASSIC_STARGATE =
		registerMenuType(StargateEngravingMenu.Classic::new, "engraving_classic_stargate");
	
	public static final RegistryObject<MenuType<DHDEngravingMenu.Universe>> ENGRAVING_UNIVERSE_DHD =
		registerMenuType(DHDEngravingMenu.Universe::new, "engraving_universe_dhd");
	public static final RegistryObject<MenuType<DHDEngravingMenu.MilkyWay>> ENGRAVING_MILKY_WAY_DHD =
		registerMenuType(DHDEngravingMenu.MilkyWay::new, "engraving_milky_way_dhd");
	public static final RegistryObject<MenuType<DHDEngravingMenu.Classic>> ENGRAVING_CLASSIC_DHD =
		registerMenuType(DHDEngravingMenu.Classic::new, "engraving_classic_dhd");



    private static <T extends AbstractContainerMenu>RegistryObject<MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name)
    {
        return MENUS.register(name, () -> IForgeMenuType.create(factory));
    }

	
	public static void register(IEventBus eventBus) 
	{
        MENUS.register(eventBus);
    }

}
