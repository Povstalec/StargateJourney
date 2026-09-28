package net.povstalec.sgjourney.common.init;

import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
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
	public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(Registries.MENU, StargateJourney.MODID);
	
	public static final DeferredHolder<MenuType<?>, MenuType<InterfaceMenu<BasicInterfaceEntity>>> BASIC_INTERFACE =
            registerMenuType(InterfaceMenu.Basic::new, "basic_interface");
	public static final DeferredHolder<MenuType<?>, MenuType<InterfaceMenu<CrystalInterfaceEntity>>> CRYSTAL_INTERFACE =
		registerMenuType(InterfaceMenu.Crystal::new, "crystal_interface");
	public static final DeferredHolder<MenuType<?>, MenuType<InterfaceMenu<AdvancedCrystalInterfaceEntity>>> ADVANCED_CRYSTAL_INTERFACE =
			registerMenuType(InterfaceMenu.AdvancedCrystal::new, "advnaced_crystal_interface");
	
	public static final DeferredHolder<MenuType<?>, MenuType<TransportRingsMenu.Ancient>> ANCIENT_TRANSPORT_RINGS =
			registerMenuType(TransportRingsMenu.Ancient::new, "ancient_transport_rings");
	public static final DeferredHolder<MenuType<?>, MenuType<TransportRingsMenu.Goauld>> GOAULD_TRANSPORT_RINGS =
			registerMenuType(TransportRingsMenu.Goauld::new, "goauld_transport_rings");
	
	public static final DeferredHolder<MenuType<?>, MenuType<RingPanelMenu.Protected>> RING_PANEL_PROTECTED = registerMenuType(RingPanelMenu.Protected::new, "ring_panel_protected");
	public static final DeferredHolder<MenuType<?>, MenuType<RingPanelMenu.Unprotected>> RING_PANEL_UNPROTECTED = registerMenuType(RingPanelMenu.Unprotected::new, "ring_panel_unprotected");
	
	public static final DeferredHolder<MenuType<?>, MenuType<UniverseDHDMenu>> UNIVERSE_DHD =
		registerMenuType(UniverseDHDMenu::new, "universe_dhd");
	public static final DeferredHolder<MenuType<?>, MenuType<DHDCrystalMenu.Universe>> UNIVERSE_DHD_CRYSTAL =
		registerMenuType(DHDCrystalMenu.Universe::new, "universe_dhd_crystal");
	
	public static final DeferredHolder<MenuType<?>, MenuType<MilkyWayDHDMenu>> MILKY_WAY_DHD =
            registerMenuType(MilkyWayDHDMenu::new, "milky_way_dhd");
	public static final DeferredHolder<MenuType<?>, MenuType<DHDCrystalMenu.MilkyWay>> MILKY_WAY_DHD_CRYSTAL =
			registerMenuType(DHDCrystalMenu.MilkyWay::new, "milky_way_dhd_crystal");
	
	public static final DeferredHolder<MenuType<?>, MenuType<PegasusDHDMenu>> PEGASUS_DHD =
            registerMenuType(PegasusDHDMenu::new, "pegasus_dhd");
	public static final DeferredHolder<MenuType<?>, MenuType<DHDCrystalMenu.Pegasus>> PEGASUS_DHD_CRYSTAL =
			registerMenuType(DHDCrystalMenu.Pegasus::new, "pegasus_dhd_crystal");
	
	public static final DeferredHolder<MenuType<?>, MenuType<ClassicDHDMenu>> CLASSIC_DHD =
            registerMenuType(ClassicDHDMenu::new, "classic_dhd");
	public static final DeferredHolder<MenuType<?>, MenuType<DHDCrystalMenu.Classic>> CLASSIC_DHD_CRYSTAL =
			registerMenuType(DHDCrystalMenu.Classic::new, "classic_dhd_crystal");
	
	public static final DeferredHolder<MenuType<?>, MenuType<NaquadahGeneratorMenu>> NAQUADAH_GENERATOR =
            registerMenuType(NaquadahGeneratorMenu::new, "naquadah_generator");
	
	public static final DeferredHolder<MenuType<?>, MenuType<CrystallizerMenu.Crystallizer>> CRYSTALLIZER =
            registerMenuType(CrystallizerMenu.Crystallizer::new, "crystallizer");
	
	public static final DeferredHolder<MenuType<?>, MenuType<CrystallizerMenu.AdvancedCrystallizer>> ADVANCED_CRYSTALLIZER =
			registerMenuType(CrystallizerMenu.AdvancedCrystallizer::new, "advanced_crystallizer");
	
	public static final DeferredHolder<MenuType<?>, MenuType<LiquidizerMenu.LiquidNaquadah>> NAQUADAH_LIQUIDIZER =
            registerMenuType(LiquidizerMenu.LiquidNaquadah::new, "naquadah_liquidizer");
	
	public static final DeferredHolder<MenuType<?>, MenuType<LiquidizerMenu.HeavyLiquidNaquadah>> HEAVY_NAQUADAH_LIQUIDIZER =
            registerMenuType(LiquidizerMenu.HeavyLiquidNaquadah::new, "heavy_naquadah_liquidizer");
	
	public static final DeferredHolder<MenuType<?>, MenuType<TransceiverMenu>> TRANSCEIVER =
            registerMenuType(TransceiverMenu::new, "transceiver");
	
	public static final DeferredHolder<MenuType<?>, MenuType<BatteryMenu>> NAQUADAH_BATTERY =
			registerMenuType(BatteryMenu::new, "naquadah_battery");
	
	// Engraving
	
	public static final DeferredHolder<MenuType<?>, MenuType<CartoucheEngravingMenu.Stone>> ENGRAVING_STONE_CARTOUCHE =
		registerMenuType(CartoucheEngravingMenu.Stone::new, "engraving_stone_cartouche");
	public static final DeferredHolder<MenuType<?>, MenuType<CartoucheEngravingMenu.Sandstone>> ENGRAVING_SANDSTONE_CARTOUCHE =
		registerMenuType(CartoucheEngravingMenu.Sandstone::new, "engraving_sandstone_cartouche");
	public static final DeferredHolder<MenuType<?>, MenuType<CartoucheEngravingMenu.RedSandstone>> ENGRAVING_RED_SANDSTONE_CARTOUCHE =
		registerMenuType(CartoucheEngravingMenu.RedSandstone::new, "engraving_red_sandstone_cartouche");
	
	public static final DeferredHolder<MenuType<?>, MenuType<SymbolBlockEngravingMenu.Stone>> ENGRAVING_STONE_SYMBOL =
		registerMenuType(SymbolBlockEngravingMenu.Stone::new, "engraving_stone_symbol");
	public static final DeferredHolder<MenuType<?>, MenuType<SymbolBlockEngravingMenu.Sandstone>> ENGRAVING_SANDSTONE_SYMBOL =
		registerMenuType(SymbolBlockEngravingMenu.Sandstone::new, "engraving_sandstone_symbol");
	public static final DeferredHolder<MenuType<?>, MenuType<SymbolBlockEngravingMenu.RedSandstone>> ENGRAVING_RED_SANDSTONE_SYMBOL =
		registerMenuType(SymbolBlockEngravingMenu.RedSandstone::new, "engraving_red_sandstone_symbol");
	
	public static final DeferredHolder<MenuType<?>, MenuType<StargateEngravingMenu.Universe>> ENGRAVING_UNIVERSE_STARGATE =
		registerMenuType(StargateEngravingMenu.Universe::new, "engraving_universe_stargate");
	public static final DeferredHolder<MenuType<?>, MenuType<StargateEngravingMenu.MilkyWay>> ENGRAVING_MILKY_WAY_STARGATE =
		registerMenuType(StargateEngravingMenu.MilkyWay::new, "engraving_milky_way_stargate");
	public static final DeferredHolder<MenuType<?>, MenuType<StargateEngravingMenu.Classic>> ENGRAVING_CLASSIC_STARGATE =
		registerMenuType(StargateEngravingMenu.Classic::new, "engraving_classic_stargate");
	
	public static final DeferredHolder<MenuType<?>, MenuType<DHDEngravingMenu.Universe>> ENGRAVING_UNIVERSE_DHD =
		registerMenuType(DHDEngravingMenu.Universe::new, "engraving_universe_dhd");
	public static final DeferredHolder<MenuType<?>, MenuType<DHDEngravingMenu.MilkyWay>> ENGRAVING_MILKY_WAY_DHD =
		registerMenuType(DHDEngravingMenu.MilkyWay::new, "engraving_milky_way_dhd");
	public static final DeferredHolder<MenuType<?>, MenuType<DHDEngravingMenu.Classic>> ENGRAVING_CLASSIC_DHD =
		registerMenuType(DHDEngravingMenu.Classic::new, "engraving_classic_dhd");



    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name)
    {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }

	
	public static void register(IEventBus eventBus)
	{
        MENUS.register(eventBus);
    }

}
