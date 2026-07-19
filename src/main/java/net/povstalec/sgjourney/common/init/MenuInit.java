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

public class MenuInit 
{
	public static DeferredRegister<MenuType<?>> CONTAINERS = DeferredRegister.create(Registries.MENU, StargateJourney.MODID);
	
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
	
	public static final DeferredHolder<MenuType<?>, MenuType<ZPMHubMenu>> ZPM_HUB =
            registerMenuType(ZPMHubMenu::new, "zpm_hub");
	
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



    private static <T extends AbstractContainerMenu> DeferredHolder<MenuType<?>, MenuType<T>> registerMenuType(IContainerFactory<T> factory, String name)
    {
        return CONTAINERS.register(name, () -> IMenuTypeExtension.create(factory));
    }

	
	public static void register(IEventBus eventBus)
	{
        CONTAINERS.register(eventBus);
    }

}
